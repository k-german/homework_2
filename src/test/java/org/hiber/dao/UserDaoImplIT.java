package org.hiber.dao;

import org.hiber.entity.User;
import org.hiber.utils.HibernateUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplIT {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("12345678");

    private UserDaoImpl userDao;

    @BeforeAll
    void setup() {
        postgresContainer.start();

        System.setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgresContainer.getUsername());
        System.setProperty("hibernate.connection.password", postgresContainer.getPassword());

        userDao = new UserDaoImpl();
    }

    @AfterAll
    void cleanup() {
        HibernateUtil.shutdown();
        postgresContainer.stop();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User("1User", "1User@example.com", 30);
        userDao.save(user);

        User fromDb = userDao.findById(user.getId());
        assertNotNull(fromDb, "User должен существовать в БД");
        assertEquals("1User@example.com", fromDb.getEmail(), "Email должен совпадать");
        assertEquals("1User", fromDb.getName(), "Имя должно совпадать");
        assertEquals(30, fromDb.getAge(), "Возраст должен совпадать");
    }

    @Test
    void testFindAll() {
        User user1 = new User("Auser", "auser@example.com", 22);
        User user2 = new User("Buser", "buser@example.com", 25);

        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();
        assertNotNull(users, "Список пользователей не должен быть null");
        assertTrue(users.size() >= 2, "Список пользователей должен содержать как минимум 2 записи");
    }

    @Test
    void testDeleteById() {
        User user = new User("Cuser", "cuser@example.com", 28);
        userDao.save(user);

        int deleted = userDao.deleteById(user.getId());
        assertEquals(1, deleted, "Удаление должно вернуть 1");

        User fromDb = userDao.findById(user.getId());
        assertNull(fromDb, "Пользователь должен быть удалён из БД");
    }
}