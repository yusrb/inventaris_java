/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author yusrb
 */
public class UsersForm extends javax.swing.JFrame {

    /**
     * Creates new form UsersForm
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    private Users users_page;
    
    private String mode;
    private int userId;
    private java.util.Map<String, String> levelMap = new java.util.HashMap<>();
    
    public UsersForm(Users users, String username, String level) {
        initComponents();
        conn = DBConnection.getConnection();
        loadLevel();
        
        users_page = users;
        usernameForPage = username;
        levelForPage = level;
        this.mode = "create";
        
        txtUsername.requestFocus();
        
        btnAction.setText("Create");
        btnAction.setBackground(Color.GREEN);
        
        setLocationRelativeTo(null);
    }
    
    public UsersForm(Users users , String username, String level, int userId, String usernameLama)
    {
        this(users, username, level);
        
        this.mode = "update";
        this.userId = userId;
        txtUsername.setText(usernameLama);
        
        txtUsername.requestFocus();
        
        btnAction.setText("Update");
        btnAction.setBackground(Color.BLUE);
    }
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;
 
    private void loadLevel() {
        try {
            pst = conn.prepareStatement("SHOW COLUMNS FROM users LIKE 'level'");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                String type = rslt.getString("Type");
                type = type.replace("enum(", "").replace(")", "").replace("'", "");
                String[] levels = type.split(",");

                cmbLevel.removeAllItems();
                levelMap.clear();

                for (String level : levels) {
                    cmbLevel.addItem(level);
                    levelMap.put(level, level);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        btnAction = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnBackToUsersList = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        cmbLevel = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Users Form Page");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel1.setText("Username");

        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtUsernameKeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(123, 104, 238));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 42)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Users Form");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(205, 205, 205))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel3.setText("Password");

        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel4.setText("Confirm Password");

        txtConfirmPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtConfirmPasswordKeyPressed(evt);
            }
        });

        btnAction.setForeground(new java.awt.Color(255, 255, 255));
        btnAction.setText("Action");
        btnAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActionActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(102, 102, 102));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnBackToUsersList.setBackground(new java.awt.Color(123, 104, 238));
        btnBackToUsersList.setForeground(new java.awt.Color(255, 255, 255));
        btnBackToUsersList.setText("Back");
        btnBackToUsersList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackToUsersListActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel5.setText("Level");

        cmbLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLevelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(55, 55, 55)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(cmbLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel3)
                            .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClear, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBackToUsersList, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAction, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE))
                        .addGap(29, 29, 29))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAction, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBackToUsersList, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackToUsersListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackToUsersListActionPerformed
        // TODO add your handling code here:
        
        this.dispose();
    }//GEN-LAST:event_btnBackToUsersListActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        
        txtUsername.requestFocus();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActionActionPerformed
        // TODO add your handling code here:
        
        String username = txtUsername.getText();
        
        char[] passwordStr = txtPassword.getPassword();
        String password = new String(passwordStr);
        Arrays.fill(passwordStr, ' ');
        
        char[] confirmPasswordStr = txtConfirmPassword.getPassword();
        String confirmPassword = new String(confirmPasswordStr);
        Arrays.fill(confirmPasswordStr, ' ');
        
        String levelTerpilih = (String) cmbLevel.getSelectedItem();
         String level = levelMap.getOrDefault(levelTerpilih, "");
        
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Seluruh Input Harus Diisi!!!", "Input Kosong", JOptionPane.WARNING_MESSAGE);
            
            txtUsername.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            
            txtUsername.requestFocus();
        }
        else if (!password.equals(confirmPassword))
        {
             JOptionPane.showMessageDialog(this, "Input Password dan Confirm Password Harus Sama!!!", "Input Confirm Password tidak sesuai", JOptionPane.WARNING_MESSAGE);

            txtPassword.setText("");
            txtConfirmPassword.setText("");
            
            txtPassword.requestFocus();
        }
        else
        {
           try {
                if (mode.equals("create"))
                {
                     pst = conn.prepareStatement("INSERT INTO users(username, password, level) VALUES (?, ?, ?)");
                     pst.setString(1, username);
                     pst.setString(2, password);
                     pst.setString(3, level);
                        
                     int k = pst.executeUpdate();
                        
                     if (k == 1)
                     {
                         JOptionPane.showMessageDialog(this, "User Baru Berhasil Ditambahkan!!!", "Tambah User Berhasil", JOptionPane.INFORMATION_MESSAGE);
                     }
                     else
                     {
                         JOptionPane.showMessageDialog(this, "User Baru Gagal Ditambahkan!!!", "Tambah User Gagal", JOptionPane.WARNING_MESSAGE);   
                     }
                }
                else if (mode.equals("update"))
                {
                     pst = conn.prepareStatement("UPDATE users SET username = ?, password = ?, level = ? WHERE id = ?");
                     pst.setString(1, username);
                     pst.setString(2, password);
                     pst.setString(3, level);
                     pst.setInt(4, userId);
                     
                     int k = pst.executeUpdate();
                     
                     if (k == 1)
                     {
                         JOptionPane.showMessageDialog(this, "User Berhasil DiUpdate!!!", "Update User Berhasil", JOptionPane.INFORMATION_MESSAGE);
                     }
                     else
                     {
                         JOptionPane.showMessageDialog(this, "User Gagal DiUpdate!!!", "Update User Gagal", JOptionPane.WARNING_MESSAGE);   
                     }
                }
                
                if (users_page != null)
                {
                    users_page.Fetch();
                }
                
                this.dispose();
           }
           catch (SQLException ex) {
                Logger.getLogger(UsersForm.class.getName()).log(Level.SEVERE, null, ex);
           }
        }
    }//GEN-LAST:event_btnActionActionPerformed

    private void txtUsernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
        {
            cmbLevel.showPopup();
        }
    }//GEN-LAST:event_txtUsernameKeyPressed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
        {
            txtConfirmPassword.requestFocus();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void txtConfirmPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConfirmPasswordKeyPressed
        // TODO add your handling code here:
        
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
        {
            btnAction.doClick();
        }
    }//GEN-LAST:event_txtConfirmPasswordKeyPressed

    private void cmbLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLevelActionPerformed
        // TODO add your handling code here:
        
        txtPassword.requestFocus();
    }//GEN-LAST:event_cmbLevelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UsersForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UsersForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UsersForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UsersForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new UsersForm(null, "", "").setVisible(true);
        }
    });
 }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAction;
    private javax.swing.JButton btnBackToUsersList;
    private javax.swing.JButton btnClear;
    private javax.swing.JComboBox<String> cmbLevel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
