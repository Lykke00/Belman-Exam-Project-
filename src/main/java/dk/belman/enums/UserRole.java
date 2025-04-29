package dk.belman.enums;

public enum UserRole {
    ADMIN("Administrator", 3),
    INSPECTOR("Inspector", 2),
    OPERATOR("Operator", 1);


    private final String role;
    private final int id;

    UserRole(String role, int id) {
        this.role = role;
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public int getId() {
        return id;
    }

    public static UserRole fromRole(String role) {
        for (UserRole roles : values()) {
            if (roles.getRole().equals(role))
                return roles;
        }
        return null;
    }

    @Override
    public String toString() {
        return role;
    }
}

