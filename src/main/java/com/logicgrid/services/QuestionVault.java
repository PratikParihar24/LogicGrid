package com.logicgrid.services;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logicgrid.models.Question;

public class QuestionVault {

    // This list lives in the server's RAM. Ultra-fast access.
    private static List<Question> allQuestions = new ArrayList<>();

    // This "static block" runs exactly once when the server boots up
    static {
        try {
            System.out.println("VAULT: Initializing Problem Vault...");
            
            // 1. Find the questions.json file in the resources folder
            Reader reader = new InputStreamReader(
                QuestionVault.class.getClassLoader().getResourceAsStream("questions.json")
            );
            
            // 2. Wake up Google Gson
            Gson gson = new Gson();
            
            // 3. Tell Gson we want a List of Question objects
            Type listType = new TypeToken<ArrayList<Question>>(){}.getType();
            
            // 4. Translate!
            allQuestions = gson.fromJson(reader, listType);
            
            System.out.println("VAULT SUCCESS: Loaded " + allQuestions.size() + " questions into memory for battle.");
            
        } catch (Exception e) {
            System.err.println("VAULT ERROR: Failed to load questions.json. Check the file path or JSON syntax.");
            e.printStackTrace();
        }
    }

    // --- Helper Methods for the Game ---

    // Get all questions
    public static List<Question> getAllQuestions() {
        return allQuestions;
    }

    // Get a specific question by ID
    public static Question getQuestionById(int id) {
        for (Question q : allQuestions) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    // Grab a random 3-question "Gauntlet" for a match
    public static List<Question> getGauntlet() {
        List<Question> gauntlet = new ArrayList<>();
        Random rand = new Random();
        
        // Make sure we don't try to pull 3 questions if we only have 1 or 2 in the file
        int limit = Math.min(3, allQuestions.size()); 
        
        List<Question> copyList = new ArrayList<>(allQuestions);
        
        for(int i = 0; i < limit; i++) {
            int randomIndex = rand.nextInt(copyList.size());
            gauntlet.add(copyList.remove(randomIndex)); // Add to gauntlet and remove from copy so no duplicates
        }
        return gauntlet;
    }
}