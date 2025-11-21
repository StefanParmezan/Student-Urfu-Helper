package urfu.student.helper.models.student;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name="student")
@Table(name="students")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue()
    private Long id;

    @Column(name="student_name")
    private String studentName;

    @Column(name="student_surname")
    private String studentSurName;

    @Column(name="university")
    private String university;

    @Column(name="password")
    private String password;


}
