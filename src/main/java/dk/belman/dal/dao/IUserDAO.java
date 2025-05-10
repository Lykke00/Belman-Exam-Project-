package dk.belman.dal.dao;

import dk.belman.be.User;

public interface IUserDAO {
    User getUserByWorkerId(String email) throws Exception;

    User createUser(User user) throws Exception;
}
