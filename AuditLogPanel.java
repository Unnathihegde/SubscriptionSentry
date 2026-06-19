package ui;

import backend.AuditDAO;
import backend.AuditDAO.AuditRecord;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * AuditLogPanel displays database trigger logs loaded from the SubscriptionAudit table.
 */
public class AuditLogPanel extends JPanel {
    private final AuditDAO auditDAO = new AuditDAO();

    private JTable tblAudit;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    // Styling constants
    private static final Color BG_COLOR = new Color(240, 242, 245); // Light Gray
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(31, 41, 55); // Slate 800
    private static final Color BUTTON_BLUE = new Color(37, 99, 235); // #2563EB Blue
    private static final Color CARD_BORDER_COLOR = new Color(229, 231, 235); // Light grey border

    public AuditLogPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        JPanel pnlTableContainer = new JPanel(new BorderLayout(10, 10));
        pnlTableContainer.setBackground(PANEL_BG);
        pnlTableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Header Panel
        JLabel lblTitle = new JLabel("Database Audit Logs");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);
        pnlTableContainer.add(lblTitle, BorderLayout.NORTH);

        // Columns matching SubscriptionAudit table, displaying action classification and details
        String[] columns = {"Audit ID", "Subscription ID", "Action", "Description", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 1) return Integer.class;
                return String.class;
            }
        };

        // Custom JTable with alternate row styling
        tblAudit = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                } else {
                    c.setBackground(new Color(219, 234, 254)); // #DBEAFE
                    c.setForeground(new Color(30, 64, 175)); // #1E40AF
                }
                return c;
            }
        };

        tblAudit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblAudit.setRowHeight(28);
        tblAudit.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblAudit.getTableHeader().setBackground(new Color(243, 244, 246));
        tblAudit.getTableHeader().setForeground(TEXT_DARK);
        tblAudit.setFillsViewportHeight(true);
        tblAudit.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tblAudit);
        scrollPane.setPreferredSize(new Dimension(600, 380));
        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);

        // Footer buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setBackground(PANEL_BG);

        btnRefresh = new JButton("Refresh Logs");
        styleButton(btnRefresh, BUTTON_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshData());

        pnlButtons.add(btnRefresh);
        pnlTableContainer.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlTableContainer, BorderLayout.CENTER);
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBackground(bg);
        button.setForeground(fg);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); // Padding margins
    }

    /**
     * Maps the database action string from triggers into clean, readable action classifications.
     */
    private String mapActionToType(String rawAction) {
        if (rawAction == null) return "Unknown Action";
        String lower = rawAction.toLowerCase();
        if (lower.contains("added") || lower.contains("new subscription")) {
            return "Added Subscription";
        } else if (lower.contains("cancelled") || lower.contains("to cancelled")) {
            return "Cancelled Subscription";
        } else if (lower.contains("deleted") || lower.contains("subscription deleted")) {
            return "Deleted Subscription";
        }
        return "Updated Subscription"; // Fallback for other status updates
    }

    /**
     * Re-queries trigger logs from the database and updates the JTable contents.
     */
    public void refreshData() {
        List<AuditRecord> logs = auditDAO.getAuditLogs();
        tableModel.setRowCount(0);

        for (AuditRecord rec : logs) {
            tableModel.addRow(new Object[]{
                    rec.getAuditId(),
                    rec.getSubscriptionId(),
                    mapActionToType(rec.getActionPerformed()),
                    rec.getActionPerformed(),
                    rec.getLogTimestamp().toString()
            });
        }
    }
}
