package urfu.student.helper.security.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import urfu.student.helper.db.student.StudentEntity;

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
			StudentEntity studentEntity = htmlProfileParser.parseStudentProfile(html, password);

			// Then
			assertNotNull(studentEntity, "Student should not be null");
			logger.info("Successfully parsed real URFU profile file");

			// Выводим всю информацию о студенте для проверки
			logger.info("=== PARSED STUDENT INFORMATION ===");
			logger.info("Surname: {}", studentEntity.getStudentSurName());
			logger.info("Name: {}", studentEntity.getStudentName());
			logger.info("Patronymic: {}", studentEntity.getPatronymic());
			logger.info("Email: {}", studentEntity.getEmail());
			logger.info("Time Zone: {}", studentEntity.getTimeZone());
			logger.info("Education Status: {}", studentEntity.getEducationStatus());
			logger.info("Academic Group: {}", studentEntity.getAcademicGroup());
			logger.info("Student Number: {}", studentEntity.getStudentNumber());
			logger.info("Number of Courses: {}", studentEntity.getCourseEntityList().size());

			// Выводим курсы с ссылками
			if (studentEntity.getCourseEntityList() != null && !studentEntity.getCourseEntityList().isEmpty()) {
				logger.info("Courses with URLs:");
				studentEntity.getCourseEntityList().forEach(courseEntity ->
						logger.info("  - {} -> {}", courseEntity.getCourseName(), courseEntity.getCourseUrl()));

				// Проверяем что у курсов есть названия и ссылки
				studentEntity.getCourseEntityList().forEach(courseEntity -> {
					assertNotNull(courseEntity.getCourseName(), "Course name should not be null");
					assertNotNull(courseEntity.getCourseUrl(), "Course URL should not be null");
					assertTrue(courseEntity.getCourseUrl().contains("courseEntity"), "Course URL should contain 'courseEntity'");
				});
			} else {
				logger.warn("No courses found in profile");
			}
			logger.info("=== END STUDENT INFORMATION ===");

			// Проверяем основные поля
			assertNotNull(studentEntity.getStudentSurName(), "Surname should not be null");
			assertNotNull(studentEntity.getStudentName(), "Name should not be null");
			assertNotNull(studentEntity.getEmail(), "Email should not be null");

			logger.info("✅ Successfully parsed real URFU profile - Student: {} {}, Email: {}, Courses: {}",
					studentEntity.getStudentSurName(), studentEntity.getStudentName(),
					studentEntity.getEmail(), studentEntity.getCourseEntityList().size());

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
		StudentEntity studentEntity = htmlProfileParser.parseStudentProfile(html, password);

		// Then
		assertNotNull(studentEntity, "Student should not be null");
		logger.debug("Parsed student: {}", studentEntity);

		assertEquals("Лутков", studentEntity.getStudentSurName());
		assertEquals("Евгений", studentEntity.getStudentName());
		assertEquals("Александрович", studentEntity.getPatronymic());
		assertEquals("Evgeny.Lutkov@urfu.me", studentEntity.getEmail());
		assertEquals("Asia/Yekaterinburg", studentEntity.getTimeZone());
		assertEquals("Бакалавр", studentEntity.getEducationStatus());
		assertEquals("РИ-420942", studentEntity.getAcademicGroup());
		assertEquals("09203251", studentEntity.getStudentNumber());
		assertEquals("testPassword123", studentEntity.getPassword());
		assertNotNull(studentEntity.getCourseEntityList());
		assertEquals(5, studentEntity.getCourseEntityList().size());

		// Verify first courseEntity
		assertEquals("Базовая архитектура программного обеспечения",
				studentEntity.getCourseEntityList().getFirst().getCourseName());

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
                                                    <li><a href="https://elearn.urfu.ru/courseEntity/view.php?id=6736">Базовая архитектура программного обеспечения</a></li>
                                                    <li><a href="https://elearn.urfu.ru/courseEntity/view.php?id=8053">Бизнес-аналитика (2024)</a></li>
                                                    <li><a href="https://elearn.urfu.ru/courseEntity/view.php?id=4471">Основы программирования на примере C#, 2 часть</a></li>
                                                    <li><a href="https://elearn.urfu.ru/courseEntity/view.php?id=7055">Создание мобильных приложений Qt Quick</a></li>
                                                    <li><a href="https://elearn.urfu.ru/courseEntity/view.php?id=5882">Технологии программирования на Python (2 курс, ИРИТ-РТФ)</a></li>
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