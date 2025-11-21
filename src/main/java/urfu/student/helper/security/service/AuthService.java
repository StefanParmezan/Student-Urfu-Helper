/*
package urfu.student.helper.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urfu.student.helper.models.student.Student;
import urfu.student.helper.security.client.UrfuAuthClient;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.dto.LoginRequest;
import urfu.student.helper.security.jwt.JwtService;
import urfu.student.helper.security.parser.HtmlProfileParser;
import urfu.student.helper.services.student.StudentService;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UrfuAuthClient urfuAuthClient;
    private final HtmlProfileParser htmlProfileParser;
    private final StudentService studentService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UrfuAuthClient urfuAuthClient, HtmlProfileParser htmlProfileParser,
                       StudentService studentService, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.urfuAuthClient = urfuAuthClient;
        this.htmlProfileParser = htmlProfileParser;
        this.studentService = studentService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Processing login request for email: {}", loginRequest.email());

        try {
            // 1. Аутентификация через УрФУ и получение HTML профиля
            String htmlProfile = urfuAuthClient.authenticateAndGetProfile(
                    loginRequest.email(), loginRequest.password());

            // 2. Парсинг данных студента из HTML
            Student studentInfo = htmlProfileParser.parseStudentProfile(
                    htmlProfile, loginRequest.password());

            // 3. Сохранение/обновление в БД с хэшированием пароля
            Student savedStudent = studentService.saveOrUpdateStudent(studentInfo);

            // 4. Генерация JWT токена
            String token = jwtService.generateToken(savedStudent.getStudentEmail());

            logger.info("Successful login for student: {}", savedStudent.getStudentEmail());

            return new AuthResponse(token, savedStudent);

        } catch (Exception e) {
            logger.error("Login failed for email: {}", loginRequest.email(), e);
            throw new RuntimeException("Authentication failed", e);
        }
    }
}*/