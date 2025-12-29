package org.hiber.dao;

import org.hiber.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIT {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:18.1")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    private UserDao userDao;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUpAll() {
        POSTGRES_CONTAINER.start();

        Configuration configuration = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.connection.url", POSTGRES_CONTAINER.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", POSTGRES_CONTAINER.getUsername());
        configuration.setProperty("hibernate.connection.password", POSTGRES_CONTAINER.getPassword());

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @BeforeEach
    void cleanDatabase() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        POSTGRES_CONTAINER.stop();
    }

    @Test // success
//    @Order(1)
    void testSaveUser() {
        User user = new User("TestUser", "testEmail@test.com", 25);
        userDao.save(user);

        User fromDb = userDao.findByEmail("testEmail@test.com");
        assertNotNull(fromDb);
        assertEquals("TestUser", fromDb.getName());
        assertEquals(25, fromDb.getAge());
    }

    // FAIL - user == null
    @Test
//    @Order(2)
    void testSaveUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.save(null));
    }

    // FAIL - duplicate email
    @Test
//    @Order(3)
    void testSaveUser_DuplicateEmail() {
        User first = new User("User1", "duplicate@example.com", 30);
        userDao.save(first);
        User duplicate = new User("User2", "duplicate@example.com", 28);
        assertThrows(Exception.class, () -> userDao.save(duplicate),
                "Exception thrown - duplicate email (EmailAlreadyExistsException)");
    }

    // FAIL - name == null
    // lombok annotation @NonNull in entity User.java
    @Test
//    @Order(4)
    void testSaveUser_NullName() {
//        User user = new User(null, "nullname@test.com", 20);
        assertThrows(NullPointerException.class,
                () -> new User(null, "nullname@test.com", 20));
//        assertThrows(NullPointerException.class, () -> userDao.save(user),
//                "Exception thrown - name == null (@NonNull annotation)");
    }


}