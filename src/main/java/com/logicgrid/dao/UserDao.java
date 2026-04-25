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
            System.err.println("--- FATAL DAO ERROR DURING REGISTRATION ---");
            e.printStackTrace();
            
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rbe) {
                    System.err.println("DAO ALERT: Rollback failed during Save User, but we caught it.");
                }
            }
        } finally {
            if (session != null) {
                session.close(); // Leak prevented!
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
                session.close(); // Leak prevented!
            }
        }
        
        System.out.println("DAO ALERT: Invalid login attempt for '" + username + "'.");
        return null; 
    }

    // --- 3. UPDATE USER METHOD ---
 // --- 3. UPDATE USER METHOD (Bulletproof Fetch-and-Update) ---
    public void updateUser(User user) {
        Transaction transaction = null;
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // 1. Fetch the LIVE database row using the unique username
            User liveUser = (User) session.createQuery("FROM User WHERE username = :username")
                                          .setParameter("username", user.getUsername())
                                          .uniqueResult();
            
            if (liveUser != null) {
                // 2. Transfer the new game stats to the live database object
                liveUser.setEloRating(user.getEloRating());
                liveUser.setWins(user.getWins());
                liveUser.setMatchesPlayed(user.getMatchesPlayed());
                
                // 3. Save the live row
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
    
    // --- 4. SAVE MATCH RECORD ---
    public void saveMatchRecord(com.logicgrid.models.MatchRecord record) {
        Session session = null;
        Transaction transaction = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(record);
            transaction.commit();
            System.out.println("DAO SUCCESS: Match History Record Saved for " + record.getPlayer().getUsername());
            
        } catch (Exception e) {
            System.err.println("--- FATAL DAO ERROR DURING MATCH SAVE ---");
            e.printStackTrace();
            
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rbe) {
                    System.err.println("DAO ALERT: Rollback failed during Match Save, but we caught it.");
                }
            }
        } finally {
            if (session != null) {
                session.close(); // Leak prevented!
            }
        }
    }
    
 // --- GET MATCH HISTORY FOR USER ---
 // --- GET MATCH HISTORY FOR USER (Hibernate 4.3 Fix) ---
    public java.util.List<com.logicgrid.models.MatchRecord> getMatchHistory(int userId) {
        org.hibernate.Session session = null;
        java.util.List<com.logicgrid.models.MatchRecord> history = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            // In Hibernate 4, we don't pass the .class to createQuery
            String hql = "FROM MatchRecord WHERE player.id = :userId ORDER BY matchDate DESC";
            org.hibernate.Query query = session.createQuery(hql);
            
            query.setParameter("userId", userId);
            query.setMaxResults(10); 
            
            // We cast the result list manually
            history = (java.util.List<com.logicgrid.models.MatchRecord>) query.list();
            
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