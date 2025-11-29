package urfu.student.helper.parser;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Getter
public class SeleniumParser implements AutoCloseable {
    private final WebDriver driver;

    public SeleniumParser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        this.driver = new ChromeDriver(options);
    }

    @Override
    public void close() throws Exception {
        if (driver != null) {
            driver.quit();
        }
    }
}