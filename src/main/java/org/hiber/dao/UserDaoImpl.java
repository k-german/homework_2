package org.hiber.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hiber.utils.HibernateUtil;
import org.hiber.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    public UserDaoImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public void save(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("\"save(User user)\" - user saved successfully: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("\"save(User user)\" failed: {}", user, e);
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void update(User user) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("\"update(User user)\" - user updated successfully: {}", user);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.error("\"update(User user)\" failed: {}", user, e);
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public int deleteById(Integer id) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSessionFactory().openSession();
            transaction = session.beginTransaction();
            int result = session.createQuery("DELETE FROM User u WHERE u.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            transaction.commit();
            logger.info("\"deleteById(Integer id)\" - successfully. Id = {}, deleting result: {}",
                    id, result);
            return result;

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("\"deleteById(Integer id)\" failed: {}", id, e);
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public User findById(Integer id) {
        try (Session session = getSessionFactory().openSession()) {
            return session.find(User.class, id);
        }
    }

    @Override
    public User findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }
}
