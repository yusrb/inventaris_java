/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.awt.Image;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author yusrb
 */
public class BarangKeluar extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    
    public BarangKeluar(String username, String level) {
        initComponents();
        
        javax.swing.table.JTableHeader header = tblTampilBarangKeluar.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(new java.awt.Color(123, 104, 238));
                comp.setForeground(java.awt.Color.WHITE);
                comp.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                return comp;
            }
        });

        Connection();
        Fetch();
        setAlertStock();
        
        getSettings();
        
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
    
    public void Fetch() {
        try {
            pst = conn.prepareStatement(
                "SELECT " +
                "so.id, " +
                "p.nama AS nama_produk, " +
                "so.jumlah, " +
                "so.tanggal_keluar, " +
                "so.tanggal_dikembalikan, " +
                "so.penerima, " +
                "so.status, " +
                "so.deskripsi " +
                "FROM stock_out so " +
                "JOIN products p ON so.produk_id = p.id"
            );

            rslt = pst.executeQuery();

            DefaultTableModel df = (DefaultTableModel) tblTampilBarangKeluar.getModel();
            df.setRowCount(0);

            while (rslt.next()) {
                Vector v2 = new Vector();
                v2.add(rslt.getInt("id"));
                v2.add(rslt.getString("nama_produk"));
                v2.add(rslt.getString("penerima"));
                v2.add(rslt.getString("deskripsi"));
                v2.add(rslt.getInt("jumlah"));
                v2.add(rslt.getString("status"));
                v2.add(rslt.getTimestamp("tanggal_keluar"));
                v2.add(rslt.getTimestamp("tanggal_dikembalikan"));
                df.addRow(v2);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BarangKeluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setAlertStock()
    {
        try {
            pst = conn.prepareStatement("SELECT stok, stok_alert FROM products WHERE stok < stok_alert");
            
            rslt = pst.executeQuery();
            
            while (rslt.next())
            {
                btnNotifikasi.setText("Stok Barang < Stok Alert");
                btnNotifikasi.setBackground(Color.RED);
                btnNotifikasi.setForeground(Color.WHITE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BarangKeluar.class.getName()).log(Level.SEVERE, null, ex);
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
        txtNamePageTop = new javax.swing.JLabel();
        labelLogo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTampilBarangKeluar = new javax.swing.JTable();
        btnCetak = new javax.swing.JButton();
        btnNotifikasi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Barang Keluar Page");

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
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 1), 1),
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
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        txtNamePageTop.setFont(new java.awt.Font("Palatino Linotype", 1, 43)); // NOI18N
        txtNamePageTop.setForeground(new java.awt.Color(255, 255, 255));
        txtNamePageTop.setText("namePage");

        labelLogo.setText("logo");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(labelLogo)
                .addGap(20, 20, 20)
                .addComponent(txtNamePageTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1062, Short.MAX_VALUE)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
            .addComponent(btnSettings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNamePageTop)
                    .addComponent(labelLogo))
                .addGap(18, 18, 18))
        );

        jLabel3.setFont(new java.awt.Font("Palatino Linotype", 1, 18)); // NOI18N
        jLabel3.setText("Stock Out List");

        btnCreate.setBackground(new java.awt.Color(0, 255, 0));
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(102, 102, 255));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.setBorder(null);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 0, 51));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.setBorder(null);
        btnDelete.setBorderPainted(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tblTampilBarangKeluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "nama barang", "penerima", "deskripsi", "jumlah", "status", "tanggal_keluar", "tanggal_dikembalikan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblTampilBarangKeluar);

        btnCetak.setBackground(new java.awt.Color(153, 153, 153));
        btnCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnCetak.setText("Cetak");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        btnNotifikasi.setText("Notifkasi 0");
        btnNotifikasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNotifikasiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1180, 1180, 1180)
                                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnNotifikasi))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1486, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(38, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btnNotifikasi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 446, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
        
        BarangKeluar users_page = new BarangKeluar(usernameForPage, levelForPage);
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

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        
        BarangKeluarForm barang_keluar_form = new BarangKeluarForm(usernameForPage, levelForPage);
        barang_keluar_form.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling cuaode here:
        
            int pilihBaris = tblTampilBarangKeluar.getSelectedRow();

            if (pilihBaris == -1) {
                    JOptionPane.showMessageDialog(this, "Barang Keluar Tidak Ditemukan\nPilih Data Dulu", "Data Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

            int barangKeluarId = (int) tblTampilBarangKeluar.getValueAt(pilihBaris, 0);
            String namaProduk = (String) tblTampilBarangKeluar.getValueAt(pilihBaris, 1);
            String penerima = (String) tblTampilBarangKeluar.getValueAt(pilihBaris, 2);
            String deskripsi = (String) tblTampilBarangKeluar.getValueAt(pilihBaris, 3);
            int jumlah = (int) tblTampilBarangKeluar.getValueAt(pilihBaris, 4);
            String status = (String) tblTampilBarangKeluar.getValueAt(pilihBaris, 5);
            Timestamp tanggalKeluar = (Timestamp) tblTampilBarangKeluar.getValueAt(pilihBaris, 6);

            BarangKeluarForm form = new BarangKeluarForm(usernameForPage, levelForPage, barangKeluarId, namaProduk, jumlah, tanggalKeluar, penerima, status, deskripsi);
            form.setVisible(true);
            this.dispose();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            int pilihBaris = tblTampilBarangKeluar.getSelectedRow();

            if (pilihBaris == -1) {
                JOptionPane.showMessageDialog(this, "Barang Keluar Tidak Ditemukan, Pilih Data Dulu!!!", "Data Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int barangKeluarId = (int) tblTampilBarangKeluar.getValueAt(pilihBaris, 0);
            String namaProduk = (String) tblTampilBarangKeluar.getValueAt(pilihBaris, 1);

            int konfirmasiHapus = JOptionPane.showConfirmDialog(this, "Yakin Ingin Menghapus Data Barang Keluar untuk Produk " + namaProduk + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (konfirmasiHapus == JOptionPane.YES_OPTION) {
                pst = conn.prepareStatement("DELETE FROM stock_out WHERE id = ?");
                pst.setInt(1, barangKeluarId);

                int hasil = pst.executeUpdate();

                if (hasil == 1) {
                    JOptionPane.showMessageDialog(this, "Berhasil Menghapus Data Barang Keluar!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    Fetch();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal Menghapus Data Barang Keluar!", "Gagal", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(BarangKeluar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
        
        try {
            MessageFormat header = new MessageFormat("LAPORAN DATA BARANG MASUK");

            MessageFormat footer = new MessageFormat("Halaman {0}");

            boolean complete = tblTampilBarangKeluar.print(
                JTable.PrintMode.FIT_WIDTH,
                header,
                footer
            );

            if (complete) {
                JOptionPane.showMessageDialog(this, "Pencetakan berhasil dilakukan.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan oleh pengguna.", "Print Cancelled", JOptionPane.WARNING_MESSAGE);
            }

        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencetak:\n" + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnNotifikasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotifikasiActionPerformed
        // TODO add your handling code here:
        
        Notications notifikasi_page = new Notications(usernameForPage);
        notifikasi_page.setVisible(true);
    }//GEN-LAST:event_btnNotifikasiActionPerformed

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
            java.util.logging.Logger.getLogger(BarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BarangKeluar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BarangKeluar("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNotifikasi;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JTable tblTampilBarangKeluar;
    private javax.swing.JLabel txtLevelForPage;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
