package com.logicgrid.test;

import com.logicgrid.dao.UserDao;
import com.logicgrid.models.User;

public class TestUserDao {

    public static void main(String[] args) {
        
        System.out.println("Starting Backend Data Insertion Test...");

        // 1. Create a dummy user
        User testPlayer = new User();
        testPlayer.setUsername("TestGladiator");
        testPlayer.setPassword("admin123"); 
        // Note: Elo rating should default to 1000 based on your Model!

        // 2. Instantiate the DAO
        UserDao dao = new UserDao();

        // 3. Command the DAO to save the user
        dao.saveUser(testPlayer);

        System.out.println("============================================");
        System.out.println("Test finished. Please check your MySQL database!");
        System.out.println("============================================");
    }
}