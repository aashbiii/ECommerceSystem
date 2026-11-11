package ecommercesystem;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class DashboardFrame extends JFrame {

    private String username, role;
    private int customerId = -1;

    public DashboardFrame(String username, String role, int customerId) {
        this.username   = username;
        this.role       = role;
        this.customerId = customerId;
        if (this.customerId == -1 && "customer".equalsIgnoreCase(role)) {
            this.customerId = resolveCustomerId(username);
        }
        setTitle("Dashboard");
        setSize(860, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    public DashboardFrame(String username, String role) {
        this(username, role, -1);
    }

    private int resolveCustomerId(String username) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT c.id FROM customers c " +
                "JOIN users u ON LOWER(c.email) = LOWER(u.email) " +
                "WHERE u.username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return -1;
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        // ── TOPBAR ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(44, 62, 80));
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBorder(new EmptyBorder(0, 24, 0, 16));

        JLabel lblTitle = new JLabel("E-Commerce System");
        lblTitle.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 17));
        lblTitle.setForeground(Color.WHITE);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        topRight.setOpaque(false);

        JLabel lblBadge = new JLabel(role.toUpperCase());
        lblBadge.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 10));
        lblBadge.setForeground(new Color(44, 62, 80));
        lblBadge.setBackground("admin".equalsIgnoreCase(role)
            ? new Color(241, 196, 15)
            : new Color(46, 204, 113));
        lblBadge.setOpaque(true);
        lblBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel lblUser = new JLabel(username);
        lblUser.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 13));
        lblUser.setForeground(new Color(189, 195, 199));

        // ── ROUNDED SIGN OUT BUTTON ──
        JButton btnLogout = roundedBtn("Sign out",
            new Color(231, 76, 60), new Dimension(88, 30));
        btnLogout.addActionListener(e -> {
            dispose(); new LoginFrame().setVisible(true);
        });

        topRight.add(lblBadge);
        topRight.add(lblUser);
        topRight.add(Box.createHorizontalStrut(4));
        topRight.add(btnLogout);

        topBar.add(lblTitle,  BorderLayout.WEST);
        topBar.add(topRight,  BorderLayout.EAST);

        // ── BODY ──
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(new Color(245, 247, 250));
        body.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel lblWelcome = new JLabel(
            "admin".equalsIgnoreCase(role)
            ? "Welcome back, " + username + "!"
            : "Hello, " + username + "!");
        lblWelcome.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 20));
        lblWelcome.setForeground(new Color(44, 62, 80));
        lblWelcome.setBorder(new EmptyBorder(0, 0, 18, 0));

        // ── STATS ──
        JPanel statsRow = new JPanel(new GridLayout(1,
            "admin".equalsIgnoreCase(role) ? 4 : 3, 14, 0));
        statsRow.setOpaque(false);

        if ("admin".equalsIgnoreCase(role)) {
            statsRow.add(statCard("Products",   getCount("SELECT COUNT(*) FROM products"),  new Color(52,152,219), "Total products in store"));
            statsRow.add(statCard("Customers",  getCount("SELECT COUNT(*) FROM customers"), new Color(39,174,96),  "Registered customers"));
            statsRow.add(statCard("Orders",     getCount("SELECT COUNT(*) FROM orders"),    new Color(230,126,34), "All time orders"));
            statsRow.add(statCard("Revenue",    "Rs." + getRevenue(),                       new Color(155,89,182), "Excluding cancelled"));
        } else {
            statsRow.add(statCard("Products",    getCount("SELECT COUNT(*) FROM products"), new Color(52,152,219), "Available in store"));
            statsRow.add(statCard("My Orders",   String.valueOf(getCustomerOrderCount()),   new Color(230,126,34), "Orders placed by you"));
            statsRow.add(statCard("Total Spent", "Rs." + getCustomerTotalSpent(),           new Color(39,174,96),  "Your total spending"));
        }

        JLabel lblSection = new JLabel(
            "admin".equalsIgnoreCase(role) ? "Management" : "Quick Actions");
        lblSection.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblSection.setForeground(new Color(149, 165, 166));
        lblSection.setBorder(new EmptyBorder(22, 0, 10, 0));

        // ── NAV ──
        JPanel navRow = new JPanel(new GridLayout(1,
            "admin".equalsIgnoreCase(role) ? 3 : 2, 14, 0));
        navRow.setOpaque(false);

        if ("admin".equalsIgnoreCase(role)) {
            navRow.add(navCard("Products",  "Add, edit, delete products",    new Color(52,152,219), e -> new ProductFrame().setVisible(true)));
            navRow.add(navCard("Customers", "View and manage customers",     new Color(39,174,96),  e -> new CustomerFrame().setVisible(true)));
            navRow.add(navCard("Orders",    "Track and update orders",       new Color(230,126,34), e -> new OrderFrame().setVisible(true)));
        } else {
            navRow.add(navCard("Shop",      "Browse and buy products",       new Color(52,152,219), e -> new CartFrame(customerId).setVisible(true)));
            navRow.add(navCard("My Orders", "View your order history",       new Color(230,126,34), e -> new CustomerOrderFrame(customerId).setVisible(true)));
        }

        JLabel lblFooter = new JLabel("E-Commerce Management System  |  v1.0");
        lblFooter.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 10));
        lblFooter.setForeground(new Color(189, 195, 199));
        lblFooter.setBorder(new EmptyBorder(16, 0, 0, 0));

        JPanel centerBody = new JPanel();
        centerBody.setLayout(new BoxLayout(centerBody, BoxLayout.Y_AXIS));
        centerBody.setOpaque(false);
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        navRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerBody.add(lblWelcome);
        centerBody.add(statsRow);
        centerBody.add(lblSection);
        centerBody.add(navRow);
        centerBody.add(lblFooter);

        body.add(centerBody, BorderLayout.CENTER);
        root.add(topBar, BorderLayout.NORTH);
        root.add(body,   BorderLayout.CENTER);
    }

    private JPanel statCard(String label, String value, Color accent, String sub) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(16, 18, 16, 18)));
        JPanel accentLine = new JPanel();
        accentLine.setBackground(accent);
        accentLine.setPreferredSize(new Dimension(0, 3));
        card.add(accentLine, BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 0, 0, 0));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 24));
        lblValue.setForeground(new Color(44, 62, 80));
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        lblLabel.setForeground(new Color(44, 62, 80));
        lblLabel.setBorder(new EmptyBorder(4, 0, 2, 0));
        JLabel lblSub = new JLabel(sub);
        lblSub.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 10));
        lblSub.setForeground(new Color(149, 165, 166));
        content.add(lblValue);
        content.add(lblLabel);
        content.add(lblSub);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel navCard(String title, String subtitle,
            Color accent, ActionListener al) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(18, 20, 18, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(4, 0));
        card.add(bar, BorderLayout.WEST);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 14, 0, 0));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 15));
        lblTitle.setForeground(new Color(44, 62, 80));
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 11));
        lblSub.setForeground(new Color(149, 165, 166));
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));
        JLabel lblArrow = new JLabel("→");
        lblArrow.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 16));
        lblArrow.setForeground(accent);
        content.add(lblTitle);
        content.add(lblSub);
        card.add(content,  BorderLayout.CENTER);
        card.add(lblArrow, BorderLayout.EAST);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 250, 252)); card.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE); card.repaint();
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                al.actionPerformed(null);
            }
        });
        return card;
    }

    // ── ROUNDED BUTTON HELPER ──
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

    private String getCount(String sql) {
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (SQLException e) {}
        return "0";
    }

    private String getRevenue() {
        try (Connection con = DBConnection.getConnection();
             Statement st  = con.createStatement();
             ResultSet rs  = st.executeQuery(
                "SELECT NVL(SUM(total_amount),0) FROM orders WHERE status != 'cancelled'")) {
            if (rs.next()) return String.format("%.0f", rs.getDouble(1));
        } catch (SQLException e) {}
        return "0";
    }

    private int getCustomerOrderCount() {
        if (customerId == -1) return 0;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM orders WHERE customer_id=?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    private String getCustomerTotalSpent() {
        if (customerId == -1) return "0";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                "SELECT NVL(SUM(total_amount),0) FROM orders WHERE customer_id=?")) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return String.format("%.0f", rs.getDouble(1));
        } catch (SQLException e) {}
        return "0";
    }
}