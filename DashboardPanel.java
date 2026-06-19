package ui;

import backend.AnalyticsDAO;
import backend.Subscription;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * DashboardPanel displays analytical cards and lists upcoming renewals.
 */
public class DashboardPanel extends JPanel {
    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    private JLabel lblActiveCount;
    private JLabel lblCancelledCount;
    private JLabel lblTotalCount;
    private JLabel lblMonthlySpend;
    private JLabel lblMinCost;
    private JLabel lblMaxCost;
    private JLabel lblAvgCost;
    private JLabel lblRenewalsCount;
    private JLabel lblExpiredTrials;

    private JTable tblUpcoming;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;

    // Styling constants
    private static final Color BG_COLOR = new Color(240, 242, 245); // Light Gray background
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(31, 41, 55); // Slate 800
    private static final Color TEXT_MUTED = new Color(107, 114, 128); // Slate 500
    private static final Color BUTTON_BLUE = new Color(37, 99, 235); // #2563EB Blue
    private static final Color CARD_BORDER_COLOR = new Color(229, 231, 235); // Light grey border

    public DashboardPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- 3x3 Stats Grid Panel ---
        JPanel pnlCards = new JPanel(new GridLayout(3, 3, 15, 15));
        pnlCards.setBackground(BG_COLOR);

        lblActiveCount = new JLabel("0", SwingConstants.CENTER);
        lblCancelledCount = new JLabel("0", SwingConstants.CENTER);
        lblTotalCount = new JLabel("0", SwingConstants.CENTER);
        lblMonthlySpend = new JLabel("₹0", SwingConstants.CENTER);
        lblMinCost = new JLabel("₹0", SwingConstants.CENTER);
        lblMaxCost = new JLabel("₹0", SwingConstants.CENTER);
        lblAvgCost = new JLabel("₹0", SwingConstants.CENTER);
        lblRenewalsCount = new JLabel("0", SwingConstants.CENTER);
        lblExpiredTrials = new JLabel("0", SwingConstants.CENTER);

        pnlCards.add(createCard("Total Active Subscriptions", lblActiveCount));
        pnlCards.add(createCard("Total Cancelled Subscriptions", lblCancelledCount));
        pnlCards.add(createCard("Total Subscriptions", lblTotalCount));
        pnlCards.add(createCard("Monthly Spending", lblMonthlySpend));
        pnlCards.add(createCard("Minimum Subscription Cost", lblMinCost));
        pnlCards.add(createCard("Maximum Subscription Cost", lblMaxCost));
        pnlCards.add(createCard("Average Subscription Cost", lblAvgCost));
        pnlCards.add(createCard("Upcoming Renewals (7 Days)", lblRenewalsCount));
        pnlCards.add(createCard("Expired Free Trials", lblExpiredTrials));

        add(pnlCards, BorderLayout.NORTH);

        // --- Upcoming Renewals Panel ---
        JPanel pnlTable = new JPanel(new BorderLayout(10, 10));
        pnlTable.setBackground(CARD_BG);
        pnlTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel("Upcoming Renewals (Next 7 Days)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_DARK);

        String[] columns = {"Service Name", "Renewal Date", "Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) {
                    return Double.class;
                }
                return String.class;
            }
        };

        // Custom JTable with alternate row styling
        tblUpcoming = new JTable(tableModel) {
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

        tblUpcoming.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblUpcoming.setRowHeight(28);
        tblUpcoming.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblUpcoming.getTableHeader().setBackground(new Color(243, 244, 246));
        tblUpcoming.getTableHeader().setForeground(TEXT_DARK);
        tblUpcoming.setFillsViewportHeight(true);
        tblUpcoming.setAutoCreateRowSorter(true);

        // Currency Cell Renderer for Cost column (index 2)
        tblUpcoming.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(formatCurrency((Double) value));
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblUpcoming);
        scrollPane.setPreferredSize(new Dimension(600, 220));

        pnlTable.add(lblTitle, BorderLayout.NORTH);
        pnlTable.add(scrollPane, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.CENTER);

        // --- Controls Panel (Footer) ---
        JPanel pnlControls = new JPanel(new BorderLayout());
        pnlControls.setBackground(BG_COLOR);

        btnRefresh = new JButton("Refresh Statistics");
        styleButton(btnRefresh, BUTTON_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshData());

        pnlControls.add(btnRefresh, BorderLayout.EAST);
        add(pnlControls, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel lblValue) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(TEXT_MUTED);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(BUTTON_BLUE);
        lblValue.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
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
     * Formats double values into Indian Rupee formatting (e.g. ₹649 or ₹1,300).
     */
    public static String formatCurrency(double amount) {
        if (amount == (long) amount) {
            return String.format("₹%,d", (long) amount);
        } else {
            return String.format("₹%,.2f", amount);
        }
    }

    /**
     * Refreshes the dashboard stats and table content.
     */
    public void refreshData() {
        int activeCount = analyticsDAO.getActiveCount();
        int cancelledCount = analyticsDAO.getCancelledCount();
        int totalCount = analyticsDAO.getTotalCount();
        double monthlySpend = analyticsDAO.getMonthlySpending();
        double minCost = analyticsDAO.getMinCost();
        double maxCost = analyticsDAO.getMaxCost();
        double avgCost = analyticsDAO.getAvgCost();
        List<Subscription> upcoming = analyticsDAO.getUpcomingRenewals(7);
        int expiredTrials = analyticsDAO.getExpiredTrialsCount();

        lblActiveCount.setText(String.valueOf(activeCount));
        lblCancelledCount.setText(String.valueOf(cancelledCount));
        lblTotalCount.setText(String.valueOf(totalCount));

        lblMonthlySpend.setText(formatCurrency(monthlySpend));
        lblMinCost.setText(formatCurrency(minCost));
        lblMaxCost.setText(formatCurrency(maxCost));
        lblAvgCost.setText(formatCurrency(avgCost));

        lblRenewalsCount.setText(String.valueOf(upcoming.size()));
        lblExpiredTrials.setText(String.valueOf(expiredTrials));

        tableModel.setRowCount(0);
        for (Subscription sub : upcoming) {
            tableModel.addRow(new Object[]{
                    sub.getServiceName(),
                    sub.getNextRenewalDate().toString(),
                    sub.getCost()
            });
        }
    }
}
