package ecommercesystem;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JLabel         lblStatus;

    public LoginFrame() {
        setTitle("E-Commerce System");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
            new EmptyBorder(36, 36, 36, 36)));
        card.setPreferredSize(new Dimension(350, 440));

        JPanel accent = new JPanel();
        accent.setBackground(new Color(52, 152, 219));
        accent.setPreferredSize(new Dimension(0, 4));
        accent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));

        JLabel lblTitle = new JLabel("Sign In");
        lblTitle.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Enter your credentials to continue");
        lblSub.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 12));
        lblSub.setForeground(new Color(149, 165, 166));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = UIUtils.createTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = UIUtils.createPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblU = fieldLabel("Username");
        JLabel lblP = fieldLabel("Password");

        // ── ROUNDED BUTTONS ──
        JButton btnLogin    = roundedBtn("Sign In",          new Color(52, 152, 219));
        JButton btnRegister = roundedBtn("Create an account", new Color(39, 174, 96));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font(UIUtils.EMOJI_FONT, Font.PLAIN, 11));
        lblStatus.setForeground(new Color(231, 76, 60));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> {
            dispose(); new RegisterFrame().setVisible(true);
        });
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        card.add(accent);
        card.add(Box.createVerticalStrut(24));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(lblSub);
        card.add(Box.createVerticalStrut(24));
        card.add(lblU);
        card.add(Box.createVerticalStrut(5));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(14));
        card.add(lblP);
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(20));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnRegister);
        card.add(Box.createVerticalStrut(10));
        card.add(lblStatus);

        root.add(card);
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter username and password!"); return;
        }
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role  = rs.getString("role");
                String email = rs.getString("email");
                int custId   = -1;
                if ("customer".equalsIgnoreCase(role) && email != null) {
                    try (PreparedStatement cs = con.prepareStatement(
                            "SELECT id FROM customers WHERE LOWER(email)=LOWER(?)")) {
                        cs.setString(1, email);
                        ResultSet cr = cs.executeQuery();
                        if (cr.next()) custId = cr.getInt(1);
                    } catch (SQLException ex) {}
                }
                dispose();
                new DashboardFrame(username, role, custId).setVisible(true);
            } else {
                lblStatus.setText("Invalid username or password!");
                txtPassword.setText("");
            }
        } catch (SQLException ex) {
            lblStatus.setText("DB Error: " + ex.getMessage());
        }
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font(UIUtils.EMOJI_FONT, Font.BOLD, 12));
        l.setForeground(new Color(44, 62, 80));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
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