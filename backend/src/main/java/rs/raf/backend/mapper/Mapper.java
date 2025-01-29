package rs.raf.backend.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import rs.raf.backend.model.User;
import rs.raf.backend.requests.CreateUserRequest;
public class Mapper {

    // map CreateUserRequest to User
    public static User CreateUserRequestToUser(CreateUserRequest request, PasswordEncoder passwordEncoder){
        return new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPermissions()
        );
    }

}
