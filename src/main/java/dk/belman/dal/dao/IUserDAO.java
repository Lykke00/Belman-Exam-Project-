package dk.belman.dal.dao;

import dk.belman.be.User;

import java.util.List;

public interface IUserDAO {
    User getUserByWorkerId(String email) throws Exception;

    User createUser(User user) throws Exception;

    List<User> getAllUsers() throws Exception;
}
