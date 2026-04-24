package com.logicgrid.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    // This handles the request when a user visits the root URL (/)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showHomePage() {
        System.out.println("LOG: User has landed on the Home Page!");
        
        // This returns the name of the file (index.jsp) without the extension
        return "index"; 
    }
}