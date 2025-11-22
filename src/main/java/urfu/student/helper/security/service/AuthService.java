package urfu.student.helper.security.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.security.client.UrfuApiConnector;
import urfu.student.helper.security.dto.AuthRequest;

@Service
@AllArgsConstructor
public class AuthService {
    private final UrfuApiConnector urfuApiConnector;

    public StudentEntity fuckingPhpStudentSaveAndAuth(AuthRequest authRequest){
        String phpDocument = urfuApiConnector.authenticateAndGetProfile(authRequest.email(), authRequest.password());

    }

}
