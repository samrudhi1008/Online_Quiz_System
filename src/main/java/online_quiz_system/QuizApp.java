package online_quiz_system;

import java.sql.*;
import java.util.Scanner;

public class QuizApp {
    private static final String URL = "jdbc:postgresql://localhost:5432/quiz_system";
    private static final String USER = "postgres"; // Change this
    private static final String PASSWORD = "123"; // Change this

    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);
    private static String loggedInUser = null;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Database!");

            while (true) {
                System.out.println("\n===== Online Quiz System =====");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Add Question");
                System.out.println("4. Update Question");
                System.out.println("5. Delete Question");
                System.out.println("6. Start Quiz");
                System.out.println("7. Exit");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1: registerUser(); break;
                    case 2: loginUser(); break;
                    case 3: if (isLoggedIn()) addQuestion(); break;
                    case 4: if (isLoggedIn()) updateQuestion(); break;
                    case 5: if (isLoggedIn()) deleteQuestion(); break;
                    case 6: if (isLoggedIn()) startQuiz(); break;
                    case 7: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid choice! Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            System.out.println("Username already exists.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loggedInUser = username;
                System.out.println("Login successful! Welcome, " + username);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addQuestion() {
        System.out.print("Enter question: ");
        String question = scanner.nextLine();
        System.out.print("Option 1: ");
        String optionA = scanner.nextLine();
        System.out.print("Option 2: ");
        String optionB = scanner.nextLine();
        System.out.print("Option 3: ");
        String optionC = scanner.nextLine();
        System.out.print("Option 4: ");
        String optionD = scanner.nextLine();
        
        int correctOption;
        while (true) {
            System.out.print("Enter correct option (1-4): ");
            String input = scanner.nextLine();
            try {
                correctOption = Integer.parseInt(input);
                if (correctOption >= 1 && correctOption <= 4) break; // Valid input
                else System.out.println("Please enter a number between 1 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
            }
        }

        try {
            String query = "INSERT INTO questions (question_text, option_a, option_b, option_c, option_d, correct_option) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, question);
            stmt.setString(2, optionA);
            stmt.setString(3, optionB);
            stmt.setString(4, optionC);
            stmt.setString(5, optionD);
            stmt.setInt(6, correctOption);
            stmt.executeUpdate();
            System.out.println("Question added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void updateQuestion() {
        System.out.print("Enter question ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new question: ");
        String question = scanner.nextLine();

        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE questions SET question = ? WHERE id = ?");
            stmt.setString(1, question);
            stmt.setInt(2, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Question updated successfully!");
            } else {
                System.out.println("Question ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteQuestion() {
        System.out.print("Enter question ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE id = ?");
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Question deleted successfully!");
            } else {
                System.out.println("Question ID not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void startQuiz() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions");
            ResultSet rs = stmt.executeQuery();
            int score = 0;

            while (rs.next()) {
                System.out.println("\n" + rs.getString("question"));
                System.out.println("1. " + rs.getString("option1"));
                System.out.println("2. " + rs.getString("option2"));
                System.out.println("3. " + rs.getString("option3"));
                System.out.println("4. " + rs.getString("option4"));
                System.out.print("Your answer: ");
                int answer = scanner.nextInt();
                scanner.nextLine();

                if (answer == rs.getInt("correct_option")) {
                    score++;
                }
            }
            System.out.println("Quiz Over! Your Score: " + score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isLoggedIn() {
        if (loggedInUser == null) {
            System.out.println("Please login first.");
            return false;
        }
        return true;
    }
}

