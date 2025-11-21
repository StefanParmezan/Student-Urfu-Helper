package urfu.student.helper.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import urfu.student.helper.services.student.StudentService;

@AllArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;


}
