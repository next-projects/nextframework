package org.erplite.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.nextframework.controller.Action;
import org.nextframework.controller.Controller;

@Controller(path = "/public/login")
public class LoginController extends org.nextframework.authorization.LoginController {

    @Action("logout")
    public String doLogout() {
        getRequest().getSession().invalidate();
        return redirectToAction("doPage");
    }

    @Override
    protected boolean validPassword(String persisted, String provided) {
        return BCrypt.checkpw(provided, persisted);
    }

    @Override
    protected String afterLoginRedirectTo() {
        return "/app/home";
    }

}
