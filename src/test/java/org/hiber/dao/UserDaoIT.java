package org.hiber.dao;

import org.hiber.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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
                .configure("test-hibernate.cfg.xml")
                .addAnnotatedClass(User.class);

        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);

        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            System.out.printf("Очистка таблицы User, удалено '%d' элементов\n",
                    session.createMutationQuery("DELETE FROM User").executeUpdate());
//            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterEach
    void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
        // success
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

    @Test
    void testFindById_Success() {
        User user = new User("TestUser", "testEmail@example.com", 30);
        userDao.save(user);

        User fromDb = userDao.findById(user.getId());
        assertNotNull(fromDb);
        assertEquals("TestUser", fromDb.getName());
        assertEquals("testEmail@example.com", fromDb.getEmail());
        assertEquals(30, fromDb.getAge());
    }

    // FAIL invalid id
    @Test
    void testFindById_NotFound() {
        User fromDb = userDao.findById(-1);
        assertNull(fromDb);
    }

    @Test
    void testFindByEmail_Success() {
        User user = new User("TestUser", "testEmail@example.com", 25);
        userDao.save(user);
        User fromDb = userDao.findByEmail("testEmail@example.com");
        assertNotNull(fromDb);
        assertEquals("TestUser", fromDb.getName());
        assertEquals(25, fromDb.getAge());
    }

    // not found returns null
    @Test
    void testFindByEmail_NotFound() {
        User fromDb = userDao.findByEmail("nonExistent@example.com");
        assertNull(fromDb);
    }

    @Test
    void testFindAll_EmptyTable() {
        var users = userDao.findAll();
        assertNotNull(users);
        assertTrue(users.isEmpty(), "Expected: empty list ");
    }

    @Test
    void testFindAll_OneUser() {
        User user = new User("SingleUser", "single@test.com", 22);
        userDao.save(user);

        var users = userDao.findAll();
        assertEquals(1, users.size());
        assertEquals("SingleUser", users.get(0).getName());
    }

    @Test
    void testFindAll_MultipleUsers() {
        User user1 = new User("User1", "u1@test.com", 25);
        User user2 = new User("User2", "u2@test.com", 30);
        User user3 = new User("User3", "u3@test.com", 33);
        userDao.save(user1);
        userDao.save(user2);
        userDao.save(user3);

        var users = userDao.findAll();
        assertEquals(3, users.size());

        List<String> emails = users.stream().map(User::getEmail).toList();
        assertTrue(emails.contains("u1@test.com"));
        assertTrue(emails.contains("u2@test.com"));
        assertTrue(emails.contains("u3@test.com"));
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User("Original", "original@test.com", 20);
        userDao.save(user);

        user.setName("Updated");
        user.setAge(25);
        userDao.update(user);

        User fromDb = userDao.findByEmail("original@test.com");
        assertNotNull(fromDb);
        assertEquals("Updated", fromDb.getName());
        assertEquals(25, fromDb.getAge());
    }

    @Test
    void testUpdateUser_NullUser() {
        assertThrows(IllegalArgumentException.class, () -> userDao.update(null));
    }

    @Test
    void testDeleteById_ExistingUser() {
        User user = new User("ToDelete", "todelete@test.com", 30);
        userDao.save(user);

        int result = userDao.deleteById(user.getId());
        assertEquals(1, result);
        User fromDb = userDao.findById(user.getId()); //check db
        assertNull(fromDb);
    }

    @Test
    void testDeleteById_NonExistingUser() {
        int result = userDao.deleteById(9999); // non-existent ID
        assertEquals(0, result);
    }
}