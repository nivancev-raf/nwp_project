package rs.raf.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.raf.backend.model.User;
import rs.raf.backend.model.UserTypes;
import rs.raf.backend.repository.UserRepository;
import rs.raf.backend.requests.CreateUserRequest;
import rs.raf.backend.utils.UserAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static rs.raf.backend.mapper.Mapper.CreateUserRequestToUser;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByEmail(email);
        if(myUser == null) {
            throw new UsernameNotFoundException("User with email  "+email+" not found");
        }

        // SimpleGrantedAuthority is a class that implements the GrantedAuthority interface which is used to represent an authority granted to an Authentication object.
        List<SimpleGrantedAuthority> permissions = getUserPermissions(myUser.getId())
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        System.out.println("permissions1: " + permissions);
        return org.springframework.security.core.userdetails.User.withUsername(myUser.getEmail())
                .password(myUser.getPassword())
                .authorities(permissions)
                .build();

    }

    // findByEmail
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // getUserPermissions
    public Set<String> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPermissions();
    }

    // get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // create user
    public User createUser(CreateUserRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }

        // if user has all permissions, set role to admin, else set role to user
        // namesteno je da je korisnik admin ukoliko ima sve permisije
        if (user.getPermissions().size() == 9) {
            user.setRole(String.valueOf(UserTypes.ADMIN));
        } else {
            user.setRole(String.valueOf(UserTypes.USER));
        }

        return userRepository.save(CreateUserRequestToUser(user, passwordEncoder));
    }

    // get user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // update user by id
    public User updateUserById(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            // in else case, we don't want to change the password
        }
        user.setPermissions(request.getPermissions());
        return userRepository.save(user);
    }

    // delete user by id
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
