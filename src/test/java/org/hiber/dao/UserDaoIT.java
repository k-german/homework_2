package org.hiber.dao;

import org.hiber.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoIT {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:18.1")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");

    private UserDao userDao;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUpAll() {
        Configuration configuration = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);

        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test // success
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
    void testSaveUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.save(null));
    }

    // FAIL - duplicate email
    @Test
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
    void testSaveUser_NullName() {
        assertThrows(NullPointerException.class,
                () -> new User(null, "nullname@test.com", 20));
    }

}