package org.erplite.dao;

import org.erplite.entity.User;
import org.nextframework.authorization.impl.AbstractAuthorizationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorizationDAO extends AbstractAuthorizationDAO {

    @Autowired
    private UserDAO userDAO;

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findByPropertyUnique("username", username);
    }

}
