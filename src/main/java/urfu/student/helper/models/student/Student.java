package urfu.student.helper.models.student;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import urfu.student.helper.models.course.Course;

import java.util.List;

@Entity(name="student")
@Table(name="students")
@Getter
@Setter
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="student_name")
    private String studentName;

    @Column(name="student_surname")
    private String studentSurName;

    @Column(name="student_patronymic")
    private String patronymic;

    @Column(name="password")
    private String password;

    @Column(name="student_time_zone")
    private String timeZone;

    @Column(name="education_status")
    private String educationStatus;

    @Column(name="academic_group")
    private String academicGroup;

    @Column(name="student_number")
    private String studentNumber;

    @Column(name="student_email", unique = true)
    private String email;

    @OneToMany(mappedBy = "student")
    private List<Course> courseList;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "studentName = " + studentName + ", " +
                "studentSurName = " + studentSurName + ", " +
                "patronymic = " + patronymic + ", " +
                "password = " + password + ", " +
                "timeZone = " + timeZone + ", " +
                "university = " + educationStatus + ", " +
                "academic_group = " + academicGroup + ", " +
                "student_number = " + studentNumber + ", " +
                "studentEmail = " + email + ")";
    }
}
