package org.hiber.kisel.utils;

import lombok.Getter;
import org.hiber.kisel.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {

    private static final Logger log =
            LoggerFactory.getLogger(HibernateUtil.class);

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            log.info("Hibernate SessionFactory init");

            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();

        } catch (Exception ex) {
            log.error("Hibernate SessionFactory init failed", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        log.info("Hibernate SessionFactory shutdown");
        getSessionFactory().close();
    }
}
