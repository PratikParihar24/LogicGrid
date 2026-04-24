package com.logicgrid.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.logicgrid.models.User;

@Controller
public class ArenaController {

    // The Gateway to the Game
    @RequestMapping(value = "/lobby", method = RequestMethod.GET)
    public String showLobby(HttpSession session, Model model) {
        
        // 1. SECURITY CHECK: Grab the VIP Wristband
        User currentPlayer = (User) session.getAttribute("loggedInUser");
        
        // 2. THE BOUNCER: If they don't have a wristband, kick them to login
        if (currentPlayer == null) {
            System.out.println("SECURITY ALERT: Unauthenticated access attempt to /lobby");
            return "redirect:/login"; 
        }
        
        // 3. THE VIP LOUNGE: If they are logged in, let them through
        System.out.println("LOG: " + currentPlayer.getUsername() + " entered the Lobby.");
        
        // We will eventually load Match History from the database here
        
        return "lobby"; // Directs them to lobby.jsp (which we will build next)
    }
    
 // The Gateway to the Matchmaking Queue
    @RequestMapping(value = "/matchmake", method = RequestMethod.GET)
    public String enterMatchmaking(HttpSession session, Model model) {
        
        // 1. Check the Wristband
        User currentPlayer = (User) session.getAttribute("loggedInUser");
        if (currentPlayer == null) {
            return "redirect:/login"; 
        }
        
        // 2. Log that they clicked the button
        System.out.println("MATCHMAKER: " + currentPlayer.getUsername() + " has entered the queue!");
        
        // 3. Send them to the Arena page
        return "arena"; 
    }
    
}