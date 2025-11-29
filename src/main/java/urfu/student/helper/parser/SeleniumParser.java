package urfu.student.helper.parser;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SeleniumParser implements AutoCloseable {
    private WebDriver driver;

    @PostConstruct
    public void init() {
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