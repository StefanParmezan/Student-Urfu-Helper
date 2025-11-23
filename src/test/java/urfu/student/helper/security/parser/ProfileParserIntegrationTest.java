package urfu.student.helper.security.parser;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;
import urfu.student.helper.security.dto.CourseDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ProfileParserTest {

    private ProfileParser profileParser;
    private final String TEST_EMAIL = "beerklaro@bk.ru";
    private final String TEST_PASSWORD = "B0rzhchling5vaa789";

    @BeforeEach
    void setUp() {
        log.info("Инициализация ProfileParser перед тестом");
        profileParser = new ProfileParser();
    }

    @AfterEach
    void tearDown() {
        log.info("Завершение работы ProfileParser после теста");
        if (profileParser != null) {
            try {
                profileParser.close();
            } catch (Exception e) {
                log.warn("Ошибка при закрытии парсера: {}", e.getMessage());
            }
        }
    }

    @Test
    void testParseStudentProfile_RealApiCall() {
        log.info("Запуск реального теста обращения к API elearn");

        try {
            StudentRegistryDTO result = profileParser.parseStudentProfile(TEST_EMAIL, TEST_PASSWORD).block();

            assertNotNull(result, "Результат не должен быть null");
            assertNotNull(result.studentFio(), "ФИО студента не должно быть null");
            assertNotNull(result.studentEmail(), "Email студента не должен быть null");
            assertNotNull(result.courses(), "Список курсов не должен быть null");

            log.info("=== РЕАЛЬНЫЕ ДАННЫЕ СТУДЕНТА ===");
            log.info("ФИО: {}", result.studentFio());
            log.info("Email: {}", result.studentEmail());
            log.info("Часовой пояс: {}", result.timeZone());
            log.info("Статус образования: {}", result.educationStatus());
            log.info("Академическая группа: {}", result.academicGroup());
            log.info("Номер студента: {}", result.studentNumber());
            log.info("Количество курсов: {}", result.courses().size());

            assertTrue(result.studentEmail().contains("@"), "Email должен содержать @");
            assertFalse(result.studentFio().isEmpty(), "ФИО не должно быть пустым");

            log.info("=== СПИСОК КУРСОВ ===");
            for (int i = 0; i < result.courses().size(); i++) {
                CourseDto course = result.courses().get(i);
                log.info("Курс {}: {}", i + 1, course.name());
                log.info("  Категория: {}", course.courseCategory());
                log.info("  URL: {}", course.url());
            }

        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST && "Неверные учетные данные".equals(e.getReason())) {
                log.warn("Тест пропущен: неверные учетные данные для тестового аккаунта");
                return; // Пропускаем тест, если учетные данные неверные
            }
            throw e; // Пробрасываем другие исключения
        } catch (Exception e) {
            log.error("Неожиданная ошибка при выполнении теста: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    void testParseStudentProfile_CoursesValidation() {
        log.info("Запуск теста валидации курсов");

        StudentRegistryDTO result = profileParser.parseStudentProfile(TEST_EMAIL, TEST_PASSWORD)
                .block(); // Блокируем для теста

        assertNotNull(result, "Результат не должен быть null");
        assertNotNull(result.courses(), "Список курсов не должен быть null");

        if (result.courses().isEmpty()) {
            log.warn("Список курсов пуст - возможно, у студента нет активных курсов");
        } else {
            log.info("Найдено курсов: {}", result.courses().size());

            for (CourseDto course : result.courses()) {
                assertNotNull(course.name(), "Название курса не должно быть null");
                assertNotNull(course.courseCategory(), "Категория курса не должна быть null");
                assertNotNull(course.url(), "URL курса не должен быть null");

                assertFalse(course.name().isEmpty(), "Название курса не должно быть пустым");
                assertTrue(course.url().contains("elearn.urfu.ru"), "URL курса должен содержать elearn.urfu.ru");

                log.info("Курс '{}' прошел валидацию", course.name());
            }
        }
    }

    @Test
    void testParseStudentProfile_InvalidCredentials() {
        log.info("Запуск теста с неверными учетными данными");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileParser.parseStudentProfile("invalid@email.com", "wrongpassword")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        log.info("Получено ожидаемое исключение: {}", exception.getMessage());
    }

    @Test
    void testParseStudentProfile_EmptyCredentials() {
        log.info("Запуск теста с пустыми учетными данными");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileParser.parseStudentProfile("", "")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        log.info("Получено исключение при пустых учетных данных: {}", exception.getMessage());
    }

    @Test
    void testParseStudentProfile_NullCredentials() {
        log.info("Запуск теста с null учетными данными");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileParser.parseStudentProfile(null, null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        log.info("Получено исключение при null учетных данных: {}", exception.getMessage());
    }

    @Test
    void testLogin_Success() {
        log.info("Запуск теста успешного логина");

        assertDoesNotThrow(
                () -> profileParser.login(TEST_EMAIL, TEST_PASSWORD)
        );

        log.info("Успешный логин выполнен");
    }

    @Test
    void testLogin_InvalidCredentials() {
        log.info("Запуск теста неуспешного логина");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> profileParser.login("invalid@email.com", "wrongpassword")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        log.info("Получено ожидаемое исключение при неверном логине: {}", exception.getMessage());
    }

    @Test
    void testStudentRegistryDTO_ToString() {
        log.info("Запуск теста toString метода DTO");

        List<CourseDto> courses = List.of(
                new CourseDto("Базовая архитектура программного обеспечения", "Информатика и вычислительная техника (09.03.01)", "https://elearn.urfu.ru/course/view.php?id=6736")
        );

        StudentRegistryDTO dto = new StudentRegistryDTO(
                "Иванов Иван Иванович",
                "Asia/Yekaterinburg",
                "Бакалавр",
                "РИ-420942",
                "09203251",
                "test@urfu.me",
                courses
        );

        String toStringResult = dto.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Иванов Иван Иванович"));
        assertTrue(toStringResult.contains("РИ-420942"));
        assertTrue(toStringResult.contains("courses="));

        log.info("Результат toString: {}", toStringResult);
    }

    @Test
    void testCourseDto() {
        log.info("Запуск теста CourseDto");

        CourseDto course = new CourseDto(
                "Базовая архитектура программного обеспечения",
                "Информатика и вычислительная техника (09.03.01)",
                "https://elearn.urfu.ru/course/view.php?id=6736"
        );

        assertEquals("Базовая архитектура программного обеспечения", course.name());
        assertEquals("Информатика и вычислительная техника (09.03.01)", course.courseCategory());
        assertEquals("https://elearn.urfu.ru/course/view.php?id=6736", course.url());

        log.info("CourseDto создан корректно: {}", course);
    }
}