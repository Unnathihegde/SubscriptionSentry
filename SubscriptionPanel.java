package ui;

import backend.Category;
import backend.Subscription;
import backend.SubscriptionDAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * SubscriptionPanel provides forms to add/edit subscriptions, view subscriptions
 * in a JTable, search/filter them, and cancel/delete selected subscriptions.
 */
public class SubscriptionPanel extends JPanel {
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final MainFrame parentFrame;

    private List<Subscription> currentSubscriptions;
    private int editingSubscriptionId = -1; // -1 represents Add mode, otherwise Edit mode

    // Components for Add/Edit Form
    private JLabel lblFormTitle;
    private JTextField txtService;
    private JTextField txtCost;
    private JComboBox<String> cbCycle;
    private JTextField txtRenewal;
    private JComboBox<Category> cbCategory;
    private JComboBox<String> cbStatus;
    private JCheckBox chkTrial;
    private JButton btnSave;  // Dual purpose: "Add Subscription" / "Update Subscription"
    private JButton btnClear; // Dual purpose: "Clear Form" / "Cancel Edit"

    // Components for Subscriptions Table
    private JTable tblSubs;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbFilterCategory;
    private JComboBox<String> cbFilterStatus;
    private JButton btnCancel;
    private JButton btnDelete;
    private JButton btnRefresh;

    // Styling constants
    private static final Color BG_COLOR = new Color(240, 242, 245); // Light Gray
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(31, 41, 55); // Slate 800
    private static final Color CARD_BORDER_COLOR = new Color(229, 231, 235); // Light grey border

    // Button Colors
    private static final Color COLOR_BLUE = new Color(37, 99, 235); // #2563EB Blue
    private static final Color COLOR_GREEN = new Color(22, 163, 74); // #16A34A Green
    private static final Color COLOR_RED = new Color(220, 38, 38); // #DC2626 Red
    private static final Color COLOR_GRAY_BTN = new Color(75, 85, 99); // Slate 600 Dark Grey for Clear/Cancel

