package urfu.student.helper.parser;

import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SeleniumParser implements AutoCloseable {
    private WebDriver driver;

    @Override
    public void close() throws Exception {
        driver.quit();
        driver.close();
    }
}