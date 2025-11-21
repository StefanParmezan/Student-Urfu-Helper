package urfu.student.helper.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import urfu.student.helper.models.student.Student;
import urfu.student.helper.models.student.dto.StudentRegistryDTO;
import urfu.student.helper.repositories.StudentRepository;
import urfu.student.helper.services.StudentService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/urfuhelper/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Student> save(@RequestBody StudentRegistryDTO studentRegistryDTO){
        return ResponseEntity.ok(studentService.save(studentRegistryDTO));
    }

}
