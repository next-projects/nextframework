package org.erplite.service;

import org.erplite.dao.UserDAO;
import org.erplite.entity.User;
import org.nextframework.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends GenericService<User> {

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        super.setGenericDAO(userDAO);
    }

}
