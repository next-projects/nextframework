package org.erplite.controller;

import org.nextframework.controller.Controller;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.MultiActionController;

@Controller(path = "/app/home")
public class AppHomeController extends MultiActionController {

    @DefaultAction
    public String home() {
        return "app_home";
    }

}
