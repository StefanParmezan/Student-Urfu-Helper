package urfu.student.helper.security.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.parser.ElearnUfuParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElearnUfuParserTest {

    private ElearnUfuParser parser;
    private RestClient restClient;
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec requestHeadersSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Создаем моки для RestClient
        restClient = mock(RestClient.class);
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(String.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        parser = new ElearnUfuParser() {
            // Переопределяем restClient для тестов
            @Override
            protected String fetchPageContent(String url) {
                try {
                    // Читаем содержимое из локального файла
                    return Files.readString(Paths.get("/home/stefanparmezan/Загрузки/Портал электронного обучения.mhtml"));
                } catch (IOException e) {
                    throw new RuntimeException("Не удалось прочитать файл с страницей", e);
                }
            }
        };
    }

    @Test
    void testParseAllCourses_ShouldReturnNonEmptyTreeSet() {
        // Act
        TreeSet<CourseAiDTO> courses = parser.parseAllCourses();

        // Assert
        assertNotNull(courses, "Результат не должен быть null");
        assertFalse(courses.isEmpty(), "Список курсов не должен быть пустым");
        assertTrue(courses.size() > 0, "Должно быть найдено хотя бы несколько курсов");
    }

    @Test
    void testParseAllCourses_ShouldReturnSortedCourses() {
        // Act
        TreeSet<CourseAiDTO> courses = parser.parseAllCourses();

        // Assert
        String previousName = "";
        for (CourseAiDTO course : courses) {
            assertTrue(course.name().compareTo(previousName) >= 0,
                    "Курсы должны быть отсортированы по имени: " + previousName + " -> " + course.name());
            previousName = course.name();
        }
    }

    @Test
    void testParseAllCourses_CourseFieldsShouldBeFilled() {
        // Act
        TreeSet<CourseAiDTO> courses = parser.parseAllCourses();

        // Assert
        for (CourseAiDTO course : courses) {
            assertNotNull(course.name(), "Название курса не должно быть null");
            assertFalse(course.name().trim().isEmpty(), "Название курса не должно быть пустым");

            assertNotNull(course.url(), "URL курса не должен быть null");
            assertTrue(course.url().startsWith("https://elearn.urfu.ru"),
                    "URL должен начинаться с https://elearn.urfu.ru");

            // Категория может быть null или пустой для некоторых курсов
            assertNotNull(course.category(), "Категория не должна быть null (может быть пустой строкой)");
        }
    }

    @Test
    void testParseCoursesAsMap_ShouldReturnNonEmptyMap() {
        // Act
        Map<String, String> courseMap = parser.parseCoursesAsMap();

        // Assert
        assertNotNull(courseMap, "Map не должен быть null");
        assertFalse(courseMap.isEmpty(), "Map не должен быть пустым");
        assertEquals(courseMap.size(), parser.parseAllCourses().size(),
                "Размер Map должен совпадать с размером TreeSet");
    }

    @Test
    void testParseCoursesAsMap_ShouldContainValidEntries() {
        // Act
        Map<String, String> courseMap = parser.parseCoursesAsMap();

        // Assert
        for (Map.Entry<String, String> entry : courseMap.entrySet()) {
            assertNotNull(entry.getKey(), "Ключ (название курса) не должен быть null");
            assertFalse(entry.getKey().trim().isEmpty(), "Ключ (название курса) не должен быть пустым");

            assertNotNull(entry.getValue(), "Значение (URL) не должно быть null");
            assertTrue(entry.getValue().startsWith("https://elearn.urfu.ru"),
                    "URL должен начинаться с https://elearn.urfu.ru");
        }
    }

    @Test
    void testCourseAiDTO_ComparableImplementation() {
        // Arrange
        CourseAiDTO course1 = new CourseAiDTO("Алгебра", "Математика", "https://elearn.urfu.ru/course1");
        CourseAiDTO course2 = new CourseAiDTO("Биология", "Наука", "https://elearn.urfu.ru/course2");
        CourseAiDTO course3 = new CourseAiDTO("Алгебра", "Другая категория", "https://elearn.urfu.ru/course3");

        // Act & Assert
        assertTrue(course1.compareTo(course2) < 0, "Алгебра должна быть перед Биологией");
        assertTrue(course2.compareTo(course1) > 0, "Биология должна быть после Алгебры");
        assertEquals(0, course1.compareTo(course3), "Курсы с одинаковым названием должны быть равны");
    }

    @Test
    void testCourseAiDTO_ToString() {
        // Arrange
        CourseAiDTO course = new CourseAiDTO("Физика", "Естественные науки", "https://elearn.urfu.ru/physics");

        // Act
        String toStringResult = course.toString();

        // Assert
        assertTrue(toStringResult.contains("Физика"), "toString должен содержать название курса");
        assertTrue(toStringResult.contains("Естественные науки"), "toString должен содержать категорию");
        assertTrue(toStringResult.contains("https://elearn.urfu.ru/physics"), "toString должен содержать URL");
        assertTrue(toStringResult.startsWith("CourseAiDTO{"), "toString должен начинаться с CourseAiDTO{");
    }

    @Test
    void testFetchPageContent_WithMockedRestClient() {
        // Arrange
        String mockHtmlContent = "<html><body>Тестовое содержимое</body></html>";

        when(responseSpec.toEntity(String.class))
                .thenReturn(new ResponseEntity<>(mockHtmlContent, HttpStatus.OK));

        ElearnUfuParser realParser = new ElearnUfuParser();

        // Используем рефлексию для подмены restClient (в реальном коде лучше использовать dependency injection)
        try {
            var restClientField = ElearnUfuParser.class.getDeclaredField("restClient");
            restClientField.setAccessible(true);
            restClientField.set(realParser, restClient);
        } catch (Exception e) {
            fail("Не удалось установить мок restClient через рефлексию: " + e.getMessage());
        }

        // Act
        String result = realParser.fetchPageContent("/test-url");

        // Assert
        assertEquals(mockHtmlContent, result, "Должно вернуться мокированное содержимое");
    }

    @Test
    void testFetchPageContent_WithError() {
        // Arrange
        when(responseSpec.toEntity(String.class))
                .thenThrow(new RuntimeException("Network error"));

        ElearnUfuParser realParser = new ElearnUfuParser();

        try {
            var restClientField = ElearnUfuParser.class.getDeclaredField("restClient");
            restClientField.setAccessible(true);
            restClientField.set(realParser, restClient);
        } catch (Exception e) {
            fail("Не удалось установить мок restClient через рефлексию: " + e.getMessage());
        }

        // Act
        String result = realParser.fetchPageContent("/error-url");

        // Assert
        assertNull(result, "При ошибке должен возвращаться null");
    }

    @Test
    void testSpecificCoursesAreParsed() {
        // Act
        TreeSet<CourseAiDTO> courses = parser.parseAllCourses();

        // Assert - проверяем, что найдены конкретные ожидаемые курсы
        boolean foundExpectedCourse = courses.stream()
                .anyMatch(course -> course.name().contains("International Economics and Business") ||
                        course.name().contains("Бизнес-информатика") ||
                        course.name().contains("Математика"));

        assertTrue(foundExpectedCourse, "Должны быть найдены ожидаемые курсы из страницы");
    }
}