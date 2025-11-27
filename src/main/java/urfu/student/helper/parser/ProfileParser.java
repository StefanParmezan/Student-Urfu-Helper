package urfu.student.helper.parser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.course.dto.CourseDTO;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.security.dto.AuthRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProfileParser extends SeleniumParser {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    public Mono<StudentDTO> parseStudentProfile(AuthRequest authRequest) {

    }
}