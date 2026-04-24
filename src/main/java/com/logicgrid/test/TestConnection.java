package com.logicgrid.test;

import com.logicgrid.config.HibernateUtil;
import org.hibernate.SessionFactory;

public class TestConnection {

    public static void main(String[] args) {
        
        System.out.println("Starting Hibernate Connection Test...");
        
        // This single line wakes up Hibernate and tells it to read the XML file
        SessionFactory factory = HibernateUtil.getSessionFactory();
        
        if (factory != null) {
            System.out.println("============================================");
            System.out.println("SUCCESS! Hibernate is talking to MySQL.");
            System.out.println("Check your database to see the new tables!");
            System.out.println("============================================");
        } else {
            System.err.println("FAILED! Check your console for the error.");
        }
    }
}