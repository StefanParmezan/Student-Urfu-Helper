package urfu.student.helper.security.dto;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import urfu.student.helper.db.student.StudentEntity;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class StudentAuthentificationPrincipal implements UserDetails {
    private final StudentEntity student;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    public String getUsername() {
        return student.getEmail();
    }

    public Long getId() {
        return student.getId();
    }
}
