/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

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
        conn = DBConnection.getConnection();
        getSettings();
        
        setProductTerlaris();
        loadStokMinimum();
        setTotalProducts();
        setTotalUsers();
        setTotalSuppliers();
        setTotalBarangMasuk();
        setTotalBarangKeluar();
        setTotalTransaksiBarangMasuk();
        setTotalTransaksiBarangKeluar();
        setImageToDashboard();

        usernameForPage = username;
        levelForPage = level;
        batasiAkses();

        txtUsernameForPage.setText(usernameForPage);
        txtRoleForPage.setText(levelForPage);
        
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;
    
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
        setImageToLabel("images/barang_masuk.png", labelBarangMasuk);
        setImageToLabel("images/barang_keluar.png", labelBarangKeluar);
        setImageToLabel("images/transaksi_barang_masuk.png", labelTransaksiMasukImage);
        setImageToLabel("images/transaksi_barang_keluar.png", labelTransaksiKeluarImage);

        setupDynamicScaling();
    }
    
    public void setupDynamicScaling() {
        addDynamicScaling(labelProductImage);
        addDynamicScaling(labelUsersImage);
        addDynamicScaling(labelSuppliersImage);
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
    
    private void setImageToLabel(String resourcePath, JLabel label) {
        java.net.URL imageUrl = getClass().getClassLoader().getResource(resourcePath);

        if (imageUrl == null) {
            label.setText("Gambar tidak ditemukan: " + resourcePath);
            return;
        }

        ImageIcon icon = new ImageIcon(imageUrl);
        Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);

        label.setText("");
        label.setIcon(new ImageIcon(img));
    }
    
    public void setProductTerlaris() {
        try {
            String query = "SELECT nama, SUM(jumlah) AS total_terjual " +
                           "FROM detail_transaksi " +
                           "GROUP BY nama " +
                           "ORDER BY total_terjual DESC " +
                           "LIMIT 10";

            pst = conn.prepareStatement(query);
            rslt = pst.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            while (rslt.next()) {
                String namaProduk = rslt.getString("nama");
                int jumlahTerjual = rslt.getInt("total_terjual");
                dataset.addValue(jumlahTerjual, "Jumlah Terjual", namaProduk);
            }

            JFreeChart chart = ChartFactory.createBarChart(
                "10 Produk Terlaris",
                "Produk",
                "Jumlah Terjual",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
            );

            chart.setBackgroundPaint(new Color(245, 245, 245));
            TextTitle title = chart.getTitle();
            title.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            title.setPadding(10, 0, 0, 0);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            plot.setOutlineVisible(false);

            CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setLabelFont(new Font("Tahoma", Font.BOLD, 14));
            domainAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 9));
            domainAxis.setLowerMargin(0.05);
            domainAxis.setUpperMargin(0.05);

            plot.getRangeAxis().setLabelFont(new Font("Tahoma", Font.BOLD, 14));
            plot.getRangeAxis().setTickLabelFont(new Font("Tahoma", Font.PLAIN, 12));

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            Color barColor = new Color(123, 104, 238);
            renderer.setSeriesPaint(0, barColor);

            renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator() {
                @Override
                public String generateToolTip(CategoryDataset dataset, int row, int column) {
                    String produk = (String) dataset.getColumnKey(column);
                    Number value = dataset.getValue(row, column);
                    return produk + " : Stok " + value.intValue();
                }
            });

            renderer.setBarPainter(new StandardBarPainter());
            renderer.setShadowVisible(false);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1135, 350));
            chartPanel.setBackground(new Color(245, 245, 245));

            pnlTampilGrafik.removeAll();
            pnlTampilGrafik.setLayout(new BorderLayout());
            pnlTampilGrafik.add(chartPanel, BorderLayout.CENTER);
            pnlTampilGrafik.validate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan grafik produk terlaris: " + ex.getMessage());
        }
    }

    public void loadStokMinimum() {
        pnlTampilStokAlert.removeAll();
        pnlTampilStokAlert.setLayout(new BoxLayout(pnlTampilStokAlert, BoxLayout.Y_AXIS));
        pnlTampilStokAlert.setBackground(Color.WHITE);

        Color bgAlert = new Color(123, 104, 238);
        Color fgAlert = Color.WHITE;

        try {
            String sql = "SELECT kode_barang, nama, stok, stok_alert FROM products WHERE stok < stok_alert";
            pst = conn.prepareStatement(sql);
            rslt = pst.executeQuery();

            boolean adaData = false;

            while (rslt.next()) {
                adaData = true;
                String nama = rslt.getString("nama");
                int stok = rslt.getInt("stok");
                int stokAlert = rslt.getInt("stok_alert");

                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(bgAlert);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                card.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel lbl = new JLabel("\uD83D\uDD3B " + nama + " - " + stok + " pcs (Min: " + stokAlert + ")");
                lbl.setForeground(fgAlert);

                Font emojiFont = new Font("Segoe UI Emoji", Font.BOLD, 12);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                List<String> fonts = Arrays.asList(ge.getAvailableFontFamilyNames());

                if (!fonts.contains("Segoe UI Emoji")) {
                    emojiFont = lbl.getFont().deriveFont(Font.BOLD, 12f);
                }

                lbl.setFont(emojiFont);

                card.add(lbl, BorderLayout.CENTER);
                pnlTampilStokAlert.add(card);
                pnlTampilStokAlert.add(Box.createVerticalStrut(5));
            }

            if (!adaData) {
                JPanel card = new JPanel(new BorderLayout());
                card.setBackground(new Color(46, 204, 113));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                card.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel lblKosong = new JLabel("\u2705 Semua stok aman.");
                lblKosong.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

                lblKosong.setForeground(Color.WHITE);
                lblKosong.setFont(lblKosong.getFont().deriveFont(Font.BOLD, 12f));

                card.add(lblKosong, BorderLayout.CENTER);
                pnlTampilStokAlert.add(card);
            }

        } catch (SQLException e) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.RED);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            card.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel lblError = new JLabel("❌ Gagal memuat data stok.");
            lblError.setForeground(Color.WHITE);
            lblError.setFont(lblError.getFont().deriveFont(Font.BOLD, 12f));

            card.add(lblError, BorderLayout.CENTER);
            pnlTampilStokAlert.add(card);
            e.printStackTrace();
        } finally {
            try {
                if (rslt != null) rslt.close();
                if (pst != null) pst.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        pnlTampilStokAlert.revalidate();
        pnlTampilStokAlert.repaint();
    }

    private void batasiAkses() {
        switch (levelForPage.toLowerCase()) {
            case "administrator":
                break;

            case "petugas kasir":
                btnProducts.setVisible(false);
                btnCategories.setVisible(false);
                btnBrands.setVisible(false);
                btnUsers.setVisible(false);
                btnSuppliers.setVisible(false);
                btnReports.setVisible(false);
                btnTransactions.setVisible(false);
                btnSettings.setVisible(false);
                break;

            case "manager":
                btnProducts.setVisible(false);
                btnCategories.setVisible(false);
                btnBrands.setVisible(false);
                btnUsers.setVisible(false);
                btnSuppliers.setVisible(false);
                btnCashier.setVisible(false);
                break;

            default:
                JOptionPane.showMessageDialog(this, "Level tidak dikenali: " + levelForPage, "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;
        }
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

        public void setTotalBarangMasuk() {
            try {
                pst = conn.prepareStatement("SELECT SUM(jumlah) AS total_barang_masuk FROM detail_barang_masuk");
                rslt = pst.executeQuery();

                if (rslt.next()) {
                    int total = rslt.getInt("total_barang_masuk");
                    txtTotalBarangMasuk.setText(String.valueOf(total));
                }
            } catch (SQLException ex) {
                Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
                txtTotalBarangMasuk.setText("0");
            }
        }

    public void setTotalBarangKeluar() {
        try {
            pst = conn.prepareStatement("SELECT SUM(jumlah) AS total_barang_keluar FROM barang_keluar");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                int total = rslt.getInt("total_barang_keluar");
                txtTotalBarangKeluar.setText(String.valueOf(total));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
            txtTotalBarangKeluar.setText("0");
        }
    }


    public void setTotalTransaksiBarangMasuk() {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_masuk FROM barang_masuk");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                txtTotalTransaksiBM.setText(rslt.getString("total_barang_masuk"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setTotalTransaksiBarangKeluar() {
        try {
            pst = conn.prepareStatement("SELECT COUNT(*) AS total_barang_keluar FROM barang_keluar");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                txtTotalTransactionsBK.setText(rslt.getString("total_barang_keluar"));
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
        txtNamePageBottom = new javax.swing.JLabel();
        btnSuppliers = new javax.swing.JButton();
        btnBrands = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnPengeluaran = new javax.swing.JButton();
        btnCashier = new javax.swing.JButton();
        btnSalesTransactions = new javax.swing.JButton();
        btnTransactions = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnUsers = new javax.swing.JButton();
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
        jSeparator1 = new javax.swing.JSeparator();
        pnlTampilGrafik = new javax.swing.JPanel();
        pnlTampilStokAlert = new javax.swing.JPanel();

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

        txtNamePageBottom.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
        txtNamePageBottom.setForeground(new java.awt.Color(255, 255, 255));
        txtNamePageBottom.setText("namePage");

        btnSuppliers.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnSuppliers.setForeground(new java.awt.Color(255, 255, 255));
        btnSuppliers.setText("Suppliers");
        btnSuppliers.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnSuppliers.setBorderPainted(false);
        btnSuppliers.setContentAreaFilled(false);
        btnSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuppliersActionPerformed(evt);
            }
        });

        btnBrands.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        btnBrands.setForeground(new java.awt.Color(255, 255, 255));
        btnBrands.setText("Brands");
        btnBrands.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        btnBrands.setBorderPainted(false);
        btnBrands.setContentAreaFilled(false);
        btnBrands.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBrands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrandsActionPerformed(evt);
            }
        });

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
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255
                , 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnProducts.setBorderPainted(false);
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
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnCategories.setBorderPainted(false);
    btnCategories.setContentAreaFilled(false);
    btnCategories.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnCategories.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnCategoriesActionPerformed(evt);
        }
    });

    btnPengeluaran.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnPengeluaran.setForeground(new java.awt.Color(255, 255, 255));
    btnPengeluaran.setText("Operational Expenses");
    btnPengeluaran.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnPengeluaran.setBorderPainted(false);
    btnPengeluaran.setContentAreaFilled(false);
    btnPengeluaran.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnPengeluaran.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnPengeluaranActionPerformed(evt);
        }
    });

    btnCashier.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnCashier.setForeground(new java.awt.Color(255, 255, 255));
    btnCashier.setText("Cashier");
    btnCashier.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnCashier.setBorderPainted(false);
    btnCashier.setContentAreaFilled(false);
    btnCashier.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnCashier.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnCashierActionPerformed(evt);
        }
    });

    btnSalesTransactions.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnSalesTransactions.setForeground(new java.awt.Color(255, 255, 255));
    btnSalesTransactions.setText("Sales Transactions");
    btnSalesTransactions.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnSalesTransactions.setBorderPainted(false);
    btnSalesTransactions.setContentAreaFilled(false);
    btnSalesTransactions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnSalesTransactions.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnSalesTransactionsActionPerformed(evt);
        }
    });

    btnTransactions.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnTransactions.setForeground(new java.awt.Color(255, 255, 255));
    btnTransactions.setText("Stock Transactions");
    btnTransactions.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnTransactions.setBorderPainted(false);
    btnTransactions.setContentAreaFilled(false);
    btnTransactions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnTransactions.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTransactionsActionPerformed(evt);
        }
    });

    btnReports.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnReports.setForeground(new java.awt.Color(255, 255, 255));
    btnReports.setText("Reports");
    btnReports.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnReports.setBorderPainted(false);
    btnReports.setContentAreaFilled(false);
    btnReports.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnReports.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnReportsActionPerformed(evt);
        }
    });

    btnUsers.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnUsers.setForeground(new java.awt.Color(255, 255, 255));
    btnUsers.setText("Users");
    btnUsers.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnUsers.setBorderPainted(false);
    btnUsers.setContentAreaFilled(false);
    btnUsers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnUsers.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnUsersActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnSalesTransactions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnTransactions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnBrands, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnPengeluaran, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnReports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(43, 43, 43)
                    .addComponent(txtNamePageBottom)))
            .addGap(0, 0, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)
            .addComponent(btnCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnBrands, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16)
            .addComponent(btnCashier, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnSalesTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnPengeluaran, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(btnUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(39, 39, 39)
            .addComponent(txtNamePageBottom)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

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
            .addGap(13, 13, 13)
            .addComponent(txtNamePageTop)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1300, Short.MAX_VALUE)
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

    labelProductImage.setText("product");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(btnToProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap(107, Short.MAX_VALUE)
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
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalProducts)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel11)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addComponent(labelProductImage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(btnToProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel3.setText("Dashboard");

    jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
    jLabel4.setForeground(new java.awt.Color(153, 153, 153));
    jLabel4.setText("Control Panel");

    jPanel7.setBackground(new java.awt.Color(220, 130, 0));
    jPanel7.setPreferredSize(new java.awt.Dimension(423, 191));

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
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalUsers)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelUsersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(17, 17, 17)))
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
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalSuppliers)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel9))
                .addGroup(jPanel8Layout.createSequentialGroup()
                    .addGap(32, 32, 32)
                    .addComponent(labelSuppliersImage, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
            .addComponent(btnToSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
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
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelBarangMasuk, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)))
            .addComponent(btnToTotalBM, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
            .addComponent(labelBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel10Layout.setVerticalGroup(
        jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel10Layout.createSequentialGroup()
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalBarangKeluar)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel7))
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(labelBarangKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
            .addComponent(btnToTotalBK, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jPanel11.setBackground(new java.awt.Color(255, 102, 0));
    jPanel11.setPreferredSize(new java.awt.Dimension(371, 156));

    txtTotalTransaksiBM.setFont(new java.awt.Font("Times New Roman", 1, 40)); // NOI18N
    txtTotalTransaksiBM.setForeground(new java.awt.Color(255, 255, 255));
    txtTotalTransaksiBM.setText("1");

    jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelTransaksiMasukImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
    jPanel11Layout.setVerticalGroup(
        jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel11Layout.createSequentialGroup()
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalTransaksiBM)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel12))
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(34, 34, 34)
                    .addComponent(labelTransaksiMasukImage, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnToTransaksiBM, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    jPanel12.setBackground(new java.awt.Color(51, 0, 255));
    jPanel12.setPreferredSize(new java.awt.Dimension(371, 156));

    jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(labelTransaksiKeluarImage, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addComponent(btnToTransaksiBK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jPanel12Layout.setVerticalGroup(
        jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel12Layout.createSequentialGroup()
            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(txtTotalTransactionsBK)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel13))
                .addGroup(jPanel12Layout.createSequentialGroup()
                    .addGap(37, 37, 37)
                    .addComponent(labelTransaksiKeluarImage, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
            .addComponent(btnToTransaksiBK, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
    );

    pnlTampilGrafik.setBackground(new java.awt.Color(123, 104, 238));

    javax.swing.GroupLayout pnlTampilGrafikLayout = new javax.swing.GroupLayout(pnlTampilGrafik);
    pnlTampilGrafik.setLayout(pnlTampilGrafikLayout);
    pnlTampilGrafikLayout.setHorizontalGroup(
        pnlTampilGrafikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 1370, Short.MAX_VALUE)
    );
    pnlTampilGrafikLayout.setVerticalGroup(
        pnlTampilGrafikLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 349, Short.MAX_VALUE)
    );

    pnlTampilStokAlert.setBackground(new java.awt.Color(123, 104, 238));
    pnlTampilStokAlert.setPreferredSize(new java.awt.Dimension(100, 349));

    javax.swing.GroupLayout pnlTampilStokAlertLayout = new javax.swing.GroupLayout(pnlTampilStokAlert);
    pnlTampilStokAlert.setLayout(pnlTampilStokAlertLayout);
    pnlTampilStokAlertLayout.setHorizontalGroup(
        pnlTampilStokAlertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 0, Short.MAX_VALUE)
    );
    pnlTampilStokAlertLayout.setVerticalGroup(
        pnlTampilStokAlertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGap(0, 349, Short.MAX_VALUE)
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
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE))
                                    .addGap(14, 14, 14)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE))
                                    .addGap(14, 14, 14)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE))
                                    .addGap(14, 14, 14)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(pnlTampilGrafik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(pnlTampilStokAlert, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)))
                            .addGap(24, 24, 24))))
                .addGroup(layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(jSeparator1))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(50, 50, 50)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(jLabel4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(10, 10, 10)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlTampilGrafik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pnlTampilStokAlert, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(53, 53, 53))
    );

    pack();
    setLocationRelativeTo(null);
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

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        // TODO add your handling code here:

        Suppliers suppliers_page = new Suppliers(usernameForPage, levelForPage);
        suppliers_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnBrandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrandsActionPerformed
        // TODO add your handling code here:

        Brands brands_page = new Brands(usernameForPage, levelForPage);
        brands_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBrandsActionPerformed

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

    private void btnPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengeluaranActionPerformed
        // TODO add your handling code here:
        
        Pengeluaran pengeluaran_page = new Pengeluaran(usernameForPage, levelForPage);
        pengeluaran_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPengeluaranActionPerformed

    private void btnCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCashierActionPerformed
        // TODO add your handling code here:

        Cashier cashier_page = new Cashier(usernameForPage, levelForPage);
        cashier_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCashierActionPerformed

    private void btnSalesTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesTransactionsActionPerformed
        // TODO add your handling code here:

        SalesTransactions sales_transactions_page = new SalesTransactions(usernameForPage, levelForPage);
        sales_transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSalesTransactionsActionPerformed

    private void btnTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionsActionPerformed
        // TODO add your handling code here:

        Transactions transactions_page = new Transactions(usernameForPage, levelForPage);
        transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTransactionsActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:
        
        Laporan laporan_page = new Laporan(usernameForPage, levelForPage);
        laporan_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        // TODO add your handling code here:

        Users users_page = new Users(usernameForPage, levelForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUsersActionPerformed

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
    private javax.swing.JButton btnCashier;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPengeluaran;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSalesTransactions;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnToProducts;
    private javax.swing.JButton btnToSuppliers;
    private javax.swing.JButton btnToTotalBK;
    private javax.swing.JButton btnToTotalBM;
    private javax.swing.JButton btnToTransaksiBK;
    private javax.swing.JButton btnToTransaksiBM;
    private javax.swing.JButton btnToUsers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelBarangKeluar;
    private javax.swing.JLabel labelBarangMasuk;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JLabel labelProductImage;
    private javax.swing.JLabel labelSuppliersImage;
    private javax.swing.JLabel labelTransaksiKeluarImage;
    private javax.swing.JLabel labelTransaksiMasukImage;
    private javax.swing.JLabel labelUsersImage;
    private javax.swing.JPanel pnlTampilGrafik;
    private javax.swing.JPanel pnlTampilStokAlert;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtRoleForPage;
    private javax.swing.JLabel txtTotalBarangKeluar;
    private javax.swing.JLabel txtTotalBarangMasuk;
    private javax.swing.JLabel txtTotalProducts;
    private javax.swing.JLabel txtTotalSuppliers;
    private javax.swing.JLabel txtTotalTransactionsBK;
    private javax.swing.JLabel txtTotalTransaksiBM;
    private javax.swing.JLabel txtTotalUsers;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
