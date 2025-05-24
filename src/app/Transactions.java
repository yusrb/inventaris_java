/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Image;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author yusrb
 */
public class Transactions extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    
    public Transactions(String username, String level) {
        initComponents();
        Connection();
        getSettings();
        getCountBarangMasuk();
        getCountBarangKeluar();
        getCountBarangDipinjam();
        getCountBarangDikembalikan();
        
        usernameForPage = username;
        levelForPage = level;
        txtUsernameForPage.setText(usernameForPage);
        txtLevelForPage.setText(levelForPage);
        
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;
    
    public void Connection()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/inventaris_java", "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getSettings()
    {
        try {
            pst = conn.prepareStatement("SELECT logo, name_application FROM settings LIMIT 1");
            
            rslt = pst.executeQuery();
            
            if (rslt.next())
            {
                txtNamePageTop.setText(rslt.getString("name_application"));
                txtNamePageBottom.setText(rslt.getString("name_application"));
                
                byte[] gambarBytes = rslt.getBytes("logo");
                if (gambarBytes != null) {
                    ImageIcon icon = new ImageIcon(gambarBytes);
                    Image img = icon.getImage().getScaledInstance(88, 88, Image.SCALE_SMOOTH);
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
    
    public void getCountBarangMasuk()
    {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_masuk FROM stock_in");
            rslt = pst.executeQuery();
            
            if (rslt.next())
            {
                txtCountBarangMasuk.setText(rslt.getString("total_barang_masuk"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Transactions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getCountBarangKeluar()
    {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_keluar FROM stock_out WHERE status = 'selesai'");
            rslt = pst.executeQuery();
            
            if (rslt.next())
            {
                txtCountBarangKeluar.setText(rslt.getString("total_barang_keluar"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Transactions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getCountBarangDipinjam()
    {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_dipinjam FROM stock_out WHERE status = 'dipinjam'");
            rslt = pst.executeQuery();
            
            if (rslt.next())
            {
                txtCountBarangDipinjam.setText(rslt.getString("total_barang_dipinjam"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Transactions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        public void getCountBarangDikembalikan()
    {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_dikembalikan FROM stock_out WHERE status = 'selesai'");
            rslt = pst.executeQuery();
            
            if (rslt.next())
            {
                txtCountBarangDikembalikan.setText(rslt.getString("total_barang_dikembalikan"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Transactions.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUsernameForPage = new javax.swing.JLabel();
        txtLevelForPage = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnBrands = new javax.swing.JButton();
        btnRecord = new javax.swing.JButton();
        btnTransactions = new javax.swing.JButton();
        btnUsers = new javax.swing.JButton();
        btnSuppliers = new javax.swing.JButton();
        txtNamePageBottom = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        labelLogo = new javax.swing.JLabel();
        txtNamePageTop = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnBarangMasuk = new javax.swing.JButton();
        btnBarangKeluar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtCountBarangMasuk = new javax.swing.JLabel();
        txtCountBarangKeluar = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtCountBarangDipinjam = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtCountBarangDikembalikan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Transactions Page");

        jPanel1.setBackground(new java.awt.Color(123, 104, 238));

        jPanel2.setBackground(new java.awt.Color(147, 112, 219));

        jLabel1.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Hi");

        txtUsernameForPage.setFont(new java.awt.Font("Palatino Linotype", 1, 23)); // NOI18N
        txtUsernameForPage.setForeground(new java.awt.Color(255, 255, 255));
        txtUsernameForPage.setText("Username");

        txtLevelForPage.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        txtLevelForPage.setForeground(new java.awt.Color(255, 255, 255));
        txtLevelForPage.setText("Adminstrator");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsernameForPage))
                    .addComponent(txtLevelForPage))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUsernameForPage))
                .addGap(18, 18, 18)
                .addComponent(txtLevelForPage)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        btnDashboard.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("Dashboard");
        btnDashboard.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });

        btnProducts.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnProducts.setText("Products");
        btnProducts.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnProducts.setContentAreaFilled(false);
        btnProducts.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnCategories.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnCategories.setForeground(new java.awt.Color(255, 255, 255));
        btnCategories.setText("Categories");
        btnCategories.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnCategories.setContentAreaFilled(false);
        btnCategories.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriesActionPerformed(evt);
            }
        });

        btnBrands.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnBrands.setForeground(new java.awt.Color(255, 255, 255));
        btnBrands.setText("Brands");
        btnBrands.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnBrands.setContentAreaFilled(false);
        btnBrands.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrandsActionPerformed(evt);
            }
        });

        btnRecord.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnRecord.setForeground(new java.awt.Color(255, 255, 255));
        btnRecord.setText("Record");
        btnRecord.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnRecord.setContentAreaFilled(false);
        btnRecord.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordActionPerformed(evt);
            }
        });

        btnTransactions.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnTransactions.setForeground(new java.awt.Color(255, 255, 255));
        btnTransactions.setText("Stock Transactions");
        btnTransactions.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnTransactions.setContentAreaFilled(false);
        btnTransactions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTransactions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionsActionPerformed(evt);
            }
        });

        btnUsers.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnUsers.setForeground(new java.awt.Color(255, 255, 255));
        btnUsers.setText("Users");
        btnUsers.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnUsers.setContentAreaFilled(false);
        btnUsers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsersActionPerformed(evt);
            }
        });

        btnSuppliers.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        btnSuppliers.setText("Suppliers");
        btnSuppliers.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnSuppliers.setContentAreaFilled(false);
        btnSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuppliersActionPerformed(evt);
            }
        });

        txtNamePageBottom.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        txtNamePageBottom.setForeground(new java.awt.Color(255, 255, 255));
        txtNamePageBottom.setText("namePage");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnBrands, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnTransactions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(txtNamePageBottom)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnBrands, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(txtNamePageBottom)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnBrands, btnCategories, btnDashboard, btnProducts, btnRecord, btnSuppliers, btnTransactions, btnUsers});

        jPanel3.setBackground(new java.awt.Color(123, 104, 238));

        btnLogout.setBackground(new java.awt.Color(255, 51, 51));
        btnLogout.setForeground(new java.awt.Color(255, 51, 51));
        btnLogout.setText("Logout");
        btnLogout.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51), 2));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnSettings.setBackground(new java.awt.Color(51, 51, 255));
        btnSettings.setForeground(new java.awt.Color(255, 255, 255));
        btnSettings.setText("Settings");
        btnSettings.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        btnSettings.setContentAreaFilled(false);
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });

        labelLogo.setText("logo");

        txtNamePageTop.setFont(new java.awt.Font("Palatino Linotype", 1, 40)); // NOI18N
        txtNamePageTop.setForeground(new java.awt.Color(255, 255, 255));
        txtNamePageTop.setText("namePage");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(labelLogo)
                .addGap(20, 20, 20)
                .addComponent(txtNamePageTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSettings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
            .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNamePageTop)
                    .addComponent(labelLogo))
                .addGap(18, 18, 18))
        );

        jPanel4.setBackground(new java.awt.Color(123, 104, 238));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 40)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Pilihan Transaksi");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(26, 26, 26))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jLabel4.setText("Total Barang Masuk");

        btnBarangMasuk.setBackground(new java.awt.Color(123, 104, 238));
        btnBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        btnBarangMasuk.setText("Stock In");
        btnBarangMasuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarangMasukActionPerformed(evt);
            }
        });

        btnBarangKeluar.setBackground(new java.awt.Color(123, 104, 238));
        btnBarangKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnBarangKeluar.setText("Stock Out");
        btnBarangKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarangKeluarActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 13, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jLabel5.setText("Total Barang Keluar");

        txtCountBarangMasuk.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        txtCountBarangMasuk.setText("total_barang_masuk");

        txtCountBarangKeluar.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        txtCountBarangKeluar.setText("total_barang_keluar");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jLabel8.setText("Total Barang Dipinjam");

        txtCountBarangDipinjam.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        txtCountBarangDipinjam.setText("total_barang_dipinjam");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
        jLabel9.setText("Total Barang Dikembalikan");

        txtCountBarangDikembalikan.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        txtCountBarangDikembalikan.setText("total_barang_dikembalikan");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBarangMasuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnBarangKeluar, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                                .addGap(47, 47, 47)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel9)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(81, 81, 81)
                                                .addComponent(txtCountBarangDikembalikan))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(109, 109, 109)
                                        .addComponent(txtCountBarangKeluar))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(111, 111, 111)
                                        .addComponent(txtCountBarangDipinjam))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(105, 105, 105)
                                        .addComponent(txtCountBarangMasuk)))
                                .addGap(29, 29, 29)))
                        .addGap(32, 32, 32))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(btnBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(txtCountBarangMasuk)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtCountBarangKeluar)
                                .addGap(34, 34, 34)
                                .addComponent(jLabel8)))
                        .addGap(18, 18, 18)
                        .addComponent(txtCountBarangDipinjam)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(txtCountBarangDikembalikan)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        
        int pilihan = JOptionPane.showConfirmDialog(this, "Yakin Ingin Logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        
        if (pilihan == JOptionPane.YES_OPTION)
        {
            JOptionPane.showMessageDialog(this, "Anda Berhasil Logout\nSilahkan Login Kembali", "Logout Berhasil",  JOptionPane.INFORMATION_MESSAGE);
            Login login_page = new Login();
            login_page.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        // TODO add your handling code here:
        
        Settings settings_page = new Settings(usernameForPage, levelForPage);
        settings_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // TODO add your handling code here:
        
        Dashboard dashboard_page = new Dashboard(usernameForPage, levelForPage);
        dashboard_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:
        
        Products products_page = new Products(usernameForPage, levelForPage);
        products_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        // TODO add your handling code here:
        
        Categories categories_page = new Categories(usernameForPage, levelForPage);
        categories_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnBrandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrandsActionPerformed
        // TODO add your handling code here:
        
        Brands brands_page = new Brands(usernameForPage, levelForPage);
        brands_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBrandsActionPerformed

    private void btnTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionsActionPerformed
        // TODO add your handling code here:
        
        Transactions transactions_page = new Transactions(usernameForPage, levelForPage);
        transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTransactionsActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        // TODO add your handling code here:
        
        Users users_page = new Users(usernameForPage, levelForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUsersActionPerformed

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        // TODO add your handling code here:
        
        Suppliers suppliers_page = new Suppliers(usernameForPage, levelForPage);
        suppliers_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        // TODO add your handling code here:
        
        Record record_page = new Record(usernameForPage, levelForPage);
        record_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRecordActionPerformed

    private void btnBarangMasukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarangMasukActionPerformed
        // TODO add your handling code here:
        
        BarangMasuk barang_masuk_page = new BarangMasuk(usernameForPage, levelForPage);
        barang_masuk_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBarangMasukActionPerformed

    private void btnBarangKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarangKeluarActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBarangKeluarActionPerformed

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
            java.util.logging.Logger.getLogger(Transactions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Transactions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Transactions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Transactions.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transactions("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBarangKeluar;
    private javax.swing.JButton btnBarangMasuk;
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel txtCountBarangDikembalikan;
    private javax.swing.JLabel txtCountBarangDipinjam;
    private javax.swing.JLabel txtCountBarangKeluar;
    private javax.swing.JLabel txtCountBarangMasuk;
    private javax.swing.JLabel txtLevelForPage;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
