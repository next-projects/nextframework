package org.erplite.controller;

import org.erplite.entity.User;
import org.erplite.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.nextframework.controller.Controller;
import org.nextframework.controller.crud.CrudController;
import org.nextframework.controller.crud.ListViewFilter;
import org.nextframework.core.web.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;

@Controller(path = "/app/users")
public class UserCrudController extends CrudController<ListViewFilter, User, User> {

    @Autowired
    private UserService userService;

    @Override
    protected void save(WebRequestContext request, User user) throws Exception {
        // Load existing user if editing
        User existing = user.getId() != null ? userService.loadById(user.getId()) : null;

        // Set createdAt: preserve from server if editing, set to now if new
        if (existing != null) {
            user.setCreatedAt(existing.getCreatedAt());
        } else {
            user.setCreatedAt(new java.util.Date());
        }

        // Handle password
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        } else if (!password.startsWith("$2a$")) {
            // Hash new password
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
        }

        super.save(request, user);
    }

}
