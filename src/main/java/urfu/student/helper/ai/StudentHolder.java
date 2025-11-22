package urfu.student.helper.ai;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import urfu.student.helper.db.student.StudentEntity;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
@Getter
@Setter
public class StudentHolder {
    private StudentEntity student;

    public void setStudent(StudentEntity student) {
        if (this.student == null) {
            this.student = student;
        } else {
            throw new IllegalStateException("trying to replace student in holder");
        }
    }
}
