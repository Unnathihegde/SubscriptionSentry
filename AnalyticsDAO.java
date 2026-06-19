package backend;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for dashboard stats and analytics.
 * Demonstrates View querying, Aggregate SUM() queries, and Stored Procedures with OUT parameters.
 */
public class AnalyticsDAO {

    /**
     * Retrieves the active subscriptions count from the database View.
     * @return count of active subscriptions
     */
    public int getActiveCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM ActiveSubscriptionsView";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching active count from view: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Calculates monthly spending using aggregate SUM function.
     * Properly converts annual amounts to monthly equivalents (cost / 12).
     * @return total monthly spending amount
     */
    public double getMonthlySpending() {
        double spending = 0.0;
        String sql = "SELECT SUM(CASE WHEN billing_cycle = 'Monthly' THEN cost ELSE cost / 12 END) AS monthly_sum " +
                "FROM Subscriptions WHERE status = 'Active'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                spending = rs.getDouble("monthly_sum");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating monthly spending: " + e.getMessage());
            e.printStackTrace();
        }
        return spending;
    }

    /**
     * Retrieves active subscriptions that renew within a specific number of days.
     * @param days number of days (typically 7)
     * @return List of Subscription objects
     */
    public List<Subscription> getUpcomingRenewals(int days) {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT s.*, c.category_name FROM Subscriptions s " +
                "JOIN Categories c ON s.category_id = c.category_id " +
                "WHERE s.status = 'Active' " +
                "AND s.next_renewal_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                "ORDER BY s.next_renewal_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Subscription sub = new Subscription(
                            rs.getInt("subscription_id"),
                            rs.getString("service_name"),
                            rs.getDouble("cost"),
                            rs.getString("billing_cycle"),
                            rs.getDate("next_renewal_date"),
                            rs.getInt("category_id"),
                            rs.getBoolean("is_free_trial"),
                            rs.getString("status")
                    );
                    sub.setCategoryName(rs.getString("category_name"));
                    subscriptions.add(sub);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming renewals: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    /**
     * Calls the stored procedure CheckExpiredTrials using CallableStatement with OUT parameter.
     * @return count of expired trials calculated by the procedure cursor
     */
    public int getExpiredTrialsCount() {
        int count = 0;
        String sql = "{call CheckExpiredTrials(?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            // Register parameter index 1 as OUT of type INTEGER
            cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
            cstmt.execute();
            count = cstmt.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error calling CheckExpiredTrials stored procedure: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Retrieves the minimum cost among all subscriptions.
     * @return minimum subscription cost
     */
    public double getMinCost() {
        double min = 0.0;
        String sql = "SELECT MIN(cost) FROM Subscriptions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                min = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching min cost: " + e.getMessage());
            e.printStackTrace();
        }
        return min;
    }

    /**
     * Retrieves the maximum cost among all subscriptions.
     * @return maximum subscription cost
     */
    public double getMaxCost() {
        double max = 0.0;
        String sql = "SELECT MAX(cost) FROM Subscriptions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                max = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching max cost: " + e.getMessage());
            e.printStackTrace();
        }
        return max;
    }

    /**
     * Retrieves the average cost of all subscriptions.
     * @return average subscription cost
     */
    public double getAvgCost() {
        double avg = 0.0;
        String sql = "SELECT AVG(cost) FROM Subscriptions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                avg = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching avg cost: " + e.getMessage());
            e.printStackTrace();
        }
        return avg;
    }

    /**
     * Retrieves the total count of all subscriptions in the database.
     * @return total subscriptions count
     */
    public int getTotalCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Subscriptions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Retrieves the sum cost of all subscriptions.
     * @return sum of subscription costs
     */
    public double getTotalCost() {
        double sum = 0.0;
        String sql = "SELECT SUM(cost) FROM Subscriptions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                sum = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sum cost: " + e.getMessage());
            e.printStackTrace();
        }
        return sum;
    }

    /**
     * Retrieves the count of cancelled subscriptions.
     * @return count of cancelled subscriptions
     */
    public int getCancelledCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Subscriptions WHERE status = 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cancelled count: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }
}
