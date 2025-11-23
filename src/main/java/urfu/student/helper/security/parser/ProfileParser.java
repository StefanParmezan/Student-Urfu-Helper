package urfu.student.helper.security.parser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.CourseDto;

import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ProfileParser extends SeleniumParser {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public Mono<StudentRegistryDTO> parseStudentProfile(AuthRequest authRequest) {
        return login(authRequest.username(), authRequest.password())
                .flatMap(loginResult -> {
                    getDriver().get("https://elearn.urfu.ru/user/profile.php");
                    WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);

                    try {
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".userprofile")));

                        String fio = extractFio(wait);
                        String studentEmail = extractEmail(wait);
                        String timeZone = extractTimeZone(wait);
                        String educationStatus = extractEducationStatus(wait);
                        String academicGroup = extractAcademicGroup(wait);
                        String studentNumber = extractStudentNumber(wait);

                        return extractCourses()
                                .map(courses -> new StudentRegistryDTO(fio, ZoneId.of(timeZone), educationStatus, academicGroup, studentNumber, studentEmail, courses));

                    } catch (Exception e) {
                        log.error("Ошибка при парсинге профиля: {}", e.getMessage());
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при получении данных профиля"));
                    }
                });
    }

    public Mono<Boolean> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email и пароль не могут быть пустыми"));
        }

        return Mono.fromCallable(() -> {
            getDriver().get("https://elearn.urfu.ru/my/");
            WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);

            try {
                WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNameInput")));
                WebElement passwordInput = getDriver().findElement(By.id("passwordInput"));
                WebElement submitButton = getDriver().findElement(By.id("submitButton"));

                emailInput.clear();
                emailInput.sendKeys(email);
                passwordInput.clear();
                passwordInput.sendKeys(password);
                submitButton.click();

                wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("elearn.urfu.ru/my/"),
                        ExpectedConditions.presenceOfElementLocated(By.id("userNameInput"))
                ));

                if (!getDriver().findElements(By.id("userNameInput")).isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверные учетные данные");
                }

                return true;

            } catch (ResponseStatusException e) {
                throw e;
            } catch (Exception e) {
                log.error("Ошибка при логине: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка аутентификации");
            }
        });
    }

    private Mono<List<CourseDto>> extractCourses() {
        return Mono.fromCallable(() -> {
            List<CourseDto> courses = new ArrayList<>();

            try {
                // Переходим на страницу с курсами
                getDriver().get("https://elearn.urfu.ru/my/courses.php");
                WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);

                // Ждем загрузки курсов
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".dashboard-card, .course-info-container")));

                // Ищем курсы в карточках
                List<WebElement> courseCards = getDriver().findElements(
                        By.cssSelector(".dashboard-card[data-region='course-content']")
                );

                if (courseCards.isEmpty()) {
                    log.info("Курсы не найдены на странице courses.php");
                    return courses;
                }

                log.info("Найдено карточек курсов: {}", courseCards.size());

                for (WebElement card : courseCards) {
                    try {
                        String name = extractCourseNameFromCard(card);
                        String courseCategory = extractCourseCategoryFromCard(card);
                        String url = extractCourseUrlFromCard(card);

                        if (name != null && !name.isEmpty() && !name.equals("Образцы сайтов курсов")) {
                            courses.add(new CourseDto(name, courseCategory, url));
                            log.info("Добавлен курс: {}", name);
                        }
                    } catch (Exception e) {
                        log.warn("Ошибка при парсинге карточки курса: {}", e.getMessage());
                    }
                }

            } catch (Exception e) {
                log.error("Ошибка при получении курсов: {}", e.getMessage());
            }

            return courses;
        });
    }

    private String extractFio(WebDriverWait wait) {
        try {
            // Ищем ФИО в заголовке страницы профиля
            List<WebElement> fioElements = getDriver().findElements(By.cssSelector("h1, h2, h3"));
            for (WebElement element : fioElements) {
                String text = element.getText().trim();
                if (!text.isEmpty() && !text.equals("Профиль пользователя") && !text.contains("Профиль")) {
                    return text;
                }
            }

            // Альтернативный поиск по классам
            List<WebElement> profileElements = getDriver().findElements(By.cssSelector(".page-header-headings h1, .page-header-headings h2"));
            if (!profileElements.isEmpty()) {
                return profileElements.get(0).getText().trim();
            }

            // Поиск в карточке пользователя
            List<WebElement> cardElements = getDriver().findElements(By.cssSelector(".card-title, .userprofile h2"));
            for (WebElement element : cardElements) {
                String text = element.getText().trim();
                if (!text.isEmpty() && !text.equals("Профиль пользователя") && !text.contains("Профиль")) {
                    return text;
                }
            }

        } catch (Exception e) {
            log.warn("Не удалось извлечь ФИО: {}", e.getMessage());
        }
        return "Не указано";
    }

    private String extractEmail(WebDriverWait wait) {
        List<WebElement> emailLinks = getDriver().findElements(By.cssSelector("a[href^='mailto:']"));
        if (!emailLinks.isEmpty()) {
            return emailLinks.get(0).getText();
        }
        return "";
    }

    private String extractTimeZone(WebDriverWait wait) {
        List<WebElement> timezoneElements = getDriver().findElements(
                By.xpath("//li[contains(@class, 'contentnode')]//dt[contains(text(), 'Часовой пояс')]/following-sibling::dd")
        );
        if (!timezoneElements.isEmpty()) {
            return timezoneElements.get(0).getText();
        }
        return "";
    }

    private String extractEducationStatus(WebDriverWait wait) {
        // Ищем статус образования в различных возможных местах
        List<WebElement> educationElements = getDriver().findElements(
                By.xpath("//dt[contains(text(), 'Образование') or contains(text(), 'Статус') or contains(text(), 'Education')]/following-sibling::dd")
        );
        if (!educationElements.isEmpty()) {
            return educationElements.get(0).getText();
        }

        // Альтернативный поиск по классам
        List<WebElement> statusElements = getDriver().findElements(By.cssSelector("dd[id*='yui_']"));
        for (WebElement element : statusElements) {
            String text = element.getText().trim();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "";
    }

    private String extractAcademicGroup(WebDriverWait wait) {
        List<WebElement> academicGroupElements = getDriver().findElements(
                By.xpath("//dt[contains(text(), 'Academic_group')]/following-sibling::dd")
        );
        if (!academicGroupElements.isEmpty()) {
            return academicGroupElements.getFirst().getText();
        }
        return "";
    }

    private String extractStudentNumber(WebDriverWait wait) {
        List<WebElement> studentNumberElements = getDriver().findElements(
                By.xpath("//li[contains(@class, 'contentnode')]//dt[contains(text(), 'Student_number')]/following-sibling::dd")
        );
        if (!studentNumberElements.isEmpty()) {
            return studentNumberElements.getFirst().getText();
        }
        return "";
    }

    private String extractCourseNameFromCard(WebElement card) {
        try {
            // Ищем название курса в элементе с классом multiline
            WebElement nameElement = card.findElement(By.cssSelector(".coursename .multiline"));
            return nameElement.getText().trim();
        } catch (Exception e) {
            log.warn("Не удалось извлечь название курса: {}", e.getMessage());
            return "";
        }
    }

    private String extractCourseCategoryFromCard(WebElement card) {
        try {
            // Ищем категорию курса в элементе с классом categoryname
            WebElement categoryElement = card.findElement(By.cssSelector(".categoryname"));
            return categoryElement.getText().trim();
        } catch (Exception e) {
            log.warn("Не удалось извлечь категорию курса: {}", e.getMessage());
            return "";
        }
    }

    private String extractCourseUrlFromCard(WebElement card) {
        try {
            // Ищем ссылку на курс в элементе a с классом coursename
            WebElement linkElement = card.findElement(By.cssSelector(".coursename.aalink"));
            return linkElement.getAttribute("href");
        } catch (Exception e) {
            log.warn("Не удалось извлечь URL курса: {}", e.getMessage());
            return "";
        }
    }
}