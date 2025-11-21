package urfu.student.helper.models.student;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import urfu.student.helper.models.chat.ChatEntity;
import urfu.student.helper.models.course.Course;

import java.util.List;
import java.util.Objects;

@Entity(name="student")
@Table(name="students")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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
    @ToString.Exclude
    private List<Course> courseList;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private List<ChatEntity> chats;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Student student = (Student) o;
        return getId() != null && Objects.equals(getId(), student.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
