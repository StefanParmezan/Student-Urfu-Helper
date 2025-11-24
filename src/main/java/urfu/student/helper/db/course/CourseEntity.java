package urfu.student.helper.db.course;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import urfu.student.helper.db.student.StudentEntity;

import java.util.Objects;

@Entity
@Table(name="courses")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_category")
    private String courseCategory;

    @Column(name="course_url")
    private String courseUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @ToString.Exclude
    private StudentEntity studentEntity;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CourseEntity courseEntity = (CourseEntity) o;
        return getId() != null && Objects.equals(getId(), courseEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public CourseEntity(String courseName, String courseCategory, String courseUrl){
        this.courseName = courseName;
        this.courseCategory = courseCategory;
        this.courseUrl = courseUrl;
    }
}
