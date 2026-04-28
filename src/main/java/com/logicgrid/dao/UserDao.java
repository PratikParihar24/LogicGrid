package com.logicgrid.dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.Query;
import org.hibernate.exception.ConstraintViolationException;

import com.logicgrid.models.User;
import com.logicgrid.models.MatchRecord;
import com.logicgrid.config.HibernateUtil;

public class UserDao {

    // --- 1. REGISTER NEW USER (Bulletproof Commit & Report Strategy) ---
    public boolean saveUser(User user) {
        Transaction transaction = null;
        Session session = null; 
        boolean isSuccess = false; 
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            session.save(user);
            transaction.commit();
            
            System.out.println("DAO SUCCESS: User '" + user.getUsername() + "' saved to database!");
            isSuccess = true; 
            
        } catch (ConstraintViolationException cve) {
            System.err.println("DAO ERROR: Username '" + user.getUsername() + "' already exists!");
            if (transaction != null) transaction.rollback();
        } catch (Exception e) {
            System.err.println("--- FATAL DAO ERROR DURING REGISTRATION ---");
            e.printStackTrace();
            
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rbe) {
                    System.err.println("DAO ALERT: Rollback failed during Save User.");
                }
            }
        } finally {
            if (session != null) {
                session.close(); 
            }
        }
        
        return isSuccess; 
    }

    // --- 2. AUTHENTICATE USER ---
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

    // --- 3. UPDATE USER (Bulletproof Fetch-and-Update) ---
    public void updateUser(User user) {
        Transaction transaction = null;
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            User liveUser = (User) session.createQuery("FROM User WHERE username = :username")
                                          .setParameter("username", user.getUsername())
                                          .uniqueResult();
            
            if (liveUser != null) {
                liveUser.setEloRating(user.getEloRating());
                liveUser.setWins(user.getWins());
                liveUser.setMatchesPlayed(user.getMatchesPlayed());
                
                session.update(liveUser); 
                transaction.commit();
                System.out.println("DAO SUCCESS: User '" + user.getUsername() + "' updated in DB!");
            } else {
                System.err.println("DAO ERROR: Could not find user '" + user.getUsername() + "' to update.");
            }
            
        } catch (Exception e) {
            System.err.println("--- FATAL DAO ERROR DURING UPDATE ---");
            e.printStackTrace();
            
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rbe) {
                    System.err.println("DAO ALERT: Rollback failed during Update User.");
                }
            }
        } finally {
            if (session != null) {
                session.close(); 
            }
        }
    }
    
    // --- 4. SAVE MATCH RECORD (Bulletproof Link Strategy) ---
    public void saveMatchRecord(MatchRecord record) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            String username = record.getPlayer().getUsername();
            User liveUser = (User) session.createQuery("FROM User WHERE username = :username")
                                          .setParameter("username", username)
                                          .uniqueResult();
            
            if (liveUser != null) {
                record.setPlayer(liveUser);
                
                session.save(record);
                transaction.commit();
                System.out.println("DAO SUCCESS: Match History Record Saved for " + username);
            } else {
                System.err.println("DAO ERROR: Could not find live user '" + username + "' to link match record.");
            }
            
        } catch (Exception e) {
            System.err.println("--- FATAL DAO ERROR DURING MATCH SAVE ---");
            e.printStackTrace();
            
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rbe) {
                    System.err.println("DAO ALERT: Rollback failed during Match Save.");
                }
            }
        } finally {
            if (session != null) {
                session.close(); 
            }
        }
    }
    
    // --- 5. GET MATCH HISTORY FOR USER (Hibernate 4.3 Fix) ---
    public List<MatchRecord> getMatchHistory(int userId) {
        Session session = null;
        List<MatchRecord> history = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String hql = "FROM MatchRecord WHERE player.id = :userId ORDER BY matchDate DESC";
            Query query = session.createQuery(hql);
            
            query.setParameter("userId", userId);
            query.setMaxResults(10); 
            
            history = (List<MatchRecord>) query.list();
            
        } catch (Exception e) {
            System.err.println("DAO ERROR: Failed to fetch match history.");
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return history;
    }
}