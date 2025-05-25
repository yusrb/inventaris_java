/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Image;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author yusrb
 */
public class Cashier extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    private String namaAplikasi = "";
    private byte[] logoBytes = null;
    
    DefaultTableModel model;
    
    public Cashier(String username, String level) {
        initComponents();
        
        javax.swing.table.JTableHeader header = tblKeranjang.getTableHeader();
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
        setupQtyEditingValidation();
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
    
    public void getSettings() {
        try {
            pst = conn.prepareStatement("SELECT logo, name_application FROM settings LIMIT 1");
            rslt = pst.executeQuery();

            if (rslt.next()) {
                namaAplikasi = rslt.getString("name_application");
                logoBytes = rslt.getBytes("logo");

                txtNamePageTop.setText(namaAplikasi);
                txtNamePageBottom.setText(namaAplikasi);

                if (logoBytes != null) {
                    ImageIcon icon = new ImageIcon(logoBytes);
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

    private void setupQtyEditingValidation() 
        {
        model = (DefaultTableModel) tblKeranjang.getModel();

        DefaultTableModel newModel = new DefaultTableModel(
            new Object [][] {},
            new String [] {"No", "Kode Barang", "Nama", "Harga", "Qty", "Brand", "Kategori"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        tblKeranjang.setModel(newModel);
        model = newModel;

        model.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 4) {
                    String kodeBarang = model.getValueAt(row, 1).toString();
                    int newQty = 0;

                    try {
                        newQty = Integer.parseInt(model.getValueAt(row, column).toString());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Qty harus berupa angka.");
                        model.setValueAt(1, row, column);
                        return;
                    }

                    if (newQty <= 0) {
                        JOptionPane.showMessageDialog(this, "Qty minimal 1.");
                        model.setValueAt(1, row, column);
                        return;
                    }

                    try {
                        pst = conn.prepareStatement("SELECT stok FROM products WHERE kode_barang = ?");
                        pst.setString(1, kodeBarang);
                        rslt = pst.executeQuery();
                        if (rslt.next()) {
                            int stok = rslt.getInt("stok");
                            if (newQty > stok) {
                                JOptionPane.showMessageDialog(this, "Qty melebihi stok (" + stok + ").");
                                model.setValueAt(stok, row, column);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    updateTotalHarga();
                }
            }
        });
    }


    public void loadProductToCart(String kodeProduk) {

        if (tblKeranjang == null) {
            System.out.println("tblKeranjang masih null!");
            return;
        }
        if (tblKeranjang.getModel() == null) {
            System.out.println("Model dari tblKeranjang null!");
            return;
        }

        try {
            String sql = "SELECT p.kode_barang, p.nama, p.harga, p.stok, b.nama_brand, c.nama_category " +
                         "FROM products p " +
                         "LEFT JOIN brands b ON p.brand_id = b.id " +
                         "LEFT JOIN categories c ON p.category_id = c.id " +
                         "WHERE p.kode_barang = ?";

            pst = conn.prepareStatement(sql);
            pst.setString(1, kodeProduk);
            rslt = pst.executeQuery();

            if (rslt.next()) {
                String kode = rslt.getString("kode_barang");
                String nama = rslt.getString("nama");
                int harga = rslt.getInt("harga");
                int stok = rslt.getInt("stok");
                String brand = rslt.getString("nama_brand");
                String kategori = rslt.getString("nama_category");

                DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();

                if (stok <= 0) {
                    JOptionPane.showMessageDialog(this, "Stok habis untuk produk: " + nama);
                    return;
                }

                boolean found = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 1).toString().equals(kode)) {
                        int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
                        if (qty + 1 > stok) {
                            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi untuk produk: " + nama);
                            return;
                        }
                        model.setValueAt(qty + 1, i, 4);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Vector<Object> v2 = new Vector<>();
                    v2.add(model.getRowCount() + 1);  // No
                    v2.add(kode);                    // Kode Barang
                    v2.add(nama);                    // Nama
                    v2.add(harga);                   // Harga
                    v2.add(1);                       // Qty (default 1)
                    v2.add(brand != null ? brand : "-"); // Brand
                    v2.add(kategori != null ? kategori : "-"); // Kategori
                    model.addRow(v2);
                }

                updateTotalHarga();
            } else {
                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cashier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getJumlahProdukDalamKeranjang(String kodeProduk) {
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String kode = model.getValueAt(i, 0).toString();
            if (kode.equals(kodeProduk)) {
                return Integer.parseInt(model.getValueAt(i, 3).toString());
            }
        }

        return 0;
    }

    public void updateTotalHarga() {
        int total = 0;
        model = (DefaultTableModel) tblKeranjang.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
            int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
            total += harga * qty;
        }

        txtTotalHarga.setText(String.valueOf(total));
        txtAbjadTotal.setText(angkaKeTerbilang(total));
    }
    
    private void hitungKembalian() {
        try {
            int total = Integer.parseInt(txtTotalHarga.getText());
            int bayar = Integer.parseInt(txtTotalDibayar.getText());
            int kembalian = bayar - total;
            txtTotalKembalian.setText(String.valueOf(kembalian));
        } catch (NumberFormatException e) {
            txtTotalKembalian.setText("0");
        }
    }
    
    public static String angkaKeTerbilang(int number) {
        String[] huruf = {"", "Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"};

        if (number < 12)
            return huruf[number];
        else if (number < 20)
            return huruf[number - 10] + " Belas";
        else if (number < 100)
            return huruf[number / 10] + " Puluh " + huruf[number % 10];
        else if (number < 200)
            return "Seratus " + angkaKeTerbilang(number - 100);
        else if (number < 1000)
            return huruf[number / 100] + " Ratus " + angkaKeTerbilang(number % 100);
        else if (number < 2000)
            return "Seribu " + angkaKeTerbilang(number - 1000);
        else if (number < 1000000)
            return angkaKeTerbilang(number / 1000) + " Ribu " + angkaKeTerbilang(number % 1000);
        else
            return String.valueOf(number);
    }
    
    private void cetakNota() {
        String namaAplikasi = txtNamePageTop.getText();

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: monospace; font-size: 12px;'>");
        html.append("<h2 style='text-align:center;'>").append(namaAplikasi).append("</h2>");
        html.append("<h3 style='text-align:center;'>NOTA PEMBELIAN</h3>");
        html.append("<p>Kasir: ").append(usernameForPage).append("</p>");
        html.append("<hr>");

        html.append("<table width='100%' style='border-collapse: collapse;'>");
        html.append("<tr><th align='left'>Nama Produk</th><th>Qty</th><th>Harga</th><th>Subtotal</th></tr>");

        for (int i = 0; i < model.getRowCount(); i++) {
            String nama = model.getValueAt(i, 2).toString();
            String qty = model.getValueAt(i, 4).toString();
            String harga = model.getValueAt(i, 3).toString();
            int subtotal = Integer.parseInt(harga) * Integer.parseInt(qty);

            html.append("<tr>");
            html.append("<td>").append(nama).append("</td>");
            html.append("<td align='center'>").append(qty).append("</td>");
            html.append("<td align='right'>").append(harga).append("</td>");
            html.append("<td align='right'>").append(subtotal).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("<hr>");

        html.append("<p><b>Total:</b> ").append(txtTotalHarga.getText()).append("</p>");
        html.append("<p><b>Dibayar:</b> ").append(txtTotalDibayar.getText()).append("</p>");
        html.append("<p><b>Kembalian:</b> ").append(txtTotalKembalian.getText()).append("</p>");
        html.append("<hr>");
        html.append("<p style='text-align:center;'>Terima kasih telah berbelanja!</p>");
        html.append("</body></html>");

        javax.swing.JTextPane textPane = new javax.swing.JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(html.toString());

        try {
            boolean done = textPane.print();
            if (done) {
                System.out.println("Nota berhasil dicetak");
            } else {
                System.out.println("Pencetakan dibatalkan");
            }
        } catch (java.awt.print.PrinterException ex) {
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtUsernameForPage = new javax.swing.JLabel();
        txtLevelForPage = new javax.swing.JLabel();
        btnSuppliers = new javax.swing.JButton();
        btnBrands = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnCashier = new javax.swing.JButton();
        btnSalesTransactions = new javax.swing.JButton();
        btnTransactions = new javax.swing.JButton();
        btnUsers = new javax.swing.JButton();
        txtNamePageBottom = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        txtNamePageTop = new javax.swing.JLabel();
        labelLogo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtScanCodeProduct = new javax.swing.JTextField();
        btnShowProductList = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        txtTotalHarga = new javax.swing.JTextField();
        txtTotalDibayar = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtTotalKembalian = new javax.swing.JTextField();
        btnBayar = new javax.swing.JButton();
        scrollTblKeranjang = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        txtAbjadTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cashier Page");
        setBackground(new java.awt.Color(204, 204, 204));

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
        btnDashboard.setBorderPainted(false);
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

    btnCashier.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnCashier.setForeground(new java.awt.Color(255, 255, 255));
    btnCashier.setText("Cashier");
    btnCashier.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
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
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
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
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
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

    txtNamePageBottom.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
    txtNamePageBottom.setForeground(new java.awt.Color(255, 255, 255));
    txtNamePageBottom.setText("namePage");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(47, 47, 47)
            .addComponent(txtNamePageBottom))
        .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnSalesTransactions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnTransactions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnCategories, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(btnBrands, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(1, 1, 1)
            .addComponent(btnCategories, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnBrands, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16)
            .addComponent(btnCashier, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnSalesTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnTransactions, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16)
            .addComponent(btnUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(txtNamePageBottom)
            .addContainerGap(74, Short.MAX_VALUE))
    );

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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1036, Short.MAX_VALUE)
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

    jLabel2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
    jLabel2.setForeground(new java.awt.Color(255, 255, 255));
    jLabel2.setText("Scan / Code");

    txtScanCodeProduct.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            txtScanCodeProductKeyPressed(evt);
        }
    });

    btnShowProductList.setBackground(new java.awt.Color(153, 153, 255));
    btnShowProductList.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
    btnShowProductList.setText("....");
    btnShowProductList.setToolTipText("Tekan untuk melihat daftar produk");
    btnShowProductList.setAlignmentY(12.0F);
    btnShowProductList.setIconTextGap(0);
    btnShowProductList.setMargin(new java.awt.Insets(2, 14, 5, 14));
    btnShowProductList.setOpaque(false);
    btnShowProductList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnShowProductListActionPerformed(evt);
        }
    });

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGap(26, 26, 26)
            .addComponent(jLabel2)
            .addGap(74, 74, 74)
            .addComponent(txtScanCodeProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(0, 0, 0)
            .addComponent(btnShowProductList, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(553, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2)
            .addGap(40, 40, 40))
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(txtScanCodeProduct)
                .addComponent(btnShowProductList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );

    jPanel5.setBackground(new java.awt.Color(123, 104, 238));

    jLabel3.setFont(new java.awt.Font("Tahoma", 1, 32)); // NOI18N
    jLabel3.setForeground(new java.awt.Color(255, 255, 255));
    jLabel3.setText("Total");

    jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel4.setForeground(new java.awt.Color(255, 255, 255));
    jLabel4.setText("Total Harga");

    txtTotalDibayar.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
            txtTotalDibayarKeyReleased(evt);
        }
    });

    jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel5.setForeground(new java.awt.Color(255, 255, 255));
    jLabel5.setText("Total Dibayar");

    jLabel6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel6.setForeground(new java.awt.Color(255, 255, 255));
    jLabel6.setText("Kembalian");

    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
    jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jSeparator1)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(28, 28, 28)
                    .addComponent(jLabel3))
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(29, 29, 29)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTotalDibayar, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTotalKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel5Layout.setVerticalGroup(
        jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel3)
            .addGap(18, 18, 18)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jLabel4)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtTotalHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel5)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtTotalDibayar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jLabel6)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtTotalKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(37, Short.MAX_VALUE))
    );

    btnBayar.setFont(new java.awt.Font("DejaVu Sans", 1, 29)); // NOI18N
    btnBayar.setText("Bayar");
    btnBayar.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnBayarActionPerformed(evt);
        }
    });

    tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null}
        },
        new String [] {
            "id", "kode_barang", "nama", "harga", "stok", "brand", "kategori"
        }
    ));
    scrollTblKeranjang.setViewportView(tblKeranjang);

    jPanel6.setBackground(new java.awt.Color(204, 204, 204));

    txtAbjadTotal.setFont(new java.awt.Font("DejaVu Math TeX Gyre", 0, 18)); // NOI18N
    txtAbjadTotal.setText("Enam Puluh Ribu");

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(txtAbjadTotal)
            .addContainerGap(870, Short.MAX_VALUE))
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
            .addContainerGap(18, Short.MAX_VALUE)
            .addComponent(txtAbjadTotal)
            .addContainerGap())
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
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(scrollTblKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 1048, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(24, 24, 24))))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(34, 34, 34)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(38, 38, 38)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrollTblKeranjang))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        // TODO add your handling code here:

        Users users_page = new Users(usernameForPage, levelForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUsersActionPerformed

    private void btnShowProductListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowProductListActionPerformed
        // TODO add your handling code here:
        
        ProductList product_list_page = new ProductList(this, usernameForPage, this);
        product_list_page.setVisible(true);
    }//GEN-LAST:event_btnShowProductListActionPerformed

    private void txtScanCodeProductKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScanCodeProductKeyPressed
        // TODO add your handling code here:
        
        String kode = txtScanCodeProduct.getText();
        if (kode.length() >= 5) {
            loadProductToCart(kode);
            txtScanCodeProduct.setText("");
        }
    }//GEN-LAST:event_txtScanCodeProductKeyPressed

    private void txtTotalDibayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTotalDibayarKeyReleased
        // TODO add your handling code here:
        
        hitungKembalian();
    }//GEN-LAST:event_txtTotalDibayarKeyReleased

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // TODO add your handling code here:
        
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong.");
            return;
        }

        int totalHarga, totalDibayar;

        try {
            totalHarga = Integer.parseInt(txtTotalHarga.getText());
            totalDibayar = Integer.parseInt(txtTotalDibayar.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Total dibayar harus berupa angka.");
            return;
        }

        if (totalDibayar < totalHarga) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar kurang dari total harga.");
            return;
        }

        int kembalian = totalDibayar - totalHarga;
        txtTotalKembalian.setText(String.valueOf(kembalian));

        try {
            conn.setAutoCommit(false);

            String insertTransaksiSql = "INSERT INTO transaksi (tanggal, total_item, total_harga, total_bayar, kasir) VALUES (NOW(), ?, ?, ?, ?)";
            pst = conn.prepareStatement(insertTransaksiSql, Statement.RETURN_GENERATED_KEYS);

            int totalItem = 0;
            for (int i = 0; i < model.getRowCount(); i++)
                totalItem += Integer.parseInt(model.getValueAt(i, 4).toString());

            pst.setInt(1, totalItem);
            pst.setInt(2, totalHarga);
            pst.setInt(3, totalDibayar);
            pst.setString(4, usernameForPage);
            pst.executeUpdate();

            rslt = pst.getGeneratedKeys();
            int transaksiId = rslt.next() ? rslt.getInt(1) : -1;
            if (transaksiId == -1) throw new SQLException("Gagal mendapatkan ID transaksi.");

            pst = conn.prepareStatement("INSERT INTO transaksi_detail (transaksi_id, kode_barang, nama, jumlah, subtotal) VALUES (?, ?, ?, ?, ?)");
            PreparedStatement pstUpdateStok = conn.prepareStatement("UPDATE products SET stok = stok - ? WHERE kode_barang = ?");

            for (int i = 0; i < model.getRowCount(); i++) {
                String kodeBarang = model.getValueAt(i, 1).toString();
                String nama = model.getValueAt(i, 2).toString();
                int jumlah = Integer.parseInt(model.getValueAt(i, 4).toString());
                int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
                int subtotal = harga * jumlah;

                pst.setInt(1, transaksiId);
                pst.setString(2, kodeBarang);
                pst.setString(3, nama);
                pst.setInt(4, jumlah);
                pst.setInt(5, subtotal);
                pst.addBatch();

                pstUpdateStok.setInt(1, jumlah);
                pstUpdateStok.setString(2, kodeBarang);
                pstUpdateStok.addBatch();
            }

            pst.executeBatch();
            pstUpdateStok.executeBatch();

            conn.commit();

            cetakNota();

            model.setRowCount(0);
            txtTotalHarga.setText("0");
            txtAbjadTotal.setText("");
            txtTotalDibayar.setText("");
            txtTotalKembalian.setText("");

            JOptionPane.showMessageDialog(this, "Transaksi berhasil.");
        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }//GEN-LAST:event_btnBayarActionPerformed

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
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cashier("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCashier;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnSalesTransactions;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnShowProductList;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JScrollPane scrollTblKeranjang;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JLabel txtAbjadTotal;
    private javax.swing.JLabel txtLevelForPage;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JTextField txtScanCodeProduct;
    private javax.swing.JTextField txtTotalDibayar;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JTextField txtTotalKembalian;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
