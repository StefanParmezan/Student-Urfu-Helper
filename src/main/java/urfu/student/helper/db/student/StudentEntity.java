package urfu.student.helper.db.student;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import urfu.student.helper.db.chat.ChatEntity;
import urfu.student.helper.db.course.CourseEntity;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity(name="student")
@Table(name="students")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name="student_fio")
    private String fio;

    @Column(name="password")
    private String password;

    @Column(name="student_time_zone")
    @Convert(converter = Jsr310JpaConverters.ZoneIdConverter.class)
    private ZoneId timeZone;

    @Column(name="education_status")
    private EducationStatus educationStatus;

    @Column(name="academic_group")
    private String academicGroup;

    @Column(name="student_number")
    private String studentNumber;

    @Column(name="student_email", unique = true)
    private String email;

    @OneToMany(mappedBy = "studentEntity")
    @ToString.Exclude
    private List<CourseEntity> courseEntityList;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    private List<ChatEntity> chats;

    public void setTimeZone(String timeZone) {
        this.timeZone = ZoneId.of(timeZone);
    }

    public void setEducationStatus(String educationStatus) {
        this.educationStatus = EducationStatus.getByName(educationStatus);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        StudentEntity studentEntity = (StudentEntity) o;
        return getId() != null && Objects.equals(getId(), studentEntity.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @AllArgsConstructor
    public enum EducationStatus {
        BAKALAVRIAT("Бакалавр"),
        SPECIALITET("Специалитет"),
        MAGISTRATURA("Магистратура");

        @Getter
        private final String name;

        private static final Map<String, EducationStatus> LOOKUP_MAP = new HashMap<>();

        static {
            for (EducationStatus value : values()) {
                LOOKUP_MAP.put(value.getName().toLowerCase(), value);
            }
        }

        public static EducationStatus getByName(String name) {
            return LOOKUP_MAP.get(name.toLowerCase());
        }
    }


}
