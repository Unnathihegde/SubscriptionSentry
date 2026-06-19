package backend;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing Subscriptions and Categories.
 * All database operations use PreparedStatements.
 */
public class SubscriptionDAO {

    /**
     * Adds a new subscription record.
     * @param sub Subscription object
     * @return boolean indicating success
     */
    public boolean addSubscription(Subscription sub) {
        String sql = "INSERT INTO Subscriptions (service_name, cost, billing_cycle, next_renewal_date, category_id, is_free_trial, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sub.getServiceName());
            pstmt.setDouble(2, sub.getCost());
            pstmt.setString(3, sub.getBillingCycle());
            pstmt.setDate(4, sub.getNextRenewalDate());
            pstmt.setInt(5, sub.getCategoryId());
            pstmt.setBoolean(6, sub.isFreeTrial());
            pstmt.setString(7, sub.getStatus());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all subscriptions with category names joined.
     * @return List of Subscriptions
     */
    public List<Subscription> getAllSubscriptions() {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT s.*, c.category_name FROM Subscriptions s " +
                "JOIN Categories c ON s.category_id = c.category_id " +
                "ORDER BY s.subscription_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                subscriptions.add(mapResultSetToSubscription(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    /**
     * Searches and filters subscriptions by service name query, category name, and status.
     * @param query service name substring to search
     * @param categoryName category name to filter (or "All")
     * @param status status to filter (or "All")
     * @return List of matching subscriptions
     */
    public List<Subscription> searchSubscriptions(String query, String categoryName, String status) {
        List<Subscription> subscriptions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.*, c.category_name FROM Subscriptions s " +
                        "JOIN Categories c ON s.category_id = c.category_id WHERE 1=1"
        );

        boolean hasQuery = query != null && !query.trim().isEmpty();
        boolean hasCategory = categoryName != null && !categoryName.equals("All");
        boolean hasStatus = status != null && !status.equals("All");

        if (hasQuery) {
            sql.append(" AND s.service_name LIKE ?");
        }
        if (hasCategory) {
            sql.append(" AND c.category_name = ?");
        }
        if (hasStatus) {
            sql.append(" AND s.status = ?");
        }
        sql.append(" ORDER BY s.subscription_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (hasQuery) {
                pstmt.setString(paramIndex++, "%" + query.trim() + "%");
            }
            if (hasCategory) {
                pstmt.setString(paramIndex++, categoryName);
            }
            if (hasStatus) {
                pstmt.setString(paramIndex++, status);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(mapResultSetToSubscription(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching subscriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return subscriptions;
    }

    /**
     * Cancels a subscription by updating its status to 'Cancelled'.
     * This triggers the AfterSubscriptionUpdate DB trigger.
     * @param subscriptionId ID to update
     * @return boolean indicating success
     */
    public boolean cancelSubscription(int subscriptionId) {
        String sql = "UPDATE Subscriptions SET status = 'Cancelled' WHERE subscription_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriptionId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error cancelling subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing subscription record in the database.
     * @param sub Subscription object with valid ID
     * @return boolean indicating success
     */
    public boolean updateSubscription(Subscription sub) {
        String sql = "UPDATE Subscriptions SET service_name = ?, cost = ?, billing_cycle = ?, " +
                "next_renewal_date = ?, category_id = ?, is_free_trial = ?, status = ? " +
                "WHERE subscription_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sub.getServiceName());
            pstmt.setDouble(2, sub.getCost());
            pstmt.setString(3, sub.getBillingCycle());
            pstmt.setDate(4, sub.getNextRenewalDate());
            pstmt.setInt(5, sub.getCategoryId());
            pstmt.setBoolean(6, sub.isFreeTrial());
            pstmt.setString(7, sub.getStatus());
            pstmt.setInt(8, sub.getSubscriptionId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permanently deletes a subscription record from the database.
     * @param subscriptionId ID of the subscription to delete
     * @return boolean indicating success
     */
    public boolean deleteSubscription(int subscriptionId) {
        String sql = "DELETE FROM Subscriptions WHERE subscription_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subscriptionId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all categories sorted alphabetically.
     * @return List of Category objects
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM Categories ORDER BY category_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
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
        return sub;
    }
}
