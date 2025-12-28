package org.hiber.app;

import org.hiber.dao.UserDao;
import org.hiber.dao.UserDaoImpl;
import org.hiber.entity.User;
import org.hiber.exceptions.EmailAlreadyExistsException;
import org.hiber.utils.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class App {

    private final Logger logger = LoggerFactory.getLogger(App.class);

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDaoImpl();

    public static void main(String[] args) {
        App menu = new App();

        menu.start();
    }

    private void start() {
        boolean exit = false;
        while (!exit) {
            logger.info("Show main menu");
            printMenu();
            int choice = readIntInput("Введите 0 - 4: ");
            logger.info("User input {}", choice);

            switch (choice) {
                case 0 -> exit = true;
                case 1 -> createUser();
                case 2 -> updateUser();
                case 3 -> deleteUser();
                case 4 -> listAllUsers();
                default -> printInputError();
            }
        }

        HibernateUtil.shutdown();
        System.out.println("Работа завершена.");
        logger.info("The application is ending.");
    }

    private void createUser() {
        logger.info("createUser start");
        System.out.println("Введите данные.");
        String name = readStringInput("Имя: ");
        String email = readStringInput("Email: ");
        int age = readIntInput("Возраст: ");

        User user = new User(name, email, age);
        logger.info("new user created\ntrying to save user to DB");

        try {
            userDao.save(user);
            System.out.printf("Пользователь добавлен в БД с ID: %s", user.getId());
            logger.info("Successful saving user to DB");
        } catch (EmailAlreadyExistsException e) {
            System.out.printf(e.getMessage());
            System.out.println("\nОшибка записи в БД.");
            logger.info("Saving user to DB Fails");
        }
    }

    private void updateUser() {
        logger.info("updateUser start");
        System.out.println("Обновление данных пользователя.");
        int id = readIntInput("Введите ID пользователя для обновления: ");
        logger.info("Entered id: {}", id);
        User user = userDao.findById(id);
        if (user == null) {
            logger.info("User was not found");
            System.out.println("Пользователь не найден.");
            return;
        }

        String name = readStringInput("Новое имя (" + user.getName() + "): ");
        String email = readStringInput("Новый email (" + user.getEmail() + "): ");
        int age = readIntInput("Новый возраст (" + user.getAge() + "): ");

        if (!name.isEmpty()) {
            user.setName(name);
        }
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        user.setAge(age);
        logger.info("Updating user");
        userDao.update(user);
        System.out.println("Данные пользователя перезаписаны.");
        logger.info("User info updated");
    }

    private void deleteUser() {
        logger.info("deleteUser start");
        int id = readIntInput("Введите ID для удаления пользователя: ");
        logger.info("Entered id: {}", id);
        User user = userDao.findById(id);
        if (user == null) {
            logger.info("User was not found");
            System.out.println("Пользователь не найден.");
            return;
        }
        userDao.delete(user);
        System.out.println("Пользователь удалён из БД.");
        logger.info("User id {} deleted", id);
    }

    private void listAllUsers() {
        logger.info("listAllUsers start");
        System.out.println("Список пользователей из БД:");
        var users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Не найдено записей в БД.");
            logger.info("DB empty");
            return;
        }
        users.forEach(u -> System.out.printf("%d - %s - %s - возраст: %d\n",
                u.getId(), u.getName(), u.getEmail(), u.getAge()));
    }

    private int readIntInput(String message) {
        logger.info("readIntInput start");
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                printInputError();
            }
        }
    }

    private String readStringInput(String message) {
        logger.info("readStringInput start");
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private void printMenu() {
        System.out.println("\nВыбрать дальнейшие действия: ");
        System.out.println("1. Создать пользователя.");
        System.out.println("2. Обновить данные пользователя.");
        System.out.println("3. Удалить пользователя.");
        System.out.println("4. Список всех пользователей из БД.");
        System.out.println("0. Выход.");
    }

    private void printInputError() {
        logger.info("Input error");
        System.out.println("Ввод нераспознан, повторите.");
    }

}
