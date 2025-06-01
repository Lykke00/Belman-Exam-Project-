package dk.belman.be;

import dk.belman.enums.UserRole;

public class User {
    private int id;
    private String workerId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private boolean isActive;

    public User() {
    }

    public User(int id, String workerId, String firstName, String lastName, String email, String password, String role) {
        this.id = id;
        this.workerId = workerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = UserRole.fromRole(role);
    }

    public User(int id, String workerId, String firstName, String lastName, UserRole role, boolean isActive) {
        this.id = id;
        this.workerId = workerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.isActive = isActive;
    }

    public User(int id, String workerId, String firstName, String lastName, String role, boolean isActive) {
        this.id = id;
        this.workerId = workerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = UserRole.fromRole(role);
        this.isActive = isActive;
    }

    public User(String workerId, String firstName, String lastName, String password, UserRole role) {
        this.workerId = workerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }

    public User(int id, String workerId, String firstName, String lastName, String password, String role, boolean isActive) {
        this.id = id;
        this.workerId = workerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.role = UserRole.fromRole(role);
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getPasswordHash() {
        return password;
    }

    public void setPasswordHash(String hashedPassword) {
        this.password = hashedPassword;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
