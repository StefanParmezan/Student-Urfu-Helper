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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="student_name")
    private String studentName;

    @Column(name="student_surname")
    private String studentSurName;

    @Column(name="password")
    private String password;

    @Column(name="university")
    private String university;

    @Column(name="student_phone_number", unique = true)
    private String phoneNumber;

    @Column(name="student_email", unique = true)
    private String studentEmail;

    public Student(String studentName, String studentSurName, String password, String phoneNumber){
        this.studentName = studentName;
        this.studentSurName = studentSurName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
