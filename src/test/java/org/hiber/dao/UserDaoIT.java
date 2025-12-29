package org.hiber.dao;

import org.hiber.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIT {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private UserDao userDao;
    private SessionFactory sessionFactory;

    @BeforeAll
    void setUpAll() {
        POSTGRES_CONTAINER.start();

        Configuration configuration = new Configuration()
                .configure("hibernate.cfg.xml") // существующий файл
                .addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.connection.url", POSTGRES_CONTAINER.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", POSTGRES_CONTAINER.getUsername());
        configuration.setProperty("hibernate.connection.password", POSTGRES_CONTAINER.getPassword());

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        POSTGRES_CONTAINER.stop();
    }

    @Test
    @Order(1)
    void testSaveUser() {
        User user = new User("TestUser", "testEmail@test.com", 25);
        userDao.save(user);

        User fromDb = userDao.findByEmail("testEmail@test.com");
        assertNotNull(fromDb);
        assertEquals("TestUser", fromDb.getName());
        assertEquals(25, fromDb.getAge());
    }
}