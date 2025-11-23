package urfu.student.helper.security.dto;

import urfu.student.helper.db.course.CourseEntity;

public record CourseDto(String name, String courseCategory, String url) {
    public CourseEntity of(String name, String courseCategory,String url){
        return new CourseEntity(name, courseCategory, url);
    }
}