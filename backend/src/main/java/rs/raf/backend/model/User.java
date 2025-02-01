package rs.raf.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;


    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column
    private UserTypes role;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) // ovo znaci da ce se ucitavati odmah zajedno sa userom
    @CollectionTable(name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    private Set<String> permissions;

    public User() {
    }

    public User(String firstName, String lastName, String email, String role, String password, Set<String> permissions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = UserTypes.valueOf(role);
        this.password = password;
        this.permissions = permissions;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public UserTypes getRole() {
        return role;
    }

    public void setRole(UserTypes role) {
        this.role = role;
    }
}