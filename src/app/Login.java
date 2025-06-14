/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.UIManager;

/**
 *
 * @author yusrb
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        setUndecorated(true);
        initComponents();

        conn = DBConnection.getConnection();
        getSettings();
        
        txtUsername.requestFocus();
        
        setLocationRelativeTo(null);
        setOpacity(0f);
        typingEffect(jLabel4, "Hello ! Welcome Back", 80);
        fadeIn();
    }
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;
    
    public void getSettings() {
        try {
            pst = conn.prepareStatement("SELECT name_application, logo FROM settings WHERE id = 1");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                txtNamaAplikasi.setText(rslt.getString("name_application"));
                
                byte[] gambarBytes = rslt.getBytes("logo");
                
                if (gambarBytes != null) {
                    
                    ImageIcon icon = new ImageIcon(gambarBytes);
                    Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    
                    labelLogo.setText("");
                    labelLogo.setIcon(new ImageIcon(img));
                    
                    labelLogo.revalidate();
                    labelLogo.repaint();

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fadeIn() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            float opacity = 0f;
            @Override
            public void run() {
                opacity += 0.05f;
                if (opacity > 1f) {
                    opacity = 1f;
                    timer.cancel();
                }
                setOpacity(opacity);
            }
        }, 0, 50);
    }

    private void typingEffect(javax.swing.JLabel label, String text, int delay) {
        final javax.swing.Timer timer = new javax.swing.Timer(delay, null);
        timer.addActionListener(new java.awt.event.ActionListener() {
            int index = 0;
            
            @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (index <= text.length()) {
                        label.setText(text.substring(0, index));
                        index++;
                    } else {
                        timer.stop();
                    }
                }
            });
            timer.start();
        }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnClear = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtNamaAplikasi = new javax.swing.JLabel();
        iconViewAesthe = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        labelLogo = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login Page");
        setBackground(new java.awt.Color(229, 231, 235));

        jLabel2.setFont(new java.awt.Font("Palatino Linotype", 0, 18)); // NOI18N
        jLabel2.setText("Username");

        jLabel3.setFont(new java.awt.Font("Palatino Linotype", 0, 18)); // NOI18N
        jLabel3.setText("Password");

        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUsernameKeyPressed(evt);
            }
        });

        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.setBorder(null);
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClearMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClearMouseExited(evt);
            }
        });
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnLogin.setText("Login");
        btnLogin.setBorder(null);
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLoginMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLoginMouseExited(evt);
            }
        });
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(123, 104, 238));
        jPanel2.setForeground(new java.awt.Color(0, 97, 238));

        txtNamaAplikasi.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        txtNamaAplikasi.setForeground(new java.awt.Color(255, 255, 255));
        txtNamaAplikasi.setText("namaAplikasi");

        iconViewAesthe.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\pos_java\\images\\duce.png")); // NOI18N

        jPanel1.setBackground(new java.awt.Color(123, 100, 250));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(txtNamaAplikasi)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(iconViewAesthe, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 30, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(txtNamaAplikasi)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(iconViewAesthe, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel4.setFont(new java.awt.Font("Reem Kufi", 1, 18)); // NOI18N
        jLabel4.setText(" Welcome Back!");
        jLabel4.setToolTipText("");

        labelLogo.setText("logo");

        btnClose.setBackground(new java.awt.Color(255, 0, 0));
        btnClose.setForeground(new java.awt.Color(255, 255, 255));
        btnClose.setText("X");
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPassword)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnClose)
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(labelLogo))
                            .addComponent(jLabel4)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(btnClose)
                .addGap(10, 10, 10)
                .addComponent(labelLogo)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 166, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:

        final java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            float opacity = 1f;

            @Override
            public void run() {
                opacity -= 0.05f;
                if (opacity <= 0f) {
                    opacity = 0f;
                    timer.cancel();
                    System.exit(0);
                }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    setOpacity(opacity);
                });
            }
        }, 0, 50);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        try {
            // TODO add your handling code here:

            String username = txtUsername.getText();

            char[] passwordStr = txtPassword.getPassword();
            String password = new String(passwordStr);
            Arrays.fill(passwordStr, ' ');

            if (username.isEmpty() || password.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Seluruh Input Harus Diisi!!!", "Input Empty", JOptionPane.WARNING_MESSAGE);
                return;
            }

            else
            {
                pst = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                pst.setString(1, username);
                pst.setString(2, password);

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    String level = rslt.getString("level");
                    JOptionPane.showMessageDialog(this, "Login Berhasil, Diarahkan ke Dashboard", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                    Dashboard dashboard_page = new Dashboard(username, level);
                    dashboard_page.setVisible(true);
                    this.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Data Tidak Ditemukan \nSilahkan cek kembali data anda!!!", "Login Failed", JOptionPane.WARNING_MESSAGE);
                    txtUsername.requestFocus();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnLoginMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseExited
        // TODO add your handling code here:

        btnLogin.setBackground(new Color(240,240,240));
        btnLogin.setForeground(Color.BLACK);
    }//GEN-LAST:event_btnLoginMouseExited

    private void btnLoginMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnLoginMouseEntered
        // TODO add your handling code here:

        btnLogin.setBackground(new Color(147,112,219));
        btnLogin.setForeground(Color.WHITE);
    }//GEN-LAST:event_btnLoginMouseEntered

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:

        txtUsername.setText("");
        txtPassword.setText("");

        txtUsername.requestFocus();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnClearMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseExited
        // TODO add your handling code here:

        btnClear.setBackground(new Color(240,240,240));
        btnClear.setForeground(Color.BLACK);
    }//GEN-LAST:event_btnClearMouseExited

    private void btnClearMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseEntered
        // TODO add your handling code here:

        btnClear.setBackground(new Color(147,112,219));
        btnClear.setForeground(Color.WHITE);
    }//GEN-LAST:event_btnClearMouseEntered

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnLogin.doClick();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void txtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtPassword.requestFocus();
        }
    }//GEN-LAST:event_txtUsernameKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        
        try {
                if (!DBConnection.isDatabaseOnline()) {
                    JOptionPane.showMessageDialog(null, 
                        "MySQL belum aktif.\nSilakan hidupkan XAMPP atau Laragon terlebih dahulu.", 
                        "Database Tidak Tersambung", 
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            } catch (SQLException e) {
                if (e.getErrorCode() == 1049) {
                    int opsi = JOptionPane.showConfirmDialog(null, 
                        "Database 'pos_java' tidak ditemukan.\nMau unduh file SQL untuk import?", 
                        "Database Tidak Ditemukan",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                    if (opsi == JOptionPane.YES_OPTION) {
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(
                                "https://drive.google.com/drive/u/0/folders/1PGvk6vS3pdiZUMKKOCYvGJx5zW-Hu7rN"));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Gagal membuka browser: " + ex.getMessage());
                        }
                    }
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Gagal koneksi ke database:\n" + e.getMessage(), 
                        "Kesalahan Koneksi", 
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel iconViewAesthe;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel txtNamaAplikasi;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
