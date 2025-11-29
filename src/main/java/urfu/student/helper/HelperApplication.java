package urfu.student.helper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import urfu.student.helper.parser.ProfileParser;
import urfu.student.helper.security.dto.AuthRequest;

@SpringBootApplication
public class HelperApplication {
	public static void main(String[] args) {
		ApplicationContext applicationContext =  SpringApplication.run(HelperApplication.class, args);
		applicationContext.getBean(ProfileParser.class).login(new AuthRequest("beerklaro@bk.ru", "B0rzhchling5vaa789"));
	}
}
