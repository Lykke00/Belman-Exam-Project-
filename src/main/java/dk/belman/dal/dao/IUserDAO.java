package dk.belman.dal.dao;

import dk.belman.be.User;

import java.util.List;

public interface IUserDAO {
    User getUserByWorkerId(String email) throws Exception;

    User createUser(User user) throws Exception;

    List<User> getAllUsers() throws Exception;

    boolean editUser(User newData) throws Exception;

    boolean updatePassword(User user, String newPassword) throws Exception;

    boolean updateUserStatus(User user, boolean status) throws Exception;
}
