package rs.raf.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.raf.backend.dto.UserDTO;
import rs.raf.backend.model.User;
import rs.raf.backend.requests.CreateUserRequest;
import rs.raf.backend.services.UserService;
import rs.raf.backend.utils.UserAlreadyExistsException;
import rs.raf.backend.model.ErrorResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('can_read_users')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAuthority('can_create_users')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('can_read_users')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null,  // ne šaljemo password
                user.getPermissions()
        );
        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasAuthority('can_update_users')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody CreateUserRequest request) {
        User user = userService.updateUserById(id, request);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('can_delete_users')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}