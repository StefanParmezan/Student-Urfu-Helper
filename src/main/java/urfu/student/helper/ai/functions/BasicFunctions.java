package urfu.student.helper.ai.functions;

import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import urfu.student.helper.ai.StudentHolder;
import urfu.student.helper.ai.functions.service.AiFunctionService;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@AllArgsConstructor
public class BasicFunctions {
    private final StudentHolder holder;
    private final AiFunctionService service;

    @Tool(name = "getDateTime", description = "Говорит тебе текущие дату и время пользователя")
    public String getDateTime() {
        ZoneId zone = holder.getStudent().getTimeZone();
        return LocalDateTime.now(zone).toString();
    }

    @Tool(name = "getGeneralCoursesList", description = "Говорит тебе все существующие курсы. Полезно чтобы узначть что ты можешь предложить студенту")
    public String getGeneralCoursesList() {
        return service.getAllCourses().toString();
    }

    @Tool(name = "getStudentCoursesList", description = "Говорит тебе курсы на которые уже записан студент с которым ты работаешь")
    public String getStudentCoursesList() {
        return service.getStudentCourses(holder.getStudent()).toString();
    }

    @Tool(name = "getStudentMarks", description = "Говорит тебе оценки по курсу")
    public String getStudentMarks(
            @ToolParam(description = "Имя курса на который ты хочешь узнать оценки") String courseName
    ) {
        return null;
    }

}
