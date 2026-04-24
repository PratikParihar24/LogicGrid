package com.logicgrid.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.logicgrid.dao.UserDao;
import com.logicgrid.models.User;

@Controller
public class UserController {

    // 1. When a user clicks a link to register, show them the form (GET)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm() {
        return "register"; // Loads register.jsp
    }

    // 2. When the user clicks "Submit" on the form, process the data (POST)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {
        
        // Step A: Package the HTML form data into our Java Model
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        
        // Step B: Hand the Model to the DAO to save in MySQL
        UserDao dao = new UserDao();
        dao.saveUser(newUser);
        
        // Step C: Send a success message back to the View
        model.addAttribute("successMessage", "Welcome to LogicGrid, " + username + "! Your account is active.");
        
        // Step D: Send them back to the home page
        return "index"; 
    }
}