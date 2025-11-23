package urfu.student.helper.security.dto;

import urfu.student.helper.db.student.dto.StudentDTO;

public record AuthResponse(String token, StudentDTO student) {}