package urfu.student.helper.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtAuthToken extends UsernamePasswordAuthenticationToken {
    public JwtAuthToken(UserDetails principal, String jwt) {
        super(principal, jwt, principal.getAuthorities());
    }
}
