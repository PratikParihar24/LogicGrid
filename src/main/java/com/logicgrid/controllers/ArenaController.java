package com.logicgrid.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.logicgrid.models.User;

@Controller
public class ArenaController {

    
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