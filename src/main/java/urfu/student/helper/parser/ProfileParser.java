package urfu.student.helper.parser;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.course.dto.CourseDTO;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;
import urfu.student.helper.security.dto.AuthRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/*String fio,
String email,
String timeZone,
StudentEntity.EducationStatus educationStatus,
String academicGroup*/
@Slf4j
@Service
public class ProfileParser extends SeleniumParser {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private final WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);

    public Mono<StudentRegistryDTO> parseStudentProfile(AuthRequest authRequest) {
        login(authRequest);
        List<CourseDTO> courses = parseCourses();
        getDriver().get("https://elearn.urfu.ru/user/profile.php");
        String fio = getDriver().findElement(By.className("h2")).getText();
        String email = authRequest.username();
        String timeZone = parseTextByNodeName("Часовой пояс");
        StudentEntity.EducationStatus educationStatus = StudentEntity.EducationStatus.getByName(parseTextByNodeName("Должность"));
        String academicGroup = parseTextByNodeName("Academic_group");
        String studentNumber = parseTextByNodeName("Student_number");
        log.info("ФИО: {}", fio);
        log.info("email: {}", email);
        log.info("Часовой пояс: {}", timeZone);
        log.info("Статус обучения: {}", educationStatus);
        log.info("Группа: {}", academicGroup);
        log.info("Номер студента elearn: {}", studentNumber);
        log.info("Курсы: {}", courses);
        getDriver().close();
        return Mono.fromCallable(() -> new StudentRegistryDTO(fio, timeZone, educationStatus, academicGroup, studentNumber, email, courses));
    }

    public void login(@NotNull AuthRequest authRequest){
        String username = authRequest.username();
        String password = authRequest.password();
        getDriver().get("https://elearn.urfu.ru/my");
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNameInput")));
        WebElement passwordInput = getDriver().findElement(By.id("passwordInput"));
        WebElement submitButton = getDriver().findElement(By.id("submitButton"));
        emailInput.clear();
        emailInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        submitButton.click();
        try {
            wait.until(ExpectedConditions.or(ExpectedConditions.urlContains("elearn.urfu.ru/my/")));
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Такого аккаунта eLearn не существует!");
        }
        log.info("Регистрация с аккаунтом {} успешна!", authRequest.username());
    }

    public String parseTextByNodeName(String nodeName){
        List<WebElement> contentNodes = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.contentnode")));
        return contentNodes.stream().filter(node -> {
            WebElement dt = node.findElement(By.tagName("dt"));
            return nodeName.equals(dt.getText());
        }).findAny().map(node -> node.findElement(By.tagName("dd")).getText()).orElseThrow();
    }

    public List<CourseDTO> parseCourses() {
        List<WebElement> courseCards = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("div.dashboard-card[data-region='course-content']")
                )
        );

        return courseCards.stream()
                .map(this::parseCourseCard)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private CourseDTO parseCourseCard(WebElement card) {
        try {
            // Извлекаем название курса
            String name = card.findElement(By.cssSelector("a.coursename span.text-truncate")).getText();

            // Извлекаем категорию курса
            String category = card.findElement(By.cssSelector("div.text-muted span.text-truncate")).getText();

            // Извлекаем URL курса
            String url = card.findElement(By.cssSelector("a.coursename")).getAttribute("href");

            return new CourseDTO(name, category, url);

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге карточки курса: " + e.getMessage());
            return null;
        }
    }
}