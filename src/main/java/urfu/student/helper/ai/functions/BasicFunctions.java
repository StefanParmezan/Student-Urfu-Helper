package urfu.student.helper.ai.functions;

import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import urfu.student.helper.ai.StudentHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
public class BasicFunctions {
    private final StudentHolder holder;


    @Tool(name = "getDateTime", description = "Говорит тебе текущие дату и время пользователя")
    public String getDateTime() {
        ZoneId zone = ZoneId.of("Asia/Yekaterinburg"); //TODO = holder.getStudent().getTimeZone();
        return LocalDateTime.now(zone).toString();
    }

    @Tool(name = "getGeneralCoursesList", description = "Говорит тебе все существующие курсы. Полезно чтобы узначть что ты можешь предложить студенту")
    public String getGeneralCoursesList() {
        return null;
    }

    @Tool(name = "getStudentCoursesList", description = "Говорит тебе курсы на которые уже записан студент с которым ты работаешь")
    public String getStudentCoursesList() {
        return null;
    }

    @Tool(name = "getCourseDescription", description = "Говорит тебе описание конкретного курса")
    public String getCourseDescription() {
        return null;
    }

}
