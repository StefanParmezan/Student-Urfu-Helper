package urfu.student.helper.security.parser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class SeleniumParser implements AutoCloseable {
    {
        WebDriverManager.chromedriver().setup();
    }
    @Getter
    private final ChromeDriver driver = new ChromeDriver();


    @Override
    public void close() throws Exception {
        driver.close();
    }
}
