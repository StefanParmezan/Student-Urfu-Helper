package urfu.student.helper.security.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class UrfuApiConnecterTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();

        // Переходим на страницу, которая редиректит на логин
        driver.get("https://elearn.urfu.ru/my");
    }

    @Test
    public void testLoginFormDisplayed() {
        // Ждем пока загрузится форма логина
        WebElement loginForm = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("loginForm"))
        );

        assertTrue(loginForm.isDisplayed(), "Форма логина должна отображаться");

        // Проверяем основные элементы формы
        WebElement emailInput = driver.findElement(By.id("userNameInput"));
        WebElement passwordInput = driver.findElement(By.id("passwordInput"));
        WebElement loginButton = driver.findElement(By.id("submitButton"));

        assertTrue(emailInput.isDisplayed(), "Поле email должно отображаться");
        assertTrue(passwordInput.isDisplayed(), "Поле пароля должно отображаться");
        assertTrue(loginButton.isDisplayed(), "Кнопка входа должна отображаться");

        // Проверяем placeholder у поля email
        assertEquals("proverka@example.com", emailInput.getAttribute("placeholder"),
                "Placeholder поля email должен соответствовать ожидаемому");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        // Ждем появления формы
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));

        // Проверяем наличие сообщения об ошибке
        WebElement errorElement = driver.findElement(By.id("error"));
        assertTrue(errorElement.isDisplayed(), "Сообщение об ошибке должно отображаться");

        String errorText = errorElement.getText();
        assertTrue(errorText.contains("Неверный идентификатор пользователя или пароль"),
                "Текст ошибки должен содержать сообщение о неверных credentials");

        // Заполняем форму неверными данными
        WebElement emailInput = driver.findElement(By.id("userNameInput"));
        emailInput.clear();
        emailInput.sendKeys("wrong@example.com");

        WebElement passwordInput = driver.findElement(By.id("passwordInput"));
        passwordInput.sendKeys("wrongpassword");

        // Кликаем на кнопку входа
        WebElement loginButton = driver.findElement(By.id("submitButton"));
        loginButton.click();

        // Ждем обновления страницы или повторного отображения ошибки
        wait.until(ExpectedConditions.textToBePresentInElement(
                (WebElement) By.id("errorText"), "Неверный идентификатор пользователя или пароль"
        ));
    }

    @Test
    public void testLoginWithEmptyCredentials() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));

        WebElement emailInput = driver.findElement(By.id("userNameInput"));
        emailInput.clear(); // Очищаем поле email

        WebElement passwordInput = driver.findElement(By.id("passwordInput"));
        passwordInput.clear(); // Очищаем поле пароля

        WebElement loginButton = driver.findElement(By.id("submitButton"));
        loginButton.click();

        // Проверяем, что остаемся на странице логина (URL содержит /adfs/ls/)
        assertTrue(driver.getCurrentUrl().contains("/adfs/ls/"),
                "После неудачного логина должны остаться на странице входа");
    }

    @Test
    public void testEmailFieldType() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));

        WebElement emailInput = driver.findElement(By.id("userNameInput"));
        assertEquals("email", emailInput.getAttribute("type"),
                "Поле должно иметь тип email для валидации");
    }

    @Test
    public void testPasswordFieldType() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));

        WebElement passwordInput = driver.findElement(By.id("passwordInput"));
        assertEquals("password", passwordInput.getAttribute("type"),
                "Поле пароля должно иметь тип password");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}