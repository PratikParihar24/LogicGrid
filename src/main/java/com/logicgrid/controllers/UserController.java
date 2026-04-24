package com.logicgrid.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.logicgrid.dao.UserDao;
import com.logicgrid.models.User;

@Controller
public class UserController {

    // --- REGISTRATION ROUTES ---

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegistrationForm() {
        return "register"; 
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegistration(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        
        UserDao dao = new UserDao();
        dao.saveUser(newUser);
        
        model.addAttribute("successMessage", "Welcome to LogicGrid, " + username + "! Your account is active.");
        return "index"; 
    }

    // --- LOGIN ROUTES ---

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String processLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session, 
            Model model) {
        
        UserDao dao = new UserDao();
        User authenticatedUser = dao.authenticateUser(username, password);
        
        if (authenticatedUser != null) {
            session.setAttribute("loggedInUser", authenticatedUser);
            System.out.println("LOG: Session created for " + username);
            return "redirect:/"; 
        } else {
            model.addAttribute("errorMessage", "Invalid username or password!");
            return "login";
        }
    }

    // --- LOGOUT ROUTE ---

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/";
    }
}