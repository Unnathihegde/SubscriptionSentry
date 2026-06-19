package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for retrieving records from SubscriptionAudit table.
 */
public class AuditDAO {

    /**
     * Entity class representing an individual audit record from SubscriptionAudit.
     */
    public static class AuditRecord {
        private final int auditId;
        private final int subscriptionId;
        private final String actionPerformed;
        private final Timestamp logTimestamp;

        public AuditRecord(int auditId, int subscriptionId, String actionPerformed, Timestamp logTimestamp) {
            this.auditId = auditId;
            this.subscriptionId = subscriptionId;
            this.actionPerformed = actionPerformed;
            this.logTimestamp = logTimestamp;
        }

        public int getAuditId() {
            return auditId;
        }

        public int getSubscriptionId() {
            return subscriptionId;
        }

        public String getActionPerformed() {
            return actionPerformed;
        }

        public Timestamp getLogTimestamp() {
            return logTimestamp;
        }
    }

    /**
     * Fetches all audit records ordered by timestamp descending.
     * @return List of AuditRecord
     */
    public List<AuditRecord> getAuditLogs() {
        List<AuditRecord> logs = new ArrayList<>();
        String sql = "SELECT audit_id, subscription_id, action_performed, log_timestamp " +
                "FROM SubscriptionAudit ORDER BY log_timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                logs.add(new AuditRecord(
                        rs.getInt("audit_id"),
                        rs.getInt("subscription_id"),
                        rs.getString("action_performed"),
                        rs.getTimestamp("log_timestamp")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching audit logs: " + e.getMessage());
            e.printStackTrace();
        }
        return logs;
    }
}
