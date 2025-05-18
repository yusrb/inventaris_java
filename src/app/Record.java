/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.*;
import java.util.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author yusrb
 */
public class Record extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;

    public Record(String username) {
        initComponents();
        Connection();
        getSettings();

        usernameForPage = username;
        txtUsernameForPage.setText(usernameForPage);

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date tanggalAwal = cal.getTime();

        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date tanggalAkhir = cal.getTime();

        dtanggal_awal.setDate(tanggalAwal);
        dtanggal_akhir.setDate(tanggalAkhir);

        tblTampilBarangMasuk.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nama Produk", "Jumlah", "Tanggal Masuk", "Supplier", "Deskripsi"}
        ));

        tblTampilBarangKeluar.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Nama Produk", "Penerima", "Jumlah", "Tanggal Keluar", "Tgl Kembali", "Deskripsi", "Status"}
        ));

        btnTampil.addActionListener(e -> tampilkanData());

        tampilkanData();
    }

    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;

    public void Connection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/inventaris_java", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
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

    public void tampilkanData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tAwal = sdf.format(dtanggal_awal.getDate());
        String tAkhir = sdf.format(dtanggal_akhir.getDate());

        DefaultTableModel modelMasuk = (DefaultTableModel) tblTampilBarangMasuk.getModel();
        modelMasuk.setRowCount(0);

        try {
            pst = conn.prepareStatement(
                "SELECT si.id, p.nama, si.jumlah, si.tanggal_masuk, s.nama_supplier, si.deskripsi " +
                "FROM stock_in si " +
                "JOIN products p ON si.produk_id = p.id " +
                "JOIN suppliers s ON si.supplier_id = s.id " +
                "WHERE si.tanggal_masuk BETWEEN ? AND ?"
            );
            pst.setString(1, tAwal + " 00:00:00");
            pst.setString(2, tAkhir + " 23:59:59");
            rslt = pst.executeQuery();
            while (rslt.next()) {
                modelMasuk.addRow(new Object[] {
                    rslt.getInt("id"),
                    rslt.getString("nama"),
                    rslt.getInt("jumlah"),
                    rslt.getTimestamp("tanggal_masuk"),
                    rslt.getString("nama_supplier"),
                    rslt.getString("deskripsi")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Barang Masuk: " + ex.getMessage());
        }

        DefaultTableModel modelKeluar = (DefaultTableModel) tblTampilBarangKeluar.getModel();
        modelKeluar.setRowCount(0);

        try {
            pst = conn.prepareStatement(
                "SELECT so.id, p.nama ,so.penerima, so.jumlah, so.tanggal_keluar, so.tanggal_dikembalikan, so.deskripsi, so.status " +
                "FROM stock_out so " +
                "JOIN products p ON so.produk_id = p.id " +
                "WHERE so.tanggal_keluar BETWEEN ? AND ?"
            );
            pst.setString(1, tAwal + " 00:00:00");
            pst.setString(2, tAkhir + " 23:59:59");
            rslt = pst.executeQuery();
            while (rslt.next()) {
                modelKeluar.addRow(new Object[] {
                    rslt.getInt("id"),
                    rslt.getString("nama"),
                    rslt.getString("penerima"),
                    rslt.getInt("jumlah"),
                    rslt.getTimestamp("tanggal_keluar"),
                    rslt.getDate("tanggal_dikembalikan"),
                    rslt.getString("deskripsi"),
                    rslt.getString("status")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error Barang Keluar: " + ex.getMessage());
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
        jLabel2 = new javax.swing.JLabel();
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
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dtanggal_awal = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        dtanggal_akhir = new com.toedter.calendar.JDateChooser();
        btnCetak = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTampilBarangMasuk = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTampilBarangKeluar = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnTampil = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Record Page");

        jPanel1.setBackground(new java.awt.Color(123, 104, 238));

        jPanel2.setBackground(new java.awt.Color(147, 112, 219));

        jLabel1.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Hi");

        txtUsernameForPage.setFont(new java.awt.Font("Palatino Linotype", 1, 23)); // NOI18N
        txtUsernameForPage.setForeground(new java.awt.Color(255, 255, 255));
        txtUsernameForPage.setText("Username");

        jLabel2.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Adminstrator");

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
                    .addComponent(jLabel2))
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
                .addComponent(jLabel2)
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
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2),
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
        btnTransactions.setText("Transactions");
        btnTransactions.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
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
                .addContainerGap(76, Short.MAX_VALUE))
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

        txtNamePageTop.setFont(new java.awt.Font("Palatino Linotype", 1, 48)); // NOI18N
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Record");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(25, 25, 25))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel4.setText("-");

        btnCetak.setBackground(new java.awt.Color(0, 0, 0));
        btnCetak.setForeground(new java.awt.Color(255, 255, 255));
        btnCetak.setText("Cetak");
        btnCetak.setBorderPainted(false);
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        tblTampilBarangMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "produk", "jumlah", "tanggal masuk", "supplier", "deskripsi"
            }
        ));
        jScrollPane1.setViewportView(tblTampilBarangMasuk);

        tblTampilBarangKeluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "id", "produk", "penerima", "jumlah", "tanggal keluar", "tanggal kembali", "deskripsi", "status"
            }
        ));
        jScrollPane2.setViewportView(tblTampilBarangKeluar);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel5.setText("Barang Masuk");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel6.setText("Barang Keluar");

        btnTampil.setBackground(new java.awt.Color(51, 255, 0));
        btnTampil.setForeground(new java.awt.Color(255, 255, 255));
        btnTampil.setText("Tampil");
        btnTampil.setBorderPainted(false);

        btnClear.setBackground(new java.awt.Color(102, 102, 102));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Reset");
        btnClear.setBorderPainted(false);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
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
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(dtanggal_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dtanggal_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)
                                .addComponent(btnTampil)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(32, 32, 32))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(dtanggal_awal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dtanggal_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCetak)
                        .addComponent(btnTampil)
                        .addComponent(btnClear)))
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel6)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        
        Settings settings_page = new Settings(usernameForPage);
        settings_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // TODO add your handling code here:
        
        Dashboard dashboard_page = new Dashboard(usernameForPage);
        dashboard_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:
        
        Products products_page = new Products(usernameForPage);
        products_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        // TODO add your handling code here:
        
        Categories categories_page = new Categories(usernameForPage);
        categories_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnBrandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrandsActionPerformed
        // TODO add your handling code here:
        
        Brands brands_page = new Brands(usernameForPage);
        brands_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBrandsActionPerformed

    private void btnTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionsActionPerformed
        // TODO add your handling code here:
        
        Transactions transactions_page = new Transactions(usernameForPage);
        transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTransactionsActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        // TODO add your handling code here:
        
        Users users_page = new Users(usernameForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUsersActionPerformed

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        // TODO add your handling code here:
        
        Suppliers suppliers_page = new Suppliers(usernameForPage);
        suppliers_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        // TODO add your handling code here:
        
        Record record_page = new Record(usernameForPage);
        record_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRecordActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);

        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date tanggalAwal = cal.getTime();

        cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date tanggalAkhir = cal.getTime();

        dtanggal_awal.setDate(tanggalAwal);
        dtanggal_akhir.setDate(tanggalAkhir);
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String tAwal = sdf.format(dtanggal_awal.getDate());
        String tAkhir = sdf.format(dtanggal_akhir.getDate());

        String headerText = "LAPORAN DATA BARANG MASUK & KELUAR Periode: " + tAwal + " s/d " + tAkhir;

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Cetak Laporan Barang Masuk & Keluar");

        job.setPrintable(new Printable() {
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                double margin = 40;
                g2d.translate(pageFormat.getImageableX() + margin, pageFormat.getImageableY() + margin);

                int printableWidth = (int)(pageFormat.getImageableWidth() - 2 * margin);
                int y = 0;

                g2d.setFont(new Font("Serif", Font.BOLD, 14));
                g2d.drawString(headerText, 0, y + 15);
                y += 30;

                JTable table1 = tblTampilBarangMasuk;
                y += printTable(g2d, table1, y, printableWidth);

                y += 40;

                JTable table2 = tblTampilBarangKeluar;
                y += printTable(g2d, table2, y, printableWidth);

                return PAGE_EXISTS;
            }

            private int printTable(Graphics2D g2d, JTable table, int startY, int pageWidth) {
                TableModel model = table.getModel();
                Font font = new Font("Serif", Font.PLAIN, 9);
                g2d.setFont(font);

                int rowHeight = 18;
                int headerHeight = 22;
                int x = 0;
                int y = startY;

                TableColumnModel colModel = table.getColumnModel();
                int[] colWidths = new int[colModel.getColumnCount()];
                int totalWidth = 0;

                for (int i = 0; i < colModel.getColumnCount(); i++) {
                    colWidths[i] = colModel.getColumn(i).getWidth();
                    totalWidth += colWidths[i];
                }

                double scale = (double) pageWidth / totalWidth;
                for (int i = 0; i < colWidths.length; i++) {
                    colWidths[i] = (int) (colWidths[i] * scale * 1.14);
                }

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillRect(x, y, pageWidth, headerHeight);
                g2d.setColor(Color.BLACK);

                int colX = x;
                for (int i = 0; i < model.getColumnCount(); i++) {
                    String colName = model.getColumnName(i);
                    g2d.drawRect(colX, y, colWidths[i], headerHeight);
                    g2d.drawString(colName, colX + 3, y + 16);
                    colX += colWidths[i];
                }

                y += headerHeight;

                for (int row = 0; row < model.getRowCount(); row++) {
                    colX = x;
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        String text = (value == null) ? "" : value.toString();
                        g2d.drawRect(colX, y, colWidths[col], rowHeight);
                        g2d.drawString(text, colX + 3, y + 14);
                        colX += colWidths[col];
                    }
                    y += rowHeight;
                }

                return y - startY;
            }
        });

        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Error saat mencetak: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnCetakActionPerformed

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
            java.util.logging.Logger.getLogger(Record.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Record.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Record.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Record.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Record("Username").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTampil;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
    private com.toedter.calendar.JDateChooser dtanggal_akhir;
    private com.toedter.calendar.JDateChooser dtanggal_awal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JTable tblTampilBarangKeluar;
    private javax.swing.JTable tblTampilBarangMasuk;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
