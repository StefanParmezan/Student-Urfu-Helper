package urfu.student.helper.parser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.github.bonigarcia.wdm.WebDriverManager;

@Component
@Scope("prototype") // ВАЖНО: каждый запрос получает свой экземпляр
public class SeleniumParser {
    private WebDriver driver;

    public WebDriver getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    private void initializeDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        this.driver = new ChromeDriver(options);

        // Настройка таймаутов
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(30));
    }

    public void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Логируем, но не прерываем выполнение
                System.err.println("Error closing driver: " + e.getMessage());
            }
            driver = null;
        }
    }
}