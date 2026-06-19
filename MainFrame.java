package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Main Frame that houses the primary tabs (Dashboard, Manage Subscriptions, Audit Logs).
 * Serves as the central controller coordinating UI refresh actions.
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private SubscriptionPanel subscriptionPanel;
    private AuditLogPanel auditLogPanel;

    // Styling constants
    private static final Color MAIN_BG = new Color(240, 242, 245); // Light Gray
    private static final Color ACCENT_BLUE = new Color(37, 99, 235); // #2563EB Blue
    private static final Color HEADER_TEXT = Color.WHITE;
    private static final Color SUBTITLE_TEXT = new Color(229, 231, 235); // Very light gray

    public MainFrame() {
        setTitle("SubscriptionSentry – Smart Subscription Tracker");
        setSize(1080, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        getContentPane().setBackground(MAIN_BG);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // --- Header Panel ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(ACCENT_BLUE);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel lblTitle = new JLabel("SubscriptionSentry", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(HEADER_TEXT);

        JLabel lblSubtitle = new JLabel("Smart Subscription & Free Trial Tracker", SwingConstants.LEFT);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(SUBTITLE_TEXT);

        JPanel pnlTitles = new JPanel(new BorderLayout());
        pnlTitles.setOpaque(false);
        pnlTitles.add(lblTitle, BorderLayout.NORTH);
        pnlTitles.add(lblSubtitle, BorderLayout.SOUTH);

        pnlHeader.add(pnlTitles, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- Tabs Initialization ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(new Color(55, 65, 81)); // Dark Gray

        dashboardPanel = new DashboardPanel();
        subscriptionPanel = new SubscriptionPanel(this);
        auditLogPanel = new AuditLogPanel();

        tabbedPane.addTab("  Dashboard  ", dashboardPanel);
        tabbedPane.addTab("  Manage Subscriptions  ", subscriptionPanel);
        tabbedPane.addTab("  Audit Logs  ", auditLogPanel);

        // Tab selection change listener: auto-refreshes data when tabs are switched
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    dashboardPanel.refreshData();
                    break;
                case 1:
                    subscriptionPanel.refreshData();
                    break;
                case 2:
                    auditLogPanel.refreshData();
                    break;
            }
        });

        // Add padding around tabbedPane
        JPanel pnlTabWrapper = new JPanel(new BorderLayout());
        pnlTabWrapper.setBackground(MAIN_BG);
        pnlTabWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlTabWrapper.add(tabbedPane, BorderLayout.CENTER);

        add(pnlTabWrapper, BorderLayout.CENTER);
    }

    /**
     * Propagates a refresh call to all active tab panels to ensure numbers,
     * lists, and logs stay synchronised after addition, cancellation or deletion.
     */
    public void refreshAllTabs() {
        dashboardPanel.refreshData();
        subscriptionPanel.refreshData();
        auditLogPanel.refreshData();
    }
}
