package ecommercesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ProductFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtDesc, txtPrice, txtStock, txtCategory, txtSearch;
    private int selectedId = -1;

    public ProductFrame() {
        setTitle("Product Management");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        add(UIUtils.createHeader("📦   Product Management"), BorderLayout.NORTH);

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

        txtName     = UIUtils.createTextField();
        txtDesc     = UIUtils.createTextField();
        txtPrice    = UIUtils.createTextField();
        txtStock    = UIUtils.createTextField();
        txtCategory = UIUtils.createTextField();

        String[]     labels = {"Product Name", "Description", "Price (₹)", "Stock", "Category"};
        JTextField[] fields = {txtName, txtDesc, txtPrice, txtStock, txtCategory};

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

        JButton btnAdd    = UIUtils.createButton("➕ Add",    UIUtils.COLOR_SUCCESS);
        JButton btnUpdate = UIUtils.createButton("✏ Update",  UIUtils.COLOR_WARNING);
        JButton btnDelete = UIUtils.createButton("🗑 Delete",  UIUtils.COLOR_DANGER);
        JButton btnClear  = UIUtils.createButton("Clear",     UIUtils.COLOR_SUBTLE);

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
        txtSearch = UIUtils.createTextField();
        txtSearch.setPreferredSize(new Dimension(0, 36));

        JButton btnSearch  = UIUtils.createButton("🔍 Search", UIUtils.COLOR_ACCENT);
        JButton btnRefresh = UIUtils.createButton("↺ All",     UIUtils.COLOR_PRIMARY);
        btnSearch.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        btnRefresh.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));

        JPanel searchBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchBtns.setOpaque(false);
        searchBtns.add(btnSearch); searchBtns.add(btnRefresh);

        JLabel lblSearch = new JLabel("Search: ");
        lblSearch.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblSearch.setForeground(UIUtils.COLOR_TEXT);

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(searchBtns, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Description", "Price", "Stock", "Category"};
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

        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> searchProducts());
        btnRefresh.addActionListener(e -> loadProducts());

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void loadProducts() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM products ORDER BY id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                    "₹" + rs.getDouble("price"), rs.getInt("stock"), rs.getString("category")
                });
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void addProduct() {
        if (txtName.getText().isEmpty() || txtPrice.getText().isEmpty()) {
            showError("Name and Price are required!"); return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO products (name,description,price,stock,category) VALUES (?,?,?,?,?)");
            ps.setString(1, txtName.getText());
            ps.setString(2, txtDesc.getText());
            ps.setDouble(3, Double.parseDouble(txtPrice.getText()));
            ps.setInt(4, txtStock.getText().isEmpty() ? 0 : Integer.parseInt(txtStock.getText()));
            ps.setString(5, txtCategory.getText());
            ps.executeUpdate();
            showSuccess("Product added successfully!");
            clearForm(); loadProducts();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void updateProduct() {
        if (selectedId == -1) { showError("Select a product first!"); return; }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "UPDATE products SET name=?,description=?,price=?,stock=?,category=? WHERE id=?");
            ps.setString(1, txtName.getText());
            ps.setString(2, txtDesc.getText());
            ps.setDouble(3, Double.parseDouble(txtPrice.getText()));
            ps.setInt(4, Integer.parseInt(txtStock.getText()));
            ps.setString(5, txtCategory.getText());
            ps.setInt(6, selectedId);
            ps.executeUpdate();
            showSuccess("Product updated!");
            clearForm(); loadProducts();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void deleteProduct() {
        if (selectedId == -1) { showError("Select a product first!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE id=?");
                ps.setInt(1, selectedId);
                ps.executeUpdate();
                clearForm(); loadProducts();
            } catch (SQLException e) { showError(e.getMessage()); }
        }
    }

    private void searchProducts() {
        String kw = "%" + txtSearch.getText() + "%";
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?");
            ps.setString(1, kw); ps.setString(2, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("description"),
                    "₹" + rs.getDouble("price"), rs.getInt("stock"), rs.getString("category")
                });
            }
        } catch (SQLException e) { showError(e.getMessage()); }
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) model.getValueAt(row, 0);
            txtName.setText((String) model.getValueAt(row, 1));
            txtDesc.setText((String) model.getValueAt(row, 2));
            txtPrice.setText(model.getValueAt(row, 3).toString().replace("₹", ""));
            txtStock.setText(String.valueOf(model.getValueAt(row, 4)));
            txtCategory.setText((String) model.getValueAt(row, 5));
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText(""); txtDesc.setText(""); txtPrice.setText("");
        txtStock.setText(""); txtCategory.setText("");
        table.clearSelection();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}