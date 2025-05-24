/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Image;
import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author yusrb
 */
public class Dashboard extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    
    public Dashboard(String username, String level) {
        initComponents();
        Connection();
        getSettings();
        setTotalProducts();
        setTotalUsers();
        setTotalSuppliers();
        setTotalTransaksiPeminjaman();
        setTotalBarangMasuk();
        setTotalBarangKeluar();
        setTotalTransaksiBarangMasuk();
        setTotalTransaksiBarangKeluar();
        setTotalPeminjamanDikembalikan();
        setTotalPeminjamanBelumDikembalikan();
        setImageToDashboard();
//        
        usernameForPage = username;
        levelForPage = level;
        txtUsernameForPage.setText(usernameForPage);
        txtRoleForPage.setText(levelForPage);
        
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
    
    public void setImageToDashboard() {
        setImageToLabel("images/product.png", labelProductImage);
        setImageToLabel("images/users.png", labelUsersImage);
                setImageToLabel("images/suppliers.png", labelSuppliersImage);
                setImageToLabel("images/peminjaman.png", labelTPeminjaman);
                setImageToLabel("images/barang_masuk.png", labelBarangMasuk);
                setImageToLabel("images/barang_keluar.png", labelBarangKeluar);
                setImageToLabel("images/transaksi_barang_masuk.png", labelTransaksiMasukImage);
                setImageToLabel("images/transaksi_barang_keluar.png", labelTransaksiKeluarImage);
                setImageToLabel("images/peminjaman_dikembalikan.png", labelPeminjamanDikembalikanImage);
                setImageToLabel("images/peminjaman_belum_dikembalikan.png", labelPeminjamanBelumDikembalikanImage);

         setupDynamicScaling();
    }
    
    public void setupDynamicScaling() {
        addDynamicScaling(labelProductImage);
        addDynamicScaling(labelUsersImage);
        addDynamicScaling(labelSuppliersImage);
        addDynamicScaling(labelTPeminjaman);
        addDynamicScaling(labelBarangMasuk);
        //        addDynamicScaling(labelBarangKeluarImage);
        //        addDynamicScaling(labelTransaksiMasukImage);
        //        addDynamicScaling(labelTransaksiKeluarImage);
        //        addDynamicScaling(labelKembaliImage);
        //        addDynamicScaling(labelBelumKembaliImage);
    }

    private void addDynamicScaling(JLabel label) {
        ImageIcon originalIcon = (ImageIcon) label.getIcon();
        if (originalIcon == null) {
            label.setText("Gambar tidak ada");
            return;
        }

        label.putClientProperty("originalIcon", originalIcon);
        updateIconSize(label);

        label.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateIconSize(label);
            }
        });
    }

    private void updateIconSize(JLabel label) {
        ImageIcon originalIcon = (ImageIcon) label.getClientProperty("originalIcon");
        if (originalIcon == null) return;

        int width = label.getWidth() > 0 ? label.getWidth() : 150;
        int height = label.getHeight() > 0 ? label.getHeight() : 150;

        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();
        double ratio = Math.min((double) width / originalWidth, (double) height / originalHeight);
        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImage));
    }
    
    private void setImageToLabel(String imagePath, JLabel label) {
            File file = new File(imagePath);

            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

            label.setText("");
            label.setIcon(new ImageIcon(img));
        }

    public void setTotalProducts()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_products FROM products");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalProducts.setText(rslt.getString("total_products"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    public void setTotalUsers()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_users FROM users");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalUsers.setText(rslt.getString("total_users"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    public void setTotalSuppliers()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_suppliers FROM suppliers");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalSuppliers.setText(rslt.getString("total_suppliers"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    public void setTotalTransaksiPeminjaman()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_peminjaman FROM stock_out WHERE status = 'dipinjam' OR status = 'selesai dipinjam'");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalPeminjaman.setText(rslt.getString("total_peminjaman"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void setTotalBarangMasuk()
        {
            try {
                pst = conn.prepareStatement("SELECT SUM(jumlah) AS total_barang_masuk FROM stock_in");
                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    int total = rslt.getInt("total_barang_masuk");
                    txtTotalBarangMasuk.setText(String.valueOf(total));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                txtTotalBarangMasuk.setText("0");
            }
        }
    
    public void setTotalBarangKeluar()
        {
            try {
                pst = conn.prepareStatement("SELECT SUM(jumlah) AS total_barang_keluar FROM stock_out");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalBarangKeluar.setText(rslt.getString("total_barang_keluar"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                txtTotalBarangMasuk.setText("0");
            }
        }

    public void setTotalTransaksiBarangMasuk()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_masuk FROM stock_in");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalTransaksiBM.setText(rslt.getString("total_barang_masuk"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    public void setTotalTransaksiBarangKeluar()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_keluar FROM stock_out");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalTransactionsBK.setText(rslt.getString("total_barang_keluar"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    public void setTotalPeminjamanDikembalikan()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_peminjaman_dikembalikan FROM stock_out WHERE status = 'selesai dipinjam'");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalPeminjamanDikembalikan.setText(rslt.getString("total_peminjaman_dikembalikan"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    public void setTotalPeminjamanBelumDikembalikan()
        {
            try {
                pst = conn.prepareStatement("SELECT COUNT(*) AS total_peminjaman_belum_dikembalikan FROM stock_out WHERE status = 'dipinjam'");

                rslt = pst.executeQuery();

                if (rslt.next())
                {
                    txtTotalPeminjamanBelumDikembalikan.setText(rslt.getString("total_peminjaman_belum_dikembalikan"));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
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
        txtRoleForPage = new javax.swing.JLabel();
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
        txtNamePageTop = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        labelLogo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnToProducts = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        txtTotalProducts = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelProductImage = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtTotalUsers = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnToUsers = new javax.swing.JButton();
        labelUsersImage = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        txtTotalSuppliers = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnToSuppliers = new javax.swing.JButton();
        labelSuppliersImage = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        txtTotalPeminjaman = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnToTPeminjaman = new javax.swing.JButton();
        labelTPeminjaman = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtTotalBarangMasuk = new javax.swing.JLabel();
        btnToTotalBM = new javax.swing.JButton();
        labelBarangMasuk = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtTotalBarangKeluar = new javax.swing.JLabel();
        btnToTotalBK = new javax.swing.JButton();
        labelBarangKeluar = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtTotalTransaksiBM = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnToTransaksiBM = new javax.swing.JButton();
        labelTransaksiMasukImage = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalTransactionsBK = new javax.swing.JLabel();
        btnToTransaksiBK = new javax.swing.JButton();
        labelTransaksiKeluarImage = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        txtTotalPeminjamanDikembalikan = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnToPeminjamanDikembalikan = new javax.swing.JButton();
        labelPeminjamanDikembalikanImage = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        txtTotalPeminjamanBelumDikembalikan = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        btnToPeminjamanBelumDikembalikan = new javax.swing.JButton();
        labelPeminjamanBelumDikembalikanImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard Page");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(123, 104, 238));

        jPanel2.setBackground(new java.awt.Color(147, 112, 219));

        jLabel1.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Hi");

        txtUsernameForPage.setFont(new java.awt.Font("Palatino Linotype", 1, 23)); // NOI18N
        txtUsernameForPage.setForeground(new java.awt.Color(255, 255, 255));
        txtUsernameForPage.setText("Username");

        txtRoleForPage.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        txtRoleForPage.setForeground(new java.awt.Color(255, 255, 255));
        txtRoleForPage.setText("Role");

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
                    .addComponent(txtRoleForPage))
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
                .addComponent(txtRoleForPage)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        btnDashboard.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("Dashboard");
        btnDashboard.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2),
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
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        txtNamePageTop.setFont(new java.awt.Font("Palatino Linotype", 1, 43)); // NOI18N
        txtNamePageTop.setForeground(new java.awt.Color(255, 255, 255));
        txtNamePageTop.setText("namePage");

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(labelLogo)
                .addGap(20, 20, 20)
                .addComponent(txtNamePageTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1281, Short.MAX_VALUE)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSettings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtNamePageTop)
                    .addComponent(labelLogo))
                .addGap(18, 18, 18))
        );

        jPanel4.setBackground(new java.awt.Color(27, 164, 0));

        btnToProducts.setBackground(new java.awt.Color(20, 120, 0));
        btnToProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnToProducts.setText("Lihat Detail ->");
        btnToProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToProductsActionPerformed(evt);
            }
        });

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        txtTotalProducts.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalProducts.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalProducts.setText("1");

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Model Barang");

        labelProductImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\product.png")); // NOI18N
        labelProductImage.setText("product");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(234, 234, 234))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalProducts)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(labelProductImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalProducts)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelProductImage, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(btnToProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setText("Dashboard");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(204, 204, 204));
        jLabel4.setText("Control Panel");

        jPanel7.setBackground(new java.awt.Color(220, 130, 0));
        jPanel7.setPreferredSize(new java.awt.Dimension(371, 156));

        txtTotalUsers.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalUsers.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalUsers.setText("1");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Model Users");

        btnToUsers.setBackground(new java.awt.Color(220, 130, 0));
        btnToUsers.setForeground(new java.awt.Color(255, 255, 255));
        btnToUsers.setText("Lihat Detail ->");
        btnToUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToUsersActionPerformed(evt);
            }
        });

        labelUsersImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\users.png")); // NOI18N
        labelUsersImage.setText("product");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(txtTotalUsers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelUsersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(btnToUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(txtTotalUsers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(labelUsersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)))
                .addGap(16, 16, 16)
                .addComponent(btnToUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setBackground(new java.awt.Color(220, 20, 60));
        jPanel8.setPreferredSize(new java.awt.Dimension(371, 156));

        txtTotalSuppliers.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalSuppliers.setText("1");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Model Suppliers");

        btnToSuppliers.setBackground(new java.awt.Color(180, 0, 40));
        btnToSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        btnToSuppliers.setText("Lihat Detail ->");
        btnToSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToSuppliersActionPerformed(evt);
            }
        });

        labelSuppliersImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\suppliers.png")); // NOI18N
        labelSuppliersImage.setText("product");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(txtTotalSuppliers))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelSuppliersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtTotalSuppliers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addComponent(labelSuppliersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnToSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.setBackground(new java.awt.Color(135, 206, 235));
        jPanel9.setPreferredSize(new java.awt.Dimension(371, 156));

        txtTotalPeminjaman.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalPeminjaman.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalPeminjaman.setText("1");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Transaksi Peminjaman");

        btnToTPeminjaman.setBackground(new java.awt.Color(70, 130, 180));
        btnToTPeminjaman.setForeground(new java.awt.Color(255, 255, 255));
        btnToTPeminjaman.setText("Lihat Detail ->");
        btnToTPeminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToTPeminjamanActionPerformed(evt);
            }
        });

        labelTPeminjaman.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\peminjaman.png")); // NOI18N
        labelTPeminjaman.setText("product");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(txtTotalPeminjaman))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addComponent(labelTPeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(btnToTPeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(txtTotalPeminjaman)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelTPeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(16, 16, 16)
                .addComponent(btnToTPeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 0, 128));
        jPanel5.setForeground(new java.awt.Color(0, 0, 128));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Total Barang Masuk");

        txtTotalBarangMasuk.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalBarangMasuk.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalBarangMasuk.setText("1");

        btnToTotalBM.setBackground(new java.awt.Color(0, 0, 128));
        btnToTotalBM.setForeground(new java.awt.Color(255, 255, 255));
        btnToTotalBM.setText("Lihat Detail ->");
        btnToTotalBM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToTotalBMActionPerformed(evt);
            }
        });

        labelBarangMasuk.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\barang_masuk.png")); // NOI18N
        labelBarangMasuk.setText("product");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToTotalBM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(txtTotalBarangMasuk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(txtTotalBarangMasuk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)))
                .addComponent(btnToTotalBM, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel10.setBackground(new java.awt.Color(204, 0, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(371, 156));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Total Barang Keluar");

        txtTotalBarangKeluar.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalBarangKeluar.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalBarangKeluar.setText("1");

        btnToTotalBK.setBackground(new java.awt.Color(153, 0, 153));
        btnToTotalBK.setForeground(new java.awt.Color(255, 255, 255));
        btnToTotalBK.setText("Lihat Detail ->");
        btnToTotalBK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToTotalBKActionPerformed(evt);
            }
        });

        labelBarangKeluar.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\barang_keluar.png")); // NOI18N
        labelBarangKeluar.setText("product");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToTotalBK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtTotalBarangKeluar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(txtTotalBarangKeluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addComponent(labelBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnToTotalBK, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel11.setBackground(new java.awt.Color(255, 102, 0));
        jPanel11.setPreferredSize(new java.awt.Dimension(371, 156));

        txtTotalTransaksiBM.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalTransaksiBM.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalTransaksiBM.setText("1");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 17)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Total Transaksi Barang Masuk");

        btnToTransaksiBM.setBackground(new java.awt.Color(204, 85, 0));
        btnToTransaksiBM.setForeground(new java.awt.Color(255, 255, 255));
        btnToTransaksiBM.setText("Lihat Detail ->");
        btnToTransaksiBM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToTransaksiBMActionPerformed(evt);
            }
        });

        labelTransaksiMasukImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\transaksi_barang_masuk.png")); // NOI18N
        labelTransaksiMasukImage.setText("product");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToTransaksiBM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(txtTotalTransaksiBM))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(labelTransaksiMasukImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtTotalTransaksiBM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12))
                    .addComponent(labelTransaksiMasukImage, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnToTransaksiBM, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel12.setBackground(new java.awt.Color(51, 0, 255));
        jPanel12.setPreferredSize(new java.awt.Dimension(371, 156));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total Transaksi Barang Keluar");

        txtTotalTransactionsBK.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalTransactionsBK.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalTransactionsBK.setText("1");

        btnToTransaksiBK.setBackground(new java.awt.Color(0, 0, 255));
        btnToTransaksiBK.setForeground(new java.awt.Color(255, 255, 255));
        btnToTransaksiBK.setText("Lihat Detail ->");
        btnToTransaksiBK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToTransaksiBKActionPerformed(evt);
            }
        });

        labelTransaksiKeluarImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\transaksi_barang_keluar.png")); // NOI18N
        labelTransaksiKeluarImage.setText("product");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(txtTotalTransactionsBK))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTransaksiKeluarImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(btnToTransaksiBK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(txtTotalTransactionsBK)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelTransaksiKeluarImage, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(btnToTransaksiBK, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel13.setBackground(new java.awt.Color(51, 255, 0));
        jPanel13.setPreferredSize(new java.awt.Dimension(315, 153));

        txtTotalPeminjamanDikembalikan.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalPeminjamanDikembalikan.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalPeminjamanDikembalikan.setText("1");

        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 20)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Peminjaman DIkembalikan");

        btnToPeminjamanDikembalikan.setBackground(new java.awt.Color(51, 204, 0));
        btnToPeminjamanDikembalikan.setForeground(new java.awt.Color(255, 255, 255));
        btnToPeminjamanDikembalikan.setText("Lihat Detail ->");
        btnToPeminjamanDikembalikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToPeminjamanDikembalikanActionPerformed(evt);
            }
        });

        labelPeminjamanDikembalikanImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\peminjaman_dikembalikan.png")); // NOI18N
        labelPeminjamanDikembalikanImage.setText("product");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToPeminjamanDikembalikan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(txtTotalPeminjamanDikembalikan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(labelPeminjamanDikembalikanImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(txtTotalPeminjamanDikembalikan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addComponent(labelPeminjamanDikembalikanImage, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(btnToPeminjamanDikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel14.setBackground(new java.awt.Color(255, 51, 51));
        jPanel14.setPreferredSize(new java.awt.Dimension(371, 156));

        txtTotalPeminjamanBelumDikembalikan.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
        txtTotalPeminjamanBelumDikembalikan.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalPeminjamanBelumDikembalikan.setText("1");

        jLabel15.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Peminjaman Belum Dikembalikan");

        btnToPeminjamanBelumDikembalikan.setBackground(new java.awt.Color(255, 0, 51));
        btnToPeminjamanBelumDikembalikan.setForeground(new java.awt.Color(255, 255, 255));
        btnToPeminjamanBelumDikembalikan.setText("Lihat Detail ->");
        btnToPeminjamanBelumDikembalikan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToPeminjamanBelumDikembalikanActionPerformed(evt);
            }
        });

        labelPeminjamanBelumDikembalikanImage.setIcon(new javax.swing.ImageIcon("C:\\Users\\yusrb\\OneDrive\\Dokumen\\NetBeansProjects\\inventaris_java\\images\\peminjaman_belum_dikembalikan.png")); // NOI18N
        labelPeminjamanBelumDikembalikanImage.setText("product");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnToPeminjamanBelumDikembalikan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(txtTotalPeminjamanBelumDikembalikan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelPeminjamanBelumDikembalikanImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(txtTotalPeminjamanBelumDikembalikan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addComponent(labelPeminjamanBelumDikembalikanImage, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnToPeminjamanBelumDikembalikan, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)))
                                .addGap(24, 24, 24))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnToTPeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToTPeminjamanActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToTPeminjamanActionPerformed

    private void btnToTotalBMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToTotalBMActionPerformed
        // TODO add your handling code here:
        
        BarangMasuk barang_masuk_page = new BarangMasuk(usernameForPage, levelForPage);
        barang_masuk_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToTotalBMActionPerformed

    private void btnToSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToSuppliersActionPerformed
        // TODO add your handling code here:

        Suppliers suppliers_page = new Suppliers(usernameForPage, levelForPage);
        suppliers_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToSuppliersActionPerformed

    private void btnToUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToUsersActionPerformed
        // TODO add your handling code here:
        
        Users users_page = new Users(usernameForPage, levelForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToUsersActionPerformed

    private void btnToProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToProductsActionPerformed
        // TODO add your handling code here:
        
        Products products_page = new Products(usernameForPage, levelForPage);
        products_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToProductsActionPerformed

    private void btnToTotalBKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToTotalBKActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToTotalBKActionPerformed

    private void btnToTransaksiBMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToTransaksiBMActionPerformed
        // TODO add your handling code here:
        
        BarangMasuk barang_masuk_page = new BarangMasuk(usernameForPage, levelForPage);
        barang_masuk_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToTransaksiBMActionPerformed

    private void btnToTransaksiBKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToTransaksiBKActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToTransaksiBKActionPerformed

    private void btnToPeminjamanDikembalikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToPeminjamanDikembalikanActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToPeminjamanDikembalikanActionPerformed

    private void btnToPeminjamanBelumDikembalikanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToPeminjamanBelumDikembalikanActionPerformed
        // TODO add your handling code here:
        
        BarangKeluar barang_keluar_page = new BarangKeluar(usernameForPage, levelForPage);
        barang_keluar_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnToPeminjamanBelumDikembalikanActionPerformed

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
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnToPeminjamanBelumDikembalikan;
    private javax.swing.JButton btnToPeminjamanDikembalikan;
    private javax.swing.JButton btnToProducts;
    private javax.swing.JButton btnToSuppliers;
    private javax.swing.JButton btnToTPeminjaman;
    private javax.swing.JButton btnToTotalBK;
    private javax.swing.JButton btnToTotalBM;
    private javax.swing.JButton btnToTransaksiBK;
    private javax.swing.JButton btnToTransaksiBM;
    private javax.swing.JButton btnToUsers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel labelBarangKeluar;
    private javax.swing.JLabel labelBarangMasuk;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelPeminjamanBelumDikembalikanImage;
    private javax.swing.JLabel labelPeminjamanDikembalikanImage;
    private javax.swing.JLabel labelProductImage;
    private javax.swing.JLabel labelSuppliersImage;
    private javax.swing.JLabel labelTPeminjaman;
    private javax.swing.JLabel labelTransaksiKeluarImage;
    private javax.swing.JLabel labelTransaksiMasukImage;
    private javax.swing.JLabel labelUsersImage;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtRoleForPage;
    private javax.swing.JLabel txtTotalBarangKeluar;
    private javax.swing.JLabel txtTotalBarangMasuk;
    private javax.swing.JLabel txtTotalPeminjaman;
    private javax.swing.JLabel txtTotalPeminjamanBelumDikembalikan;
    private javax.swing.JLabel txtTotalPeminjamanDikembalikan;
    private javax.swing.JLabel txtTotalProducts;
    private javax.swing.JLabel txtTotalSuppliers;
    private javax.swing.JLabel txtTotalTransactionsBK;
    private javax.swing.JLabel txtTotalTransaksiBM;
    private javax.swing.JLabel txtTotalUsers;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
