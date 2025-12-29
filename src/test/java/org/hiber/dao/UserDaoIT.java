package org.hiber.dao;

import org.hiber.entity.User;
import org.hiber.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIT {

    private static PostgreSQLContainer<?> postgresContainer;
    private UserDao userDao;

    @BeforeAll
    static void startContainer() {
        postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
                .withDatabaseName("testdb")
                .withUsername("user")
                .withPassword("pass");
        postgresContainer.start();

        System.setProperty("JDBC_URL", postgresContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgresContainer.getPassword());

        HibernateUtil.getSessionFactory();
    }

    @AfterAll
    static void stopContainer() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
        HibernateUtil.shutdown();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        // Очистка таблицы перед каждым тестом
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.flush();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    void testSaveAndFindById() {
        User user = new User("User Userone", "uone@one.com", 30);
        userDao.save(user);

        User saved = userDao.findById(user.getId());
        Assertions.assertNotNull(saved, "Пользователь должен быть сохранен");
        Assertions.assertEquals("User Userone", saved.getName());
        Assertions.assertEquals("uone@one.com", saved.getEmail());
    }

    @Test
    @Order(2)
    void testUpdate() {
        User user = new User("Second", "second@user.com", 25);
        userDao.save(user);

        user.setAge(26);
        userDao.update(user);

        User updated = userDao.findById(user.getId());
        Assertions.assertEquals(26, updated.getAge());
    }

    @Test
    @Order(3)
    void testDeleteById() {
        User user = new User("Third", "Third@user.com", 28);
        userDao.save(user);

        int deleted = userDao.deleteById(user.getId());
        Assertions.assertEquals(1, deleted);

        User notFound = userDao.findById(user.getId());
        Assertions.assertNull(notFound);
    }

    @Test
    @Order(4)
    void testFindByEmail() {
        User user = new User("Fourth", "fourth@user.com", 35);
        userDao.save(user);

        User found = userDao.findByEmail("fourth@user.com");
        Assertions.assertNotNull(found);
        Assertions.assertEquals("Fourth", found.getName());
    }

    @Test
    @Order(5)
    void testFindAll() {
        User user1 = new User("Fifth", "fifth@user.com", 20);
        User user2 = new User("Sixth", "sixth@user.com", 22);
        userDao.save(user1);
        userDao.save(user2);

        List<User> allUsers = userDao.findAll();
        Assertions.assertEquals(2, allUsers.size());
    }
}
