package urfu.student.helper.security.parser;

import lombok.Getter;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class ChromeParser implements AutoCloseable {
    @Getter
    private final ChromeDriver driver = new ChromeDriver();

    @Override
    public void close() throws Exception {
        driver.close();
    }
}