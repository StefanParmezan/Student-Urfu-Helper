package urfu.student.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import urfu.student.helper.security.parser.OpenInfoParser;

@SpringBootApplication
public class HelperApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelperApplication.class, args);
	}
}
