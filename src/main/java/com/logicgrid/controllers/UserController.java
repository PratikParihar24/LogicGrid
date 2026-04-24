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
            HttpSession session, // Added Session for Auto-Login
            Model model) {
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEloRating(1000); // CRITICAL FIX: Give them a starting rating so DB saves them!
        
        UserDao dao = new UserDao();
        dao.saveUser(newUser);
        
        // Auto-Login and send directly to Lobby
        session.setAttribute("loggedInUser", newUser);
        System.out.println("LOG: Auto-login session created for new user " + username);
        
        return "redirect:/lobby"; 
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
            
            return "redirect:/lobby"; // FIX: Point to lobby, not "/"
        } else {
            model.addAttribute("errorMessage", "Invalid username or password!");
            return "login";
        }
    }

    // --- LOGOUT ROUTE ---

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/"; // Safely kick them back to the landing page
    }
}