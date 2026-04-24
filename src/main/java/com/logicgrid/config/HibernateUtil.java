package com.logicgrid.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Read the hibernate.cfg.xml file
                Configuration configuration = new Configuration().configure();
                
                // Build the connection pool
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                
                // Open the factory
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                System.out.println("Hibernate successfully connected to MySQL!");
                
            } catch (Exception e) {
                System.err.println("Hibernate Connection Failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}