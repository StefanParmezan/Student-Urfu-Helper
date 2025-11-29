package urfu.student.helper.security.dto;

public record AuthResponse(String token, urfu.student.helper.db.student.dto.StudentRegistryDTO student) {}