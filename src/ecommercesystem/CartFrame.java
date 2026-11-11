package ecommercesystem;

import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class CartFrame extends JFrame {

    private JTable            productTable, cartTable;
    private DefaultTableModel productModel, cartModel;
    private JLabel            lblTotal, lblCustomerName;
    private Map<Integer, Double> productPrices = new HashMap<>();
    private double totalAmount       = 0;
    private int    currentCustomerId = -1;

    public CartFrame() { this(-1); }

    public CartFrame(int customerId) {
        setTitle("Shop");
        setSize(1100, 640);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        this.currentCustomerId = customerId;
        initComponents();
        loadProducts();
        loadCustomerName();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel lblH = new JLabel("Shop - Browse & Order");
        lblH.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 17));
        lblH.setForeground(Color.WHITE);

        lblCustomerName = new JLabel("");
        lblCustomerName.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 12));
        lblCustomerName.setForeground(new Color(189, 195, 199));

        header.add(lblH,            BorderLayout.WEST);
        header.add(lblCustomerName, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── LEFT - Products ──
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(245, 247, 250));
        leftPanel.setBorder(new EmptyBorder(14, 14, 14, 7));

        JLabel lblP = new JLabel("Available Products");
        lblP.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblP.setForeground(new Color(44, 62, 80));
        lblP.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lblHint = new JLabel("Double-click to add to cart");
        lblHint.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 10));
        lblHint.setForeground(new Color(149, 165, 166));
        lblHint.setBorder(new EmptyBorder(6, 0, 0, 0));

        String[] pCols = {"ID","Name","Price","Stock","Category"};
        productModel = new DefaultTableModel(pCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productModel);
        UIUtils.styleTable(productTable);
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) addToCart();
            }
        });

        JScrollPane ps = new JScrollPane(productTable);
        ps.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        ps.getViewport().setBackground(Color.WHITE);

        leftPanel.add(lblP,   BorderLayout.NORTH);
        leftPanel.add(ps,     BorderLayout.CENTER);
        leftPanel.add(lblHint,BorderLayout.SOUTH);

        // ── RIGHT - Cart ──
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(245, 247, 250));
        rightPanel.setBorder(new EmptyBorder(14, 7, 14, 14));

        JLabel lblC = new JLabel("Your Cart");
        lblC.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblC.setForeground(new Color(44, 62, 80));
        lblC.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] cCols = {"Product ID","Product Name","Qty","Unit Price","Subtotal"};
        cartModel = new DefaultTableModel(cCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        UIUtils.styleTable(cartTable);

        JScrollPane cs = new JScrollPane(cartTable);
        cs.setBorder(new LineBorder(new Color(220, 220, 220), 1));
        cs.getViewport().setBackground(Color.WHITE);

        // action bar
        JPanel actionBar = new JPanel(new BorderLayout());
        actionBar.setBackground(Color.WHITE);
        actionBar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 14, 10, 14)));

        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftActions.setOpaque(false);
        JButton btnRemove = actionBtn("Remove",     new Color(231, 76, 60));
        JButton btnClear  = actionBtn("Clear Cart", new Color(149, 165, 166));
        btnRemove.setPreferredSize(new Dimension(90, 32));
        btnClear.setPreferredSize(new Dimension(90, 32));
        leftActions.add(btnRemove);
        leftActions.add(btnClear);

        lblTotal = new JLabel("Total: Rs.0.00");
        lblTotal.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 16));
        lblTotal.setForeground(new Color(231, 76, 60));

        actionBar.add(leftActions, BorderLayout.WEST);
        actionBar.add(lblTotal,    BorderLayout.EAST);

        // place order bar
        JPanel orderBar = new JPanel(new BorderLayout());
        orderBar.setBackground(new Color(245, 247, 250));
        orderBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnPlace = new JButton("Place Order");
        btnPlace.setBackground(new Color(39, 174, 96));
        btnPlace.setForeground(Color.WHITE);
        btnPlace.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 14));
        btnPlace.setFocusPainted(false);
        btnPlace.setBorderPainted(false);
        btnPlace.setOpaque(true);
        btnPlace.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlace.setPreferredSize(new Dimension(140, 40));
        btnPlace.setBorder(new EmptyBorder(0, 0, 0, 0));
        btnPlace.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnPlace.setBackground(new Color(39, 174, 96).brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnPlace.setBackground(new Color(39, 174, 96));
            }
        });

        orderBar.add(btnPlace, BorderLayout.EAST);

        JPanel rightBottom = new JPanel(new BorderLayout());
        rightBottom.setBackground(new Color(245, 247, 250));
        rightBottom.add(actionBar, BorderLayout.NORTH);
        rightBottom.add(orderBar,  BorderLayout.SOUTH);

        rightPanel.add(lblC,        BorderLayout.NORTH);
        rightPanel.add(cs,          BorderLayout.CENTER);
        rightPanel.add(rightBottom, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(530);
        split.setDividerSize(4);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e  -> { cartModel.setRowCount(0); updateTotal(); });
        btnPlace.addActionListener(e  -> placeOrder());
    }

    private void loadCustomerName() {
        if (currentCustomerId == -1) {
            lblCustomerName.setText("Admin Mode");
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT name FROM customers WHERE id=?")) {
            ps.setInt(1, currentCustomerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                lblCustomerName.setText("Shopping as: " + rs.getString("name"));
        } catch (SQLException e) {}
    }

    private void loadProducts() {
        productModel.setRowCount(0);
        productPrices.clear();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT * FROM products WHERE stock > 0 ORDER BY name")) {
            while (rs.next()) {
                int    id    = rs.getInt("id");
                double price = rs.getDouble("price");
                productPrices.put(id, price);
                productModel.addRow(new Object[]{
                    id, rs.getString("name"), "Rs." + price,
                    rs.getInt("stock"), rs.getString("category")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) return;
        int    pid   = (int)    productModel.getValueAt(row, 0);
        String pname = (String) productModel.getValueAt(row, 1);
        double price = productPrices.get(pid);

        for (int i = 0; i < cartModel.getRowCount(); i++) {
            if ((int) cartModel.getValueAt(i, 0) == pid) {
                int qty = (int) cartModel.getValueAt(i, 2) + 1;
                cartModel.setValueAt(qty,         i, 2);
                cartModel.setValueAt(qty * price, i, 4);
                updateTotal(); return;
            }
        }
        cartModel.addRow(new Object[]{pid, pname, 1, price, price});
        updateTotal();
    }

    private void removeFromCart() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) { cartModel.removeRow(row); updateTotal(); }
        else JOptionPane.showMessageDialog(this, "Select an item to remove!");
    }

    private void updateTotal() {
        totalAmount = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++)
            totalAmount += (double) cartModel.getValueAt(i, 4);
        lblTotal.setText(String.format("Total: Rs.%.2f", totalAmount));
    }

    private void placeOrder() {
        if (cartModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty!"); return;
        }
        if (currentCustomerId == -1) {
            JOptionPane.showMessageDialog(this,
                "No customer linked. Please log in as a customer."); return;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders (customer_id,total_amount,status) VALUES (?,?,'pending')",
                    new String[]{"ID"});
                ps.setInt(1, currentCustomerId);
                ps.setDouble(2, totalAmount);
                ps.executeUpdate();

                ResultSet rs      = ps.getGeneratedKeys();
                int       orderId = rs.next() ? rs.getInt(1) : -1;

                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    int    pid   = (int)    cartModel.getValueAt(i, 0);
                    int    qty   = (int)    cartModel.getValueAt(i, 2);
                    double price = (double) cartModel.getValueAt(i, 3);

                    PreparedStatement ps2 = con.prepareStatement(
                        "INSERT INTO order_items (order_id,product_id,quantity,unit_price) " +
                        "VALUES (?,?,?,?)");
                    ps2.setInt(1, orderId); ps2.setInt(2, pid);
                    ps2.setInt(3, qty);     ps2.setDouble(4, price);
                    ps2.executeUpdate();

                    PreparedStatement ps3 = con.prepareStatement(
                        "UPDATE products SET stock = stock - ? WHERE id=?");
                    ps3.setInt(1, qty); ps3.setInt(2, pid);
                    ps3.executeUpdate();
                }
                con.commit();
                JOptionPane.showMessageDialog(this,
                    "Order #" + orderId + " placed!\nTotal: Rs." +
                    String.format("%.2f", totalAmount),
                    "Order Placed", JOptionPane.INFORMATION_MESSAGE);
                cartModel.setRowCount(0);
                updateTotal();
                loadProducts();
            } catch (SQLException ex) {
                con.rollback();
                JOptionPane.showMessageDialog(this, "Order failed: " + ex.getMessage());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private JButton actionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setOpaque(true); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }
}