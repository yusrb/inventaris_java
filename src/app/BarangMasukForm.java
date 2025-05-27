/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.Color;
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
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.SwingConstants;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author yusrb
 */
public class BarangMasukForm extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    

    private final String usernameForPage;
    private final String levelForPage;
    
    private final int supplierId;

    private String namaAplikasi = "";
    private byte[] logoBytes = null;
    
    DefaultTableModel model;
    Connection conn;
    PreparedStatement pst;
    ResultSet rslt;

    public BarangMasukForm(BarangMasuk barangMasuk, String username, String level,
                           int supplierId, String namaSupplier, String kontak, String alamat) {
        
            initComponents();
            Connection();
            setupModel();
            getSettings();
            setAlertStock();
            batasiAkses();

            this.usernameForPage = username;
            this.levelForPage = level;
            this.supplierId = supplierId;

            txtUsernameForPage.setText(usernameForPage);
            txtLevelForPage.setText(levelForPage);

            txtNamaSupplier.setText("Supplier: " + namaSupplier);
            txtKontak.setText("Kontak: " + kontak);
            txtAlamat.setText("Kontak: " + alamat);

            setLocationRelativeTo(null);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        public void Connection() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost/inventaris_java", "root", "");
            } catch (Exception e) {
                Logger.getLogger(BarangMasukForm.class.getName()).log(Level.SEVERE, null, e);
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
                Logger.getLogger(BarangMasukForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void setAlertStock() {
            try {
                pst = conn.prepareStatement("SELECT nama, stok, stok_alert FROM products WHERE stok < stok_alert");
                rslt = pst.executeQuery();

                while(rslt.next())
                {
                    btnNotifikasi.setText("Stok Barang < Stok Alert");
                    btnNotifikasi.setBackground(Color.RED);
                    btnNotifikasi.setForeground(Color.WHITE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
        public void loadProductToCart(String kodeBarang) {
           try {
               for (int i = 0; i < model.getRowCount(); i++) {
                   if (model.getValueAt(i, 2).toString().equals(kodeBarang)) {
                        int jumlahLama = Integer.parseInt(model.getValueAt(i, 4).toString());
                        int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
                        int jumlahBaru = jumlahLama + 1;
                        model.setValueAt(jumlahBaru, i, 4);
                        model.setValueAt(harga * jumlahBaru, i, 5);
                        
                       updateTotalHarga();
                       return;
                   }
               }

               String sql = "SELECT id, kode_barang, nama, harga FROM products WHERE kode_barang = ?";
               pst = conn.prepareStatement(sql);
               pst.setString(1, kodeBarang);
               rslt = pst.executeQuery();
               if (rslt.next()) {
                   String id = rslt.getString("id");
                   String kode = rslt.getString("kode_barang");
                   String nama = rslt.getString("nama");
                   int harga = rslt.getInt("harga");
                   int jumlah = 1;
                   int subtotal = harga * jumlah;
                   String barangMasukId = "";
                   Object[] row = {id, kode, nama, harga, jumlah, subtotal};
                   model.addRow(row);
                   updateTotalHarga();
               } else {
                   JOptionPane.showMessageDialog(this, "Produk dengan kode " + kodeBarang + " tidak ditemukan.");
               }
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(this, "Error load product: " + e.getMessage());
           }
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
        
        
    private void setupModel() {
            model = new DefaultTableModel(
                new Object [][] {},
                new String [] {"Id", "Kode Barang", "Nama", "Harga", "Jumlah", "Subtotal"}
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };

            tblKeranjang.setModel(model);

            model.addTableModelListener(e -> {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();

                    if (column == 4) {
                        try {
                            int newJumlah = Integer.parseInt(model.getValueAt(row, column).toString());
                            if (newJumlah <= 0) {
                                JOptionPane.showMessageDialog(this, "Jumlah minimal 1.");
                                newJumlah = 1;
                                model.setValueAt(newJumlah, row, column);
                            }
                            int harga = Integer.parseInt(model.getValueAt(row, 3).toString());
                            int subtotal = harga * newJumlah;
                            model.setValueAt(subtotal, row, 5);
                            updateTotalHarga();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
                            model.setValueAt(1, row, column);
                            int harga = Integer.parseInt(model.getValueAt(row, 3).toString());
                            model.setValueAt(harga, row, 5);
                            updateTotalHarga();
                        }
                    }
                }
            });
        }

        public void updateTotalHarga() {
            int total = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
                int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
                total += harga * qty;
            }

            txtTotalHarga.setText(String.valueOf(total));
            txtAbjadTotal.setText(angkaKeTerbilang(total));
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
            String namaSupplier = "Tidak Diketahui";

            try {
                PreparedStatement pstSupplier = conn.prepareStatement("SELECT nama_supplier FROM suppliers WHERE id = ?");
                pstSupplier.setInt(1, supplierId);
                ResultSet rsSupplier = pstSupplier.executeQuery();
                if (rsSupplier.next()) {
                    namaSupplier = rsSupplier.getString("nama_supplier");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family: monospace;'>");
            html.append("<h2 style='text-align:center;'>").append(namaAplikasi).append("</h2>");
            html.append("<h3 style='text-align:center;'>NOTA PEMBELIAN</h3>");
            html.append("<p>Supplier: ").append(namaSupplier).append("</p>");
            html.append("<p>Kasir: ").append(usernameForPage).append("</p>");
            html.append("<hr>");
            html.append("<table width='100%'><tr><th>Nama</th><th>Qty</th><th>Harga</th><th>Subtotal</th></tr>");

            for (int i = 0; i < model.getRowCount(); i++) {
                String nama = model.getValueAt(i, 2).toString();
                String qty = model.getValueAt(i, 4).toString();
                String harga = model.getValueAt(i, 3).toString();
                int subtotal = Integer.parseInt(harga) * Integer.parseInt(qty);

                html.append("<tr>");
                html.append("<td>").append(nama).append("</td>");
                html.append("<td>").append(qty).append("</td>");
                html.append("<td>").append(harga).append("</td>");
                html.append("<td>").append(subtotal).append("</td>");
                html.append("</tr>");
            }

            html.append("</table><hr>");
            html.append("<p>Total: ").append(txtTotalHarga.getText()).append("</p>");
            html.append("<p>Dibayar: ").append(txtTotalDibayar.getText()).append("</p>");
            html.append("<p style='text-align:center;'>Terima kasih!</p>");
            html.append("</body></html>");

            JTextPane textPane = new JTextPane();
            textPane.setContentType("text/html");
            textPane.setText(html.toString());

            try {
                boolean printed = textPane.print();
                if (printed) System.out.println("Nota berhasil dicetak");
            } catch (Exception e) {
                e.printStackTrace();
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
        btnTransactions = new javax.swing.JButton();
        btnSalesTransactions = new javax.swing.JButton();
        btnPengeluaran = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        btnBrands = new javax.swing.JButton();
        btnSuppliers = new javax.swing.JButton();
        txtNamePageBottom = new javax.swing.JLabel();
        btnUsers = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnCashier = new javax.swing.JButton();
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
        btnBayar = new javax.swing.JButton();
        scrollTblKeranjang = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        txtAbjadTotal = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        txtNamaSupplier = new javax.swing.JLabel();
        txtKontak = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JLabel();
        btnNotifikasi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cashier Page");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);

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

    txtNamePageBottom.setFont(new java.awt.Font("Palatino Linotype", 1, 30)); // NOI18N
    txtNamePageBottom.setForeground(new java.awt.Color(255, 255, 255));
    txtNamePageBottom.setText("namePage");

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

    btnCashier.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnCashier.setForeground(new java.awt.Color(255, 255, 255));
    btnCashier.setText("Cashier");
    btnCashier.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1), 
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

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addGap(43, 43, 43)
            .addComponent(txtNamePageBottom))
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
            .addGap(13, 13, 13)
            .addComponent(txtNamePageTop)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1043, Short.MAX_VALUE)
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
            .addGap(28, 28, 28)
            .addComponent(jLabel2)
            .addGap(72, 72, 72)
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
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(28, Short.MAX_VALUE))
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
            .addContainerGap(54, Short.MAX_VALUE))
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
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null}
        },
        new String [] {
            "id", "kode_barang", "nama", "harga", "jumlah", "subtotal"
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

    btnDelete.setBackground(new java.awt.Color(255, 0, 0));
    btnDelete.setForeground(new java.awt.Color(255, 255, 255));
    btnDelete.setText("Delete");
    btnDelete.setBorderPainted(false);
    btnDelete.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDeleteActionPerformed(evt);
        }
    });

    btnReset.setBackground(new java.awt.Color(102, 102, 102));
    btnReset.setForeground(new java.awt.Color(255, 255, 255));
    btnReset.setText("Reset");
    btnReset.setBorderPainted(false);
    btnReset.setMaximumSize(new java.awt.Dimension(77, 29));
    btnReset.setMinimumSize(new java.awt.Dimension(77, 29));
    btnReset.setPreferredSize(new java.awt.Dimension(77, 29));
    btnReset.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnResetActionPerformed(evt);
        }
    });

    txtNamaSupplier.setText("Supplier");

    txtKontak.setText("Kontak");

    txtAlamat.setText("Alamat");

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
                    .addGap(24, 24, 24)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtKontak)
                                .addComponent(txtAlamat))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtNamaSupplier)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnNotifikasi))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(scrollTblKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 1048, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnBayar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(24, 24, 24))))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(16, 16, 16)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtNamaSupplier)
                .addComponent(btnNotifikasi, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(0, 0, 0)
            .addComponent(txtKontak)
            .addGap(10, 10, 10)
            .addComponent(txtAlamat)
            .addGap(18, 18, 18)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnDelete)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(scrollTblKeranjang, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void btnShowProductListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowProductListActionPerformed
        // TODO add your handling code here:
        
        BarangMasukProductList products_list = new BarangMasukProductList(null, usernameForPage, this);
        products_list.setVisible(true);
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
        
    }//GEN-LAST:event_txtTotalDibayarKeyReleased

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // TODO add your handling code here:
        
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong.");
            return;
        }

        int totalHarga;
        int totalDibayar;
        int totalItem = 0;

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

        try {
            conn.setAutoCommit(false);

            for (int i = 0; i < model.getRowCount(); i++) {
                int qty = Integer.parseInt(model.getValueAt(i, 4).toString());
                totalItem += qty;
            }

            String sqlBarangMasuk = "INSERT INTO barang_masuk (tanggal, supplier_id, total_item, total_harga, status) VALUES (NOW(), ?, ?, ?, 'masuk')";
            pst = conn.prepareStatement(sqlBarangMasuk, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, supplierId);
            pst.setInt(2, totalItem);
            pst.setInt(3, totalHarga);
            pst.executeUpdate();

            rslt = pst.getGeneratedKeys();
            int barangMasukId = rslt.next() ? rslt.getInt(1) : -1;
            if (barangMasukId == -1) throw new SQLException("Gagal mendapatkan ID barang masuk.");

            pst = conn.prepareStatement("INSERT INTO detail_barang_masuk (barang_masuk_id, kode_barang, nama, harga, jumlah, subtotal) VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement pstUpdateStok = conn.prepareStatement("UPDATE products SET stok = stok + ? WHERE kode_barang = ?");

            for (int i = 0; i < model.getRowCount(); i++) {
                String kode = model.getValueAt(i, 1).toString();
                String nama = model.getValueAt(i, 2).toString();
                int harga = Integer.parseInt(model.getValueAt(i, 3).toString());
                int jumlah = Integer.parseInt(model.getValueAt(i, 4).toString());
                int subtotal = Integer.parseInt(model.getValueAt(i, 5).toString());

                pst.setInt(1, barangMasukId);
                pst.setString(2, kode);
                pst.setString(3, nama);
                pst.setInt(4, harga);
                pst.setInt(5, jumlah);
                pst.setInt(6, subtotal);
                pst.addBatch();

                pstUpdateStok.setInt(1, jumlah);
                pstUpdateStok.setString(2, kode);
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

            JOptionPane.showMessageDialog(this, "Barang masuk berhasil disimpan.");

        } catch (SQLException ex) {
            try { conn.rollback(); } catch (SQLException rollbackEx) { rollbackEx.printStackTrace(); }
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data barang masuk: " + ex.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }//GEN-LAST:event_btnBayarActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        
        int selectedRow = tblKeranjang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin dihapus dari keranjang!", "Tidak Ada Item Dipilih", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
        model.removeRow(selectedRow);

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }

        updateTotalHarga();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengosongkan keranjang?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
            model.setRowCount(0);
            updateTotalHarga();
        }
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnNotifikasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotifikasiActionPerformed
        // TODO add your handling code here:
        
        Notications notifikasi_page = new Notications(usernameForPage);
        notifikasi_page.setVisible(true);
    }//GEN-LAST:event_btnNotifikasiActionPerformed

    private void btnTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionsActionPerformed
        // TODO add your handling code here:

        Transactions transactions_page = new Transactions(usernameForPage, levelForPage);
        transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTransactionsActionPerformed

    private void btnSalesTransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesTransactionsActionPerformed
        // TODO add your handling code here:

        SalesTransactions sales_transactions_page = new SalesTransactions(usernameForPage, levelForPage);
        sales_transactions_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSalesTransactionsActionPerformed

    private void btnPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengeluaranActionPerformed
        // TODO add your handling code here:

        Pengeluaran pengeluaran_page = new Pengeluaran(usernameForPage, levelForPage);
        pengeluaran_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPengeluaranActionPerformed

    private void btnCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriesActionPerformed
        // TODO add your handling code here:

        Categories categories_page = new Categories(usernameForPage, levelForPage);
        categories_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCategoriesActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:

        Products products_page = new Products(usernameForPage, levelForPage);
        products_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        // TODO add your handling code here:

        Dashboard dashboard_page = new Dashboard(usernameForPage, levelForPage);
        dashboard_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnBrandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrandsActionPerformed
        // TODO add your handling code here:

        Brands brands_page = new Brands(usernameForPage, levelForPage);
        brands_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBrandsActionPerformed

    private void btnSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuppliersActionPerformed
        // TODO add your handling code here:

        Suppliers suppliers_page = new Suppliers(usernameForPage, levelForPage);
        suppliers_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnSuppliersActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        // TODO add your handling code here:

        Users users_page = new Users(usernameForPage, levelForPage);
        users_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnUsersActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:

        Laporan laporan_page = new Laporan(usernameForPage, levelForPage);
        laporan_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCashierActionPerformed
        // TODO add your handling code here:

        Cashier cashier_page = new Cashier(usernameForPage, levelForPage);
        cashier_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCashierActionPerformed

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
            java.util.logging.Logger.getLogger(BarangMasukForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BarangMasukForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BarangMasukForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BarangMasukForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            new BarangMasukForm(null, "", "", -1, "", "", "").setVisible(true);
        }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCashier;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNotifikasi;
    private javax.swing.JButton btnPengeluaran;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnReset;
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
    private javax.swing.JLabel txtAlamat;
    private javax.swing.JLabel txtKontak;
    private javax.swing.JLabel txtLevelForPage;
    private javax.swing.JLabel txtNamaSupplier;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JTextField txtScanCodeProduct;
    private javax.swing.JTextField txtTotalDibayar;
    private javax.swing.JTextField txtTotalHarga;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
