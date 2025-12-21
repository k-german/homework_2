package org.hiber.kisel.app;

import org.hiber.kisel.dao.UserDao;
import org.hiber.kisel.dao.UserDaoImpl;
import org.hiber.kisel.entity.User;
import org.hiber.kisel.utils.HibernateUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        UserDao userDao = new UserDaoImpl();

        User user = new User("UserOne", "one@mail.ru", 11);
        userDao.save(user);

        User foundUser = userDao.findById(user.getId());
        System.out.println("Found User name: " + foundUser.getName());

//        foundUser.setEmail("one-co3rrect@mail.ru");
        userDao.update(foundUser);

        userDao.findAll().forEach(u -> System.out.println(u.getName() + " - " +
                u.getAge() + " - " + u.getEmail() +
                " - id:" + u.getId() + " - " + u.getCreatedAt()));

//        userDao.delete(foundUser);

        HibernateUtil.shutdown();

//        log.info("START");
//
//        try (Session session = HibernateUtil
//                .getSessionFactory()
//                .openSession()) {
//
//            session.beginTransaction();
//            log.info("Hibernate session opened - success");
//            session.getTransaction().commit();
//
//        } catch (Exception e) {
//            log.error("Hibernate session NOT opened, exception:", e);
//        } finally {
//            HibernateUtil.shutdown();
//        }
    }
}
