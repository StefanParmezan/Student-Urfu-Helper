package urfu.student.helper.security.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import urfu.student.helper.models.student.Student;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HtmlProfileParserTest {

	private static final Logger logger = LoggerFactory.getLogger(HtmlProfileParserTest.class);

	private HtmlProfileParser htmlProfileParser;

	@BeforeEach
	void setUp() {
		logger.info("Setting up HtmlProfileParserTest");
		htmlProfileParser = new HtmlProfileParser();
	}

	@Test
	void testParseRealProfileFile() {
		logger.info("Running testParseRealProfileFile");

		try {
			// Given
			Path filePath = Paths.get("/home/stefanparmezan/Загрузки/profile.php");

			if (!Files.exists(filePath)) {
				logger.warn("Real profile file not found at: {}. Skipping test.", filePath);
				return; // Пропускаем тест если файла нет
			}

			String html = Files.readString(filePath);
			String password = "testPassword123";

			logger.info("Read real URFU profile file, size: {} characters", html.length());

			// Диагностика структуры HTML для отладки
			logger.info("=== DIAGNOSING HTML STRUCTURE ===");
			htmlProfileParser.diagnoseHtmlStructure(html);
			logger.info("=== END DIAGNOSIS ===");

			// When
			Student student = htmlProfileParser.parseStudentProfile(html, password);

			// Then
			assertNotNull(student, "Student should not be null");
			logger.info("Successfully parsed real URFU profile file");

			// Выводим всю информацию о студенте для проверки
			logger.info("=== PARSED STUDENT INFORMATION ===");
			logger.info("Surname: {}", student.getStudentSurName());
			logger.info("Name: {}", student.getStudentName());
			logger.info("Patronymic: {}", student.getPatronymic());
			logger.info("Email: {}", student.getEmail());
			logger.info("Time Zone: {}", student.getTimeZone());
			logger.info("Education Status: {}", student.getEducationStatus());
			logger.info("Academic Group: {}", student.getAcademicGroup());
			logger.info("Student Number: {}", student.getStudentNumber());
			logger.info("Number of Courses: {}", student.getCourseList().size());

			// Выводим курсы с ссылками
			if (student.getCourseList() != null && !student.getCourseList().isEmpty()) {
				logger.info("Courses with URLs:");
				student.getCourseList().forEach(course ->
						logger.info("  - {} -> {}", course.getCourseName(), course.getCourseUrl()));

				// Проверяем что у курсов есть названия и ссылки
				student.getCourseList().forEach(course -> {
					assertNotNull(course.getCourseName(), "Course name should not be null");
					assertNotNull(course.getCourseUrl(), "Course URL should not be null");
					assertTrue(course.getCourseUrl().contains("course"), "Course URL should contain 'course'");
				});
			} else {
				logger.warn("No courses found in profile");
			}
			logger.info("=== END STUDENT INFORMATION ===");

			// Проверяем основные поля
			assertNotNull(student.getStudentSurName(), "Surname should not be null");
			assertNotNull(student.getStudentName(), "Name should not be null");
			assertNotNull(student.getEmail(), "Email should not be null");

			logger.info("✅ Successfully parsed real URFU profile - Student: {} {}, Email: {}, Courses: {}",
					student.getStudentSurName(), student.getStudentName(),
					student.getEmail(), student.getCourseList().size());

		} catch (Exception e) {
			logger.error("❌ Error reading or parsing real URFU profile file", e);
			fail("Should not throw exception when parsing real URFU profile file: " + e.getMessage());
		}
	}

	@Test
	void testParseValidProfile() {
		logger.info("Running testParseValidProfile");

		// Given
		String html = getValidProfileHtml();
		String password = "testPassword123";

		// When
		Student student = htmlProfileParser.parseStudentProfile(html, password);

		// Then
		assertNotNull(student, "Student should not be null");
		logger.debug("Parsed student: {}", student);

		assertEquals("Лутков", student.getStudentSurName());
		assertEquals("Евгений", student.getStudentName());
		assertEquals("Александрович", student.getPatronymic());
		assertEquals("Evgeny.Lutkov@urfu.me", student.getEmail());
		assertEquals("Asia/Yekaterinburg", student.getTimeZone());
		assertEquals("Бакалавр", student.getEducationStatus());
		assertEquals("РИ-420942", student.getAcademicGroup());
		assertEquals("09203251", student.getStudentNumber());
		assertEquals("testPassword123", student.getPassword());
		assertNotNull(student.getCourseList());
		assertEquals(5, student.getCourseList().size());

		// Verify first course
		assertEquals("Базовая архитектура программного обеспечения",
				student.getCourseList().getFirst().getCourseName());

		logger.info("testParseValidProfile completed successfully");
	}

	// ... остальные тестовые методы остаются такими же ...

	private String getValidProfileHtml() {
		// Используем упрощенную версию для базового тестирования
		return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Лутков Евгений Александрович: Публичный профиль</title>
            </head>
            <body>
                <div class="header-main">УрФУ</div>
                <header id="page-header">
                    <div class="page-header-headings">
                        <h1 class="h2">Лутков Евгений Александрович</h1>
                    </div>
                </header>
                <div class="userprofile">
                    <div class="profile_tree">
                        <section class="node_category card">
                            <div class="card-body">
                                <h3 class="lead">Подробная информация о пользователе</h3>
                                <ul>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Адрес электронной почты</dt>
                                            <dd>
                                                <a href="mailto:Evgeny.Lutkov@urfu.me">
                                                    Evgeny.Lutkov@urfu.me
                                                </a> (Видно другим участникам курса)
                                            </dd>
                                        </dl>
                                    </li>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Часовой пояс</dt>
                                            <dd>Asia/Yekaterinburg</dd>
                                        </dl>
                                    </li>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Должность</dt>
                                            <dd>Бакалавр</dd>
                                        </dl>
                                    </li>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Academic_group</dt>
                                            <dd>РИ-420942</dd>
                                        </dl>
                                    </li>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Student_number</dt>
                                            <dd>09203251</dd>
                                        </dl>
                                    </li>
                                </ul>
                            </div>
                        </section>
                        <section class="node_category card">
                            <div class="card-body">
                                <h3 class="lead">Информация о курсах</h3>
                                <ul>
                                    <li class="contentnode">
                                        <dl>
                                            <dt>Участник курсов</dt>
                                            <dd>
                                                <ul>
                                                    <li><a href="https://elearn.urfu.ru/course/view.php?id=6736">Базовая архитектура программного обеспечения</a></li>
                                                    <li><a href="https://elearn.urfu.ru/course/view.php?id=8053">Бизнес-аналитика (2024)</a></li>
                                                    <li><a href="https://elearn.urfu.ru/course/view.php?id=4471">Основы программирования на примере C#, 2 часть</a></li>
                                                    <li><a href="https://elearn.urfu.ru/course/view.php?id=7055">Создание мобильных приложений Qt Quick</a></li>
                                                    <li><a href="https://elearn.urfu.ru/course/view.php?id=5882">Технологии программирования на Python (2 курс, ИРИТ-РТФ)</a></li>
                                                </ul>
                                            </dd>
                                        </dl>
                                    </li>
                                </ul>
                            </div>
                        </section>
                    </div>
                </div>
            </body>
            </html>
            """;
	}
}