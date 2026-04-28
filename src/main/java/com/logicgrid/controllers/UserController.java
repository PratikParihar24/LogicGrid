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
            HttpSession session, 
            Model model) {
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEloRating(1000); 
        
        UserDao dao = new UserDao();
        
        // 1. Capture the boolean response from our upgraded DAO
        boolean isSuccess = dao.saveUser(newUser);
        
        if (isSuccess) {
            // 🟢 SUCCESS: The database confirmed the save. Grant the VIP pass.
            session.setAttribute("loggedInUser", newUser);
            System.out.println("LOG: Auto-login session created for REAL user " + username);
            return "redirect:/lobby"; 
            
        } else {
            // 🔴 FAILURE: Name is taken, too long, or DB crashed. Deny entry!
            System.out.println("LOG: Registration blocked for " + username + ". Returning to form.");
            model.addAttribute("errorMessage", "Registration failed. Username may already be taken.");
            return "register"; // Send them back to the UI to try again
        }
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