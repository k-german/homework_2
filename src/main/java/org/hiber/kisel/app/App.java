package org.hiber.kisel.app;

import org.hiber.kisel.dao.UserDao;
import org.hiber.kisel.dao.UserDaoImpl;
import org.hiber.kisel.entity.User;
import org.hiber.kisel.exceptions.EmailAlreadyExistsException;
import org.hiber.kisel.utils.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class App {

    private Logger logger = LoggerFactory.getLogger(App.class);

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
    }

    private static void createUser() {
        logger.info("createUser started");
        System.out.println("Введите данные.");
        String name = readStringInput("Имя: ");
        String email = readStringInput("Email: ");
        int age = readIntInput("Возраст: ");

        User user = new User(name, email, age);

        try {
            userDao.save(user);
            System.out.printf("Пользователь добавлен в БД с ID: %s", user.getId());
        } catch (EmailAlreadyExistsException e) {
            System.out.printf(e.getMessage());
            System.out.println("\nОшибка записи в БД.");
        }
    }

    private static void updateUser() {
        System.out.println("Обновление данных пользователя.");
        int id = readIntInput("Введите ID пользователя для обновления: ");
        User user = userDao.findById(id);
        if (user == null) {
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

        userDao.update(user);
        System.out.println("Данные пользователя перезаписаны.");
    }

    private static void deleteUser() {
        int id = readIntInput("Введите ID для удаления пользователя: ");
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }
        userDao.delete(user);
        System.out.println("Пользователь удалён из БД.");
    }

    private static void listAllUsers() {
        System.out.println("Список пользователей из БД:");
        var users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Не найдено записей в БД.");
            return;
        }
        users.forEach(u -> System.out.printf("%d - %s - %s - возраст: %d\n",
                u.getId(), u.getName(), u.getEmail(), u.getAge()));
    }

    private static int readIntInput(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                printInputError();
            }
        }
    }

    private static String readStringInput(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static void printMenu() {
        System.out.println("\nВыбрать дальнейшие действия: ");
        System.out.println("1. Создать пользователя.");
        System.out.println("2. Обновить данные пользователя.");
        System.out.println("3. Удалить пользователя.");
        System.out.println("4. Список всех пользователей из БД.");
        System.out.println("0. Выход.");
    }

    private static void printInputError() {
        System.out.println("Ввод нераспознан, повторите.");
    }

}
