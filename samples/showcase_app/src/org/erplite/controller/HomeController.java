package org.erplite.controller;

import org.nextframework.controller.Controller;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.MultiActionController;

@Controller(path = "/public/home")
public class HomeController extends MultiActionController {

    @DefaultAction
    public String home() {
        return "home";
    }

}
