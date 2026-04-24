package com.logicgrid.dao;

// EXACT IMPORTS REQUIRED
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.logicgrid.models.User;
import com.logicgrid.config.HibernateUtil;

public class UserDao {

    public void saveUser(User user) {
        Transaction transaction = null;
        Session session = null; // Declare it up here now
        
        try {
            // Open the session manually
            session = HibernateUtil.getSessionFactory().openSession();
            
            // Start the transaction
            transaction = session.beginTransaction();
            
            // Save the object
            session.save(user);
            
            // Lock it in
            transaction.commit();
            System.out.println("DAO SUCCESS: User '" + user.getUsername() + "' saved to database!");
            
        } catch (Exception e) {
            // Roll back if it fails
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("DAO ERROR: Failed to save user.");
            e.printStackTrace();
            
        } finally {
            // BULLETPROOF CLOSING: Guarantee the session closes, avoiding the AutoCloseable error
            if (session != null) {
                session.close();
            }
        }
    }
}