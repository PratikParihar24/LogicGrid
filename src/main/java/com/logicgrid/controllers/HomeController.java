package com.logicgrid.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.logicgrid.dao.UserDao;
import com.logicgrid.models.User;
import com.logicgrid.models.MatchRecord;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class HomeController {

    private UserDao userDao = new UserDao(); // Initialize our DAO

    // 1. Landing Page
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showHomePage() {
        System.out.println("LOG: User has landed on the Home Page!");
        return "index"; 
    }

    // 2. The Lobby (Where the Match History lives)
    @RequestMapping(value = "/lobby", method = RequestMethod.GET)
    public String showLobby(HttpSession session, Model model) {
        // Retrieve the logged-in user from the session
        User user = (User) session.getAttribute("loggedInUser");

        // If no user is logged in, kick them back to index/login
        if (user == null) {
            return "redirect:/";
        }

        System.out.println("LOG: " + user.getUsername() + " is viewing the Lobby.");

        // --- 📊 FETCH MATCH HISTORY ---
        // We use the method we added to UserDao to get the last 10 matches
        List<MatchRecord> history = userDao.getMatchHistory(user.getId());

        // Attach the history list to the 'model' so the JSP can see it
        model.addAttribute("matchHistory", history);

        // Return the lobby.jsp file
        return "lobby";
    }
}