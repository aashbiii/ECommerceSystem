package ecommercesystem;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class UIUtils {

    public static final String EMOJI_FONT = "Segoe UI Emoji";

    public static final Color COLOR_PRIMARY    = new Color(44, 62, 80);
    public static final Color COLOR_ACCENT     = new Color(52, 152, 219);
    public static final Color COLOR_SUCCESS    = new Color(39, 174, 96);
    public static final Color COLOR_WARNING    = new Color(230, 126, 34);
    public static final Color COLOR_DANGER     = new Color(231, 76, 60);
    public static final Color COLOR_BACKGROUND = new Color(245, 247, 250);
    public static final Color COLOR_WHITE      = Color.WHITE;
    public static final Color COLOR_TEXT       = new Color(44, 62, 80);
    public static final Color COLOR_SUBTLE     = new Color(149, 165, 166);
    public static final Color COLOR_NAV        = new Color(142, 68, 173);
    public static final Color COLOR_INFO       = new Color(52, 73, 94);

    public static final Font FONT_TITLE  = new Font(EMOJI_FONT, Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font(EMOJI_FONT, Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font(EMOJI_FONT, Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font(EMOJI_FONT, Font.PLAIN, 11);

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(EMOJI_FONT, Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);  // ← key line
        btn.setOpaque(false);             // ← key line
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.brighter());
                btn.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });
        return btn;
    }

    public static JButton roundedButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // draw text manually
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(getForeground());
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setFont(getFont());
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(EMOJI_FONT, Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.brighter()); btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg); btn.repaint();
            }
        });
        return btn;
    }
    
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(200, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(4, 8, 4, 8)));
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_BODY);
        field.setPreferredSize(new Dimension(200, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(4, 8, 4, 8)));
        return field;
    }

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 25, 20, 25)));
        return card;
    }

    public static JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font(EMOJI_FONT, Font.BOLD, 18));
        header.add(lbl, BorderLayout.WEST);
        return header;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(52, 152, 219, 80));
        table.setSelectionForeground(COLOR_TEXT);
        table.getTableHeader().setFont(new Font(EMOJI_FONT, Font.BOLD, 13));
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        final int[] hoverRow = {-1};
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r != hoverRow[0]) { hoverRow[0] = r; table.repaint(); }
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, val, sel, foc, r, c);
                setFont(new Font(EMOJI_FONT, Font.PLAIN, 13));
                if (!sel) {
                    if (r == hoverRow[0])
                        comp.setBackground(new Color(230, 240, 250));
                    else
                        comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return comp;
            }
        });
    }

    public static Color statusColor(String status) {
        switch (status != null ? status.toLowerCase() : "") {
            case "delivered":  return new Color(212, 255, 212);
            case "cancelled":  return new Color(255, 212, 212);
            case "shipped":    return new Color(212, 235, 255);
            case "processing": return new Color(255, 245, 200);
            default:           return COLOR_WHITE;
        }
    }
}