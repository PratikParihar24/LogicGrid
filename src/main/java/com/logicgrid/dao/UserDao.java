package com.logicgrid.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.logicgrid.models.User;
import com.logicgrid.config.HibernateUtil;

public class UserDao {

    // --- 1. SAVE USER METHOD ---
    public void saveUser(User user) {
        Transaction transaction = null;
        Session session = null; 
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            System.out.println("DAO SUCCESS: User '" + user.getUsername() + "' saved to database!");
            
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("DAO ERROR: Failed to save user.");
            e.printStackTrace();
            
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // --- 2. AUTHENTICATE USER METHOD ---
    public User authenticateUser(String username, String password) {
        Session session = null;
        User user = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM User WHERE username = :username";
            user = (User) session.createQuery(hql)
                                 .setParameter("username", username)
                                 .uniqueResult();
            
            if (user != null && user.getPassword().equals(password)) {
                System.out.println("DAO SUCCESS: User '" + username + "' authenticated!");
                return user; 
            }
            
        } catch (Exception e) {
            System.err.println("DAO ERROR: Failed to authenticate user.");
            e.printStackTrace();
            
        } finally {
            if (session != null) {
                session.close();
            }
        }
        
        System.out.println("DAO ALERT: Invalid login attempt for '" + username + "'.");
        return null; 
    }
}