package org.hiber.kisel.app;

import org.hiber.kisel.utils.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("START");

        try (Session session = HibernateUtil
                .getSessionFactory()
                .openSession()) {

            session.beginTransaction();
            log.info("Hibernate session opened - success");
            session.getTransaction().commit();

        } catch (Exception e) {
            log.error("Hibernate session NOT opened, exception:", e);
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
