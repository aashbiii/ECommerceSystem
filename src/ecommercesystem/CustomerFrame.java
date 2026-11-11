package ecommercesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CustomerFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtEmail, txtPhone, txtAddress;
    private int selectedId = -1;

    public CustomerFrame() {
        setTitle("Customer Management");
        setSize(1000, 580);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        add(UIUtils.createHeader("👥   Customer Management"), BorderLayout.NORTH);

        // LEFT
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(UIUtils.COLOR_BACKGROUND);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 8));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        JPanel card = UIUtils.createCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtName    = UIUtils.createTextField();
        txtEmail   = UIUtils.createTextField();
        txtPhone   = UIUtils.createTextField();
        txtAddress = UIUtils.createTextField();

        String[]     labels = {"Full Name", "Email Address", "Phone Number", "Address"};
        JTextField[] fields = {txtName, txtEmail, txtPhone, txtAddress};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridy = i * 2; gbc.gridx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
            lbl.setForeground(UIUtils.COLOR_TEXT);
            card.add(lbl, gbc);
            gbc.gridy = i * 2 + 1;
            fields[i].setPreferredSize(new Dimension(240, 34));
            card.add(fields[i], gbc);
        }

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton btnAdd    = UIUtils.createButton("➕ Add",   UIUtils.COLOR_SUCCESS);
        JButton btnUpdate = UIUtils.createButton("✏ Update", UIUtils.COLOR_WARNING);
        JButton btnDelete = UIUtils.createButton("🗑 Delete", UIUtils.COLOR_DANGER);
        JButton btnClear  = UIUtils.createButton("Clear",    UIUtils.COLOR_SUBTLE);

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);

        gbc.gridy = labels.length * 2;
        card.add(btnPanel, gbc);
        leftPanel.add(card, BorderLayout.NORTH);

        // RIGHT
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(UIUtils.COLOR_BACKGROUND);
        rightPanel.setBorder(new EmptyBorder(15, 8, 15, 15));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        JTextField txtSearch = UIUtils.createTextField();
        txtSearch.setPreferredSize(new Dimension(0, 36));

        JButton btnSearch  = UIUtils.createButton("🔍 Search", UIUtils.COLOR_ACCENT);
        JButton btnRefresh = UIUtils.createButton("↺ All",     UIUtils.COLOR_PRIMARY);
        btnSearch.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        btnRefresh.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));

        JPanel sb = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        sb.setOpaque(false);
        sb.add(btnSearch); sb.add(btnRefresh);

        JLabel lblSearch = new JLabel("Search: ");
        lblSearch.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblSearch.setForeground(UIUtils.COLOR_TEXT);

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(sb, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Email", "Phone", "Address"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIUtils.COLOR_SUBTLE));
        scroll.getViewport().setBackground(UIUtils.COLOR_WHITE);

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(scroll, BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedId = (int) model.getValueAt(row, 0);
                txtName.setText((String) model.getValueAt(row, 1));
                txtEmail.setText((String) model.getValueAt(row, 2));
                txtPhone.setText((String) model.getValueAt(row, 3));
                txtAddress.setText((String) model.getValueAt(row, 4));
            }
        });

        btnAdd.addActionListener(e -> {
            if (txtName.getText().isEmpty()) { showError("Name is required!"); return; }
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO customers (name,email,phone,address) VALUES (?,?,?,?)");
                ps.setString(1, txtName.getText()); ps.setString(2, txtEmail.getText());
                ps.setString(3, txtPhone.getText()); ps.setString(4, txtAddress.getText());
                ps.executeUpdate();
                showSuccess("Customer added!"); clearForm(); loadCustomers();
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) { showError("Select a customer!"); return; }
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE customers SET name=?,email=?,phone=?,address=? WHERE id=?");
                ps.setString(1, txtName.getText()); ps.setString(2, txtEmail.getText());
                ps.setString(3, txtPhone.getText()); ps.setString(4, txtAddress.getText());
                ps.setInt(5, selectedId);
                ps.executeUpdate();
                showSuccess("Customer updated!"); clearForm(); loadCustomers();
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId == -1) { showError("Select a customer!"); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getConnection()) {
                    PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE id=?");
                    ps.setInt(1, selectedId); ps.executeUpdate();
                    clearForm(); loadCustomers();
                } catch (SQLException ex) { showError(ex.getMessage()); }
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnSearch.addActionListener(e -> {
            String kw = "%" + txtSearch.getText() + "%";
            model.setRowCount(0);
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM customers WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?");
                ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("phone"), rs.getString("address")
                    });
                }
            } catch (SQLException ex) { showError(ex.getMessage()); }
        });

        btnRefresh.addActionListener(e -> loadCustomers());

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void loadCustomers() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("phone"), rs.getString("address")
                });
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText(""); txtEmail.setText("");
        txtPhone.setText(""); txtAddress.setText("");
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}