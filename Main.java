import backend.DBConnection;
import ui.MainFrame;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for SubscriptionSentry.
 * Initialises Look & Feel, checks database connectivity, and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        // Use cross platform look and feel for consistent solid background colors on buttons
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set Cross Platform Look and Feel. Defaulting to standard.");
        }

        // Test database connectivity before launching UI
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to MySQL database 'subscriptionsentry_db'.\n" +
                            "Error: " + e.getMessage() + "\n\n" +
                            "Please verify that:\n" +
                            "1. MySQL Server is running.\n" +
                            "2. Database 'subscriptionsentry_db' exists.\n" +
                            "3. Password is correct in backend/DBConnection.java.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Launch the application window
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
