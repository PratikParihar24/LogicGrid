package com.logicgrid.models;

import java.util.List;

public class Question {
    
    private int id;
    private String difficulty;
    private String type;
    private String title;
    private String codeSnippet;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    
    // --- Empty Constructor ---
    public Question() {}

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}