package ecommercesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class OrderFrame extends JFrame {

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> cmbStatus;
    private int               selectedId = -1;

    public OrderFrame() {
        setTitle("Order Management");
        setSize(980, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
        loadOrders();
    }

    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel lblH = new JLabel("Order Management");
        lblH.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 17));
        lblH.setForeground(Color.WHITE);
        header.add(lblH, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Order ID","Customer","Order Date","Total (Rs.)","Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UIUtils.styleTable(table);

        table.getColumnModel().getColumn(4).setCellRenderer(
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

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedId = (int) model.getValueAt(row, 0);
                cmbStatus.setSelectedItem(model.getValueAt(row, 4).toString());
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

        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBar.setOpaque(false);

        JLabel lblS = new JLabel("Change status:");
        lblS.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
        lblS.setForeground(new Color(149, 165, 166));

        String[] statuses = {"pending","processing","shipped","delivered","cancelled"};
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 13));
        cmbStatus.setPreferredSize(new Dimension(150, 34));
        cmbStatus.setBorder(new LineBorder(new Color(220, 220, 220), 1));

        JButton btnUpdate  = roundedBtn("Update",  new Color(230, 126, 34), new Dimension(90, 34));
        JButton btnRefresh = roundedBtn("Refresh", new Color(44,  62,  80), new Dimension(88, 34));

        leftBar.add(lblS);
        leftBar.add(cmbStatus);
        leftBar.add(btnUpdate);
        leftBar.add(btnRefresh);

        // legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        legend.setOpaque(false);
        legend.add(legendDot("Pending",    Color.WHITE));
        legend.add(legendDot("Processing", UIUtils.statusColor("processing")));
        legend.add(legendDot("Shipped",    UIUtils.statusColor("shipped")));
        legend.add(legendDot("Delivered",  UIUtils.statusColor("delivered")));
        legend.add(legendDot("Cancelled",  UIUtils.statusColor("cancelled")));

        bottomBar.add(leftBar, BorderLayout.WEST);
        bottomBar.add(legend,  BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(245, 247, 250));
        center.add(scroll,    BorderLayout.CENTER);
        center.add(bottomBar, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(this, "Select an order!"); return;
            }
            try (Connection con = DBConnection.getConnection()) {
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE orders SET status=? WHERE id=?");
                ps.setString(1, (String) cmbStatus.getSelectedItem());
                ps.setInt(2, selectedId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Status updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnRefresh.addActionListener(e -> loadOrders());
    }

    private JPanel legendDot(String label, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JPanel dot = new JPanel();
        dot.setBackground(color);
        dot.setPreferredSize(new Dimension(12, 12));
        dot.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 10));
        lbl.setForeground(new Color(149, 165, 166));
        p.add(dot); p.add(lbl);
        return p;
    }

    private void loadOrders() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                "SELECT o.id, NVL(c.name,'Unknown') as cname, " +
                "TO_CHAR(o.order_date,'DD-MON-YYYY HH24:MI') as odate, " +
                "o.total_amount, o.status " +
                "FROM orders o LEFT JOIN customers c ON o.customer_id=c.id " +
                "ORDER BY o.id DESC")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("cname"),
                    rs.getString("odate"), rs.getDouble("total_amount"),
                    rs.getString("status")
                });
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