    public SubscriptionPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        initComponents();
        loadCategories();
        refreshData();
    }

    private void initComponents() {
        // --- LEFT SIDE: Add/Edit Subscription Form ---
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBackground(PANEL_BG);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlForm.setPreferredSize(new Dimension(340, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Form Title
        lblFormTitle = new JLabel("Add Subscription");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(TEXT_DARK);
        gbc.gridwidth = 2;
        pnlForm.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Service Name
        pnlForm.add(createStyledLabel("Service Name:"), gbc);
        gbc.gridx = 1;
        txtService = new JTextField();
        txtService.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(txtService, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Cost
        pnlForm.add(createStyledLabel("Cost (₹):"), gbc);
        gbc.gridx = 1;
        txtCost = new JTextField();
        txtCost.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(txtCost, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Billing Cycle
        pnlForm.add(createStyledLabel("Billing Cycle:"), gbc);
        gbc.gridx = 1;
        cbCycle = new JComboBox<>(new String[]{"Monthly", "Annually"});
        cbCycle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(cbCycle, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Next Renewal Date
        pnlForm.add(createStyledLabel("Renewal Date:"), gbc);
        gbc.gridx = 1;
        txtRenewal = new JTextField("YYYY-MM-DD");
        txtRenewal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRenewal.setForeground(Color.GRAY);
        txtRenewal.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtRenewal.getText().equals("YYYY-MM-DD")) {
                    txtRenewal.setText("");
                    txtRenewal.setForeground(TEXT_DARK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtRenewal.getText().isEmpty()) {
                    txtRenewal.setText("YYYY-MM-DD");
                    txtRenewal.setForeground(Color.GRAY);
                }
            }
        });
        pnlForm.add(txtRenewal, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Category
        pnlForm.add(createStyledLabel("Category:"), gbc);
        gbc.gridx = 1;
        cbCategory = new JComboBox<>();
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(cbCategory, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Status
        pnlForm.add(createStyledLabel("Status:"), gbc);
        gbc.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"Active", "Cancelled", "Expired"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(cbStatus, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Free Trial Checkbox
        gbc.gridwidth = 2;
        chkTrial = new JCheckBox("Is Free Trial?");
        chkTrial.setBackground(PANEL_BG);
        chkTrial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlForm.add(chkTrial, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Buttons Panel (Save/Clear)
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel pnlFormButtons = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlFormButtons.setOpaque(false);

        btnSave = new JButton("Add Subscription");
        styleButton(btnSave, COLOR_BLUE, Color.WHITE);
        btnSave.addActionListener(e -> saveSubscriptionAction());

        btnClear = new JButton("Clear Form");
        styleButton(btnClear, COLOR_GRAY_BTN, Color.WHITE);
        btnClear.addActionListener(e -> clearForm());

        pnlFormButtons.add(btnSave);
        pnlFormButtons.add(btnClear);
        pnlForm.add(pnlFormButtons, gbc);

        // Fill remaining vertical space
        gbc.gridy++;
        gbc.weighty = 1.0;
        pnlForm.add(new JLabel(""), gbc);

        // --- RIGHT SIDE: Search, Filter, and Table ---
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBackground(BG_COLOR);

        // Search & Filter Panel (Top)
        JPanel pnlSearchFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlSearchFilter.setBackground(PANEL_BG);
        pnlSearchFilter.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER_COLOR));

        txtSearch = new JTextField(12);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        cbFilterCategory = new JComboBox<>();
        cbFilterCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbFilterCategory.addItem("All");

        cbFilterStatus = new JComboBox<>(new String[]{"All", "Active", "Cancelled", "Expired"});
        cbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnSearch = new JButton("Search");
        styleButton(btnSearch, COLOR_BLUE, Color.WHITE);

        btnSearch.addActionListener(e -> runSearchAndFilter());
        cbFilterCategory.addActionListener(e -> runSearchAndFilter());
        cbFilterStatus.addActionListener(e -> runSearchAndFilter());

        pnlSearchFilter.add(createStyledLabel("Search:"));
        pnlSearchFilter.add(txtSearch);
        pnlSearchFilter.add(createStyledLabel("Category:"));
        pnlSearchFilter.add(cbFilterCategory);
        pnlSearchFilter.add(createStyledLabel("Status:"));
        pnlSearchFilter.add(cbFilterStatus);
        pnlSearchFilter.add(btnSearch);

        pnlRight.add(pnlSearchFilter, BorderLayout.NORTH);

        // Table Panel (Center)
        JPanel pnlTableContainer = new JPanel(new BorderLayout(10, 10));
        pnlTableContainer.setBackground(PANEL_BG);
        pnlTableContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        String[] columns = {"ID", "Service Name", "Category", "Cost", "Billing Cycle", "Renewal Date", "Trial?", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                if (column == 3) return Double.class;
                return String.class;
            }
        };

        // Custom JTable with alternate row background colors and custom selection styling
        tblSubs = new JTable(tableModel) {
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

        tblSubs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblSubs.setRowHeight(28);
        tblSubs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblSubs.getTableHeader().setBackground(new Color(243, 244, 246));
        tblSubs.getTableHeader().setForeground(TEXT_DARK);
        tblSubs.setFillsViewportHeight(true);
        tblSubs.setAutoCreateRowSorter(true);

        // Currency Cell Renderer for Cost column (index 3)
        tblSubs.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(DashboardPanel.formatCurrency((Double) value));
                }
                return this;
            }
        });

        // Add selection listener to trigger Edit Mode
        tblSubs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblSubs.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = tblSubs.convertRowIndexToModel(selectedRow);
                    int subId = (int) tableModel.getValueAt(modelRow, 0);
                    Subscription selectedSub = findSubscriptionById(subId);
                    if (selectedSub != null) {
                        setToEditMode(selectedSub);
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(tblSubs);
        pnlTableContainer.add(tableScroll, BorderLayout.CENTER);

        // Cancel / Delete / Refresh Button Panel (Bottom of Table)
        JPanel pnlTableButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlTableButtons.setBackground(PANEL_BG);

        btnCancel = new JButton("Cancel Subscription");
        styleButton(btnCancel, COLOR_RED, Color.WHITE);
        btnCancel.addActionListener(e -> cancelSubscriptionAction());

        btnDelete = new JButton("Delete Subscription");
        styleButton(btnDelete, COLOR_GREEN, Color.WHITE);
        btnDelete.addActionListener(e -> deleteSubscriptionAction());

        btnRefresh = new JButton("Refresh List");
        styleButton(btnRefresh, COLOR_BLUE, Color.WHITE);
        btnRefresh.addActionListener(e -> refreshData());

        pnlTableButtons.add(btnCancel);
        pnlTableButtons.add(btnDelete);
        pnlTableButtons.add(btnRefresh);
        pnlTableContainer.add(pnlTableButtons, BorderLayout.SOUTH);

        pnlRight.add(pnlTableContainer, BorderLayout.CENTER);

        // Split Pane (Form on Left, Table on Right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlForm, pnlRight);
        splitPane.setDividerLocation(340);
        splitPane.setEnabled(false); // Fix divider location
        splitPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        return lbl;
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
        button.setPreferredSize(new Dimension(180, 42));
    }

    /**
     * Loads categories from Database to populate JComboBox fields.
     */
    private void loadCategories() {
        List<Category> categories = subscriptionDAO.getAllCategories();
        cbCategory.removeAllItems();
        cbFilterCategory.removeAllItems();
        cbFilterCategory.addItem("All");

        for (Category cat : categories) {
            cbCategory.addItem(cat);
            cbFilterCategory.addItem(cat.getCategoryName());
        }
    }

    /**
     * Triggers dynamic search and filter queries.
     */
    private void runSearchAndFilter() {
        String query = txtSearch.getText();
        String selectedCategory = (String) cbFilterCategory.getSelectedItem();
        String selectedStatus = (String) cbFilterStatus.getSelectedItem();
        List<Subscription> results = subscriptionDAO.searchSubscriptions(query, selectedCategory, selectedStatus);
        populateTable(results);
    }

    /**
     * Action performed when clicking Add/Update Subscription button.
     */
    private void saveSubscriptionAction() {
        String serviceName = txtService.getText().trim();
        String costText = txtCost.getText().trim();
        String cycle = (String) cbCycle.getSelectedItem();
        String renewalText = txtRenewal.getText().trim();
        Category category = (Category) cbCategory.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();
        boolean isTrial = chkTrial.isSelected();

        // 1. Validate Input
        if (serviceName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Service Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double cost = 0.0;
        try {
            cost = Double.parseDouble(costText);
            if (cost < 0) {
                JOptionPane.showMessageDialog(this, "Cost cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid decimal number for Cost.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.sql.Date renewalDate = null;
        try {
            if (!renewalText.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new IllegalArgumentException();
            }
            java.time.LocalDate.parse(renewalText); // Validate actual calendar date
            renewalDate = java.sql.Date.valueOf(renewalText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid Date in YYYY-MM-DD format (e.g. 2026-06-15). Check that the calendar date is valid.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (category == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid Category.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editingSubscriptionId == -1) {
            // Add Mode
            Subscription sub = new Subscription(serviceName, cost, cycle, renewalDate, category.getCategoryId(), isTrial, status);
            boolean success = subscriptionDAO.addSubscription(sub);
            if (success) {
                JOptionPane.showMessageDialog(this, "Subscription added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                parentFrame.refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding subscription. Check database logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Edit/Update Mode
            Subscription sub = new Subscription(editingSubscriptionId, serviceName, cost, cycle, renewalDate, category.getCategoryId(), isTrial, status);
            boolean success = subscriptionDAO.updateSubscription(sub);
            if (success) {
                JOptionPane.showMessageDialog(this, "Subscription updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                parentFrame.refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating subscription. Check database logs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Action performed when clicking Cancel Selected Subscription button.
     */
    private void cancelSubscriptionAction() {
        int selectedRow = tblSubs.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subscription from the table to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblSubs.convertRowIndexToModel(selectedRow);
        int subId = (int) tableModel.getValueAt(modelRow, 0);
        String status = (String) tableModel.getValueAt(modelRow, 7);

        if ("Cancelled".equals(status)) {
            JOptionPane.showMessageDialog(this, "This subscription is already cancelled.", "Validation Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the selected subscription?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = subscriptionDAO.cancelSubscription(subId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Subscription cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                parentFrame.refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(this, "Error cancelling subscription. Check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Action performed when clicking Delete Subscription button.
     */
    private void deleteSubscriptionAction() {
        int selectedRow = tblSubs.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subscription from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblSubs.convertRowIndexToModel(selectedRow);
        int subId = (int) tableModel.getValueAt(modelRow, 0);
        String serviceName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently delete the subscription for '" + serviceName + "'?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = subscriptionDAO.deleteSubscription(subId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Subscription deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                parentFrame.refreshAllTabs();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting subscription. Check database connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Refreshes Categories and Subscriptions Table
     */
    public void refreshData() {
        loadCategories();
        currentSubscriptions = subscriptionDAO.getAllSubscriptions();
        populateTable(currentSubscriptions);
    }

    private void populateTable(List<Subscription> list) {
        tableModel.setRowCount(0);
        for (Subscription sub : list) {
            tableModel.addRow(new Object[]{
                    sub.getSubscriptionId(),
                    sub.getServiceName(),
                    sub.getCategoryName(),
                    sub.getCost(), // Add raw Double to the table model for numerical sorting
                    sub.getBillingCycle(),
                    sub.getNextRenewalDate().toString(),
                    sub.isFreeTrial() ? "Yes" : "No",
                    sub.getStatus()
            });
        }
    }

    private Subscription findSubscriptionById(int id) {
        if (currentSubscriptions != null) {
            for (Subscription sub : currentSubscriptions) {
                if (sub.getSubscriptionId() == id) {
                    return sub;
                }
            }
        }
        return null;
    }

    private void setToEditMode(Subscription sub) {
        editingSubscriptionId = sub.getSubscriptionId();
        lblFormTitle.setText("Edit Subscription");
        btnSave.setText("Update Subscription");
        btnClear.setText("Cancel Edit");

        txtService.setText(sub.getServiceName());
        txtCost.setText(String.valueOf(sub.getCost()));
        cbCycle.setSelectedItem(sub.getBillingCycle());
        txtRenewal.setText(sub.getNextRenewalDate().toString());
        txtRenewal.setForeground(TEXT_DARK);
        chkTrial.setSelected(sub.isFreeTrial());
        cbStatus.setSelectedItem(sub.getStatus());

        // Find and select category in combo box
        for (int i = 0; i < cbCategory.getItemCount(); i++) {
            Category cat = cbCategory.getItemAt(i);
            if (cat.getCategoryId() == sub.getCategoryId()) {
                cbCategory.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        editingSubscriptionId = -1;
        lblFormTitle.setText("Add Subscription");
        btnSave.setText("Add Subscription");
        btnClear.setText("Clear Form");

        txtService.setText("");
        txtCost.setText("");
        cbCycle.setSelectedIndex(0);
        txtRenewal.setText("YYYY-MM-DD");
        txtRenewal.setForeground(Color.GRAY);
        chkTrial.setSelected(false);
        cbStatus.setSelectedIndex(0);
        if (cbCategory.getItemCount() > 0) {
            cbCategory.setSelectedIndex(0);
        }

        tblSubs.clearSelection();
    }
}
