package urfu.student.helper.security.dto;

public record AuthResponse(String token, StudentProfileDto student) {}