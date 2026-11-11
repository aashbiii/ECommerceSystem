package ecommercesystem;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class RegisterFrame extends JFrame {

    private JTextField     txtUsername, txtEmail, txtName, txtPhone, txtAddress;
    private JPasswordField txtPassword;
    private JLabel         lblStatus;

    public RegisterFrame() {
        setTitle("E-Commerce System - Register");
        setSize(440, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 247, 250));
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(32, 36, 32, 36)));
        card.setPreferredSize(new Dimension(370, 600));

        JPanel accent = new JPanel();
        accent.setBackground(new Color(39, 174, 96));
        accent.setPreferredSize(new Dimension(0, 4));
        accent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        JLabel lblTitle = new JLabel("Create Account");
        lblTitle.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Fill in your details to register");
        lblSub.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 12));
        lblSub.setForeground(new Color(149, 165, 166));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = field(); txtPassword = passField();
        txtEmail    = field(); txtName     = field();
        txtPhone    = field(); txtAddress  = field();

        String[]    labels = {"Username","Password","Email","Full Name","Phone","Address"};
        JComponent[] fields = {txtUsername, txtPassword, txtEmail,
                               txtName, txtPhone, txtAddress};

        // ── ROUNDED BUTTONS ──
        JButton btnRegister = roundedBtn("Register",      new Color(39, 174, 96));
        JButton btnBack     = roundedBtn("Back to Sign In", new Color(52, 152, 219));
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 11));
        lblStatus.setForeground(new Color(231, 76, 60));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        card.add(accent);
        card.add(Box.createVerticalStrut(20));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(20));

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
            lbl.setForeground(new Color(44, 62, 80));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(lbl);
            card.add(Box.createVerticalStrut(4));
            card.add(fields[i]);
            card.add(Box.createVerticalStrut(10));
        }

        card.add(Box.createVerticalStrut(8));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(6));
        card.add(btnBack);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);

        root.add(card);
    }

    private JTextField field() {
        JTextField f = UIUtils.createTextField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JPasswordField passField() {
        JPasswordField f = UIUtils.createPasswordField();
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String email    = txtEmail.getText().trim();
        String name     = txtName.getText().trim();
        String phone    = txtPhone.getText().trim();
        String addr     = txtAddress.getText().trim();

        if (username.isEmpty() || password.isEmpty() ||
                email.isEmpty() || name.isEmpty()) {
            lblStatus.setText("Required fields cannot be empty"); return;
        }
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users (username,password,role,email) VALUES (?,?,?,?)");
                ps.setString(1, username); ps.setString(2, password);
                ps.setString(3, "customer"); ps.setString(4, email);
                ps.executeUpdate();

                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO customers (name,email,phone,address) VALUES (?,?,?,?)",
                    new String[]{"ID"});
                ps2.setString(1, name);  ps2.setString(2, email);
                ps2.setString(3, phone); ps2.setString(4, addr);
                ps2.executeUpdate();

                int custId = -1;
                try (ResultSet gr = ps2.getGeneratedKeys()) {
                    if (gr.next()) custId = gr.getInt(1);
                }
                con.commit();
                dispose();
                new DashboardFrame(username, "customer", custId).setVisible(true);
            } catch (SQLException ex) {
                con.rollback();
                lblStatus.setText("Failed: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            lblStatus.setText("DB error: " + ex.getMessage());
        }
    }

    private JButton roundedBtn(String text, Color bg) {
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
        btn.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 0, 10, 0));
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