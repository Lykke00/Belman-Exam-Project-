package dk.belman.bll;

import at.favre.lib.crypto.bcrypt.BCrypt;
import dk.belman.be.User;
import dk.belman.dal.dao.IUserDAO;
import dk.belman.dal.dao.UserDAO;
import dk.belman.enums.UserRole;

public class UserManager {
    private final static int BCRYPT_COST = 12;
    private final IUserDAO userDAO;

    public UserManager(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserManager() throws Exception {
        this.userDAO = new UserDAO();
    }

    public User authenticateUser(String workerId, String password) throws Exception {
        User user = userDAO.getUserByWorkerId(workerId);
        if (user == null)
            throw new Exception("DOESNT_EXIST");

        boolean okPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash()).verified;
        if (!okPassword)
            throw new Exception("PASSWORD_INCORRECT");

        return user;
    }

    public User registerUser(User user) throws Exception {
        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, user.getPasswordHash().toCharArray());
        user.setPasswordHash(hashedPassword);

        User userExists = userDAO.getUserByWorkerId(user.getWorkerId());
        if (userExists != null)
            throw new Exception("USER_EXISTS");

        return userDAO.createUser(user);
    }

    public static void main(String[] args) throws Exception {
        User user = new User("1234S", "Lykke", "Efternavn", "password", UserRole.OPERATOR);

        UserManager userManager = new UserManager();
        try {
            userManager.registerUser(user);
            System.out.println("User registered successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        User loggedInUser = userManager.authenticateUser("1234S", "password");
        if (loggedInUser != null) {
            System.out.println("User authenticated successfully: " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
        } else {
            System.out.println("Authentication failed.");
        }
    }
}
