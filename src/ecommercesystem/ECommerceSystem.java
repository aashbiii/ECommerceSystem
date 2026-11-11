package ecommercesystem;

import javax.swing.*;

public class ECommerceSystem {
    public static void main(String[] args) {
        try {
            // use Nimbus if available for a clean modern UI
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to system LF
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}