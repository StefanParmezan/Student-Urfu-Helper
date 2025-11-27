package urfu.student.helper.parser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.security.dto.AuthRequest;

import java.time.Duration;

@Slf4j
public class ProfileParser extends SeleniumParser {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(5);

    public Mono<StudentDTO> parseStudentProfile(AuthRequest authRequest) {
        login(authRequest);
        log.info("login successful");
        return null;
    }

    public void login(AuthRequest authRequest){
        String username = authRequest.username();
        String password = authRequest.password();
        getDriver().get("https://elearn.urfu.ru/my");
        WebDriverWait wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT);
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userNameInput")));
        WebElement passwordInput = getDriver().findElement(By.id("passwordInput"));
        WebElement submitButton = getDriver().findElement(By.id("submitButton"));
        emailInput.clear();
        emailInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        submitButton.click();
        boolean authSuccess = wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("elearn.urfu.ru/my/"),
                ExpectedConditions.presenceOfElementLocated(By.id("userNameInput"))
        ));
        log.info("Регистрация успешна: {}", authSuccess);
        if(!authSuccess){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Такая учетная запись elearn не найдена.");
        }
    }
}