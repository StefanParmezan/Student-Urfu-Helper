package urfu.student.helper.parser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class SeleniumParser implements AutoCloseable {
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
    }

    @Override
    public void close() throws Exception {
        driver.quit();
        driver.close();
    }
}