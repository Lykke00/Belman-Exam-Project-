package dk.belman.dal.dao;

import dk.belman.be.User;
import dk.belman.dal.DBConnector;
import dk.belman.dal.IDBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private final IDBConnector dbConnector;

    public UserDAO(IDBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    public UserDAO() throws Exception {
        this.dbConnector = new DBConnector();
    }

    @Override
    public User getUserByWorkerId(String workerId) throws Exception {
        String query = """
                SELECT users.id, users.workerId, users.firstName, users.lastName, users.password, users_roles.role as role_name 
                FROM users
                JOIN users_roles ON users.role = users_roles.id
                WHERE users.workerId = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, workerId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String workerId2 = rs.getString("workerId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String passwordHash = rs.getString("password");
                String role = rs.getString("role_name");

                return new User(id, workerId2, firstName, lastName, passwordHash, role);
            }

            return null;
        } catch (Exception e) {
            throw new Exception("User couldn't be found", e);
        }
    }

    @Override
    public User createUser(User user) throws Exception {
        String query = """
                INSERT INTO users (workerId, firstName, lastName, password, role) 
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getWorkerId());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getPasswordHash());
            stmt.setInt(5, getRoleIdByName(conn, user.getRole().getRole()));

            int rowsAffected = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

            return user;
        } catch (Exception e) {
            throw new Exception("User couldn't be registered", e);
        }
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();

        String query = """
                    SELECT * FROM users;
                """;

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String workerId2 = rs.getString("workerId");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String role = getRoleNameById(conn, rs.getInt("role"));

                // står som en BIT i db så 1 == true og 0 == false
                boolean active = rs.getBoolean("active");

                users.add(new User(id, workerId2, firstName, lastName, role, active));
            }

            return users;
        } catch (Exception e) {
            throw new Exception("Users couldn't be retrieved", e);
        }
    }

    private int getRoleIdByName(Connection conn, String roleName) throws Exception {
        String query = "SELECT id FROM users_roles WHERE role = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roleName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new Exception("Role not found: " + roleName);
            }
        }
    }

    private String getRoleNameById(Connection conn, int roleId) throws Exception {
        String query = "SELECT role FROM users_roles WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roleId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
                throw new Exception("Role id not found: " + roleId);
            }
        }
    }
}
