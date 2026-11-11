package ecommercesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class CustomerOrderFrame extends JFrame {

    private JTable            table;
    private DefaultTableModel model;
    private int               customerId;

    public CustomerOrderFrame(int customerId) {
        setTitle("My Orders");
        setSize(860, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        this.customerId = customerId;
        initComponents();
        loadOrders();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel lblH = new JLabel("My Orders");
        lblH.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 17));
        lblH.setForeground(Color.WHITE);
        header.add(lblH, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Order ID","Order Date","Total (Rs.)","Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);

        table.getColumnModel().getColumn(3).setCellRenderer(
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object val,
                        boolean sel, boolean foc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(
                        t, val, sel, foc, r, c);
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                    setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
                    if (!sel) {
                        comp.setBackground(UIUtils.statusColor(
                            val != null ? val.toString() : ""));
                        setForeground(new Color(44, 62, 80));
                    }
                    setHorizontalAlignment(CENTER);
                    return comp;
                }
            });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(14, 14, 0, 14));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── BOTTOM BAR ──
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            new EmptyBorder(12, 16, 12, 16)));

        JLabel lblInfo = new JLabel(
            "You can cancel orders that are not yet delivered.");
        lblInfo.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 11));
        lblInfo.setForeground(new Color(149, 165, 166));

        JButton btnCancel  = roundedBtn("Cancel Order", new Color(231, 76, 60),  new Dimension(120, 34));
        JButton btnRefresh = roundedBtn("Refresh",      new Color(44,  62,  80), new Dimension(88,  34));

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(btnCancel);
        rightBtns.add(btnRefresh);

        bottomBar.add(lblInfo,    BorderLayout.WEST);
        bottomBar.add(rightBtns, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(245, 247, 250));
        center.add(scroll,    BorderLayout.CENTER);
        center.add(bottomBar, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadOrders());

        btnCancel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an order to cancel!"); return;
            }
            int    orderId = (int)    model.getValueAt(row, 0);
            String status  = (String) model.getValueAt(row, 3);
            if ("delivered".equalsIgnoreCase(status) ||
                "cancelled".equalsIgnoreCase(status)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot cancel a " + status + " order!"); return;
            }
            if (JOptionPane.showConfirmDialog(this,
                    "Cancel order #" + orderId + "?", "Confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try (Connection con = DBConnection.getConnection()) {
                    PreparedStatement ps = con.prepareStatement(
                        "UPDATE orders SET status='cancelled' WHERE id=?");
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this,
                        "Order #" + orderId + " cancelled.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });
    }

    private void loadOrders() {
        model.setRowCount(0);
        if (customerId == -1) {
            JOptionPane.showMessageDialog(this,
                "Customer record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT id, " +
                "TO_CHAR(order_date,'DD-MON-YYYY HH24:MI') as odate, " +
                "total_amount, status " +
                "FROM orders WHERE customer_id=? ORDER BY id DESC")) {
            ps.setInt(1, customerId);
            ResultSet rs  = ps.executeQuery();
            boolean   any = false;
            while (rs.next()) {
                any = true;
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("odate"),
                    "Rs." + String.format("%.2f", rs.getDouble("total_amount")),
                    rs.getString("status")
                });
            }
            if (!any) {
                JOptionPane.showMessageDialog(this,
                    "You have not placed any orders yet.",
                    "No Orders", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private JButton roundedBtn(String text, Color bg, Dimension size) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(getForeground());
                g2.setFont(getFont());
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(size);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker()); btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg); btn.repaint();
            }
        });
        return btn;
    }
}