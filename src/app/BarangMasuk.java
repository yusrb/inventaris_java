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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author yusrb
 */
public class BarangMasuk extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    
    public BarangMasuk(String username, String level) {
        initComponents();
        
        javax.swing.table.JTableHeader header = tblTampilBarangMasuk.getTableHeader();
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

        conn = DBConnection.getConnection();
        Fetch();
        setAlertStock();
        
        getSettings();
        
        usernameForPage = username;
        levelForPage = level;
        
        batasiAkses();
        txtUsernameForPage.setText(usernameForPage);
        
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setFocusable(true);
        this.requestFocusInWindow(true);
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
    
    public void Fetch() {
        try {
            pst = conn.prepareStatement(
                "SELECT p.id, p.tanggal, s.nama_supplier, p.total_item, p.total_harga, p.status " +
                "FROM barang_masuk p " +
                "JOIN suppliers s ON p.supplier_id = s.id"
            );
            rslt = pst.executeQuery();

            String[] columnNames = { "ID", "Tanggal", "Supplier", "Total Item", "Total Harga", "Status" };

            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    switch (columnIndex) {
                        case 0: return Integer.class;
                        case 1: return java.util.Date.class;
                        case 3: return Integer.class;
                        case 4: return Integer.class;
                        default: return String.class;
                    }
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            while (rslt.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rslt.getInt("id"));
                row.add(rslt.getTimestamp("tanggal"));
                row.add(rslt.getString("nama_supplier"));
                row.add(rslt.getInt("total_item"));
                row.add(rslt.getInt("total_harga"));
                row.add(rslt.getString("status"));
                model.addRow(row);
            }

            tblTampilBarangMasuk.setModel(model);

            TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tblTampilBarangMasuk.setRowSorter(sorter);

            DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
            leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

            for (int i = 0; i < tblTampilBarangMasuk.getColumnCount(); i++) {
                tblTampilBarangMasuk.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            }

        } catch (SQLException ex) {
            Logger.getLogger(BarangMasuk.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(BarangMasuk.class.getName()).log(Level.SEVERE, null, ex);
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
        btnLogout = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        txtNamePageTop = new javax.swing.JLabel();
        labelLogo = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTampilBarangMasuk = new javax.swing.JTable();
        btnCetak = new javax.swing.JButton();
        btnNotifikasi = new javax.swing.JButton();
        btnDetail = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Barang Masuk Page");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

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
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1069, Short.MAX_VALUE)
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

    jLabel3.setFont(new java.awt.Font("Palatino Linotype", 1, 18)); // NOI18N
    jLabel3.setText("Barang Masuk List");

    btnCreate.setBackground(new java.awt.Color(0, 255, 0));
    btnCreate.setForeground(new java.awt.Color(255, 255, 255));
    btnCreate.setText("Transaksi Barang Masuk Baru");
    btnCreate.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnCreateActionPerformed(evt);
        }
    });

    btnUpdate.setBackground(new java.awt.Color(102, 102, 255));
    btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
    btnUpdate.setText("Update");
    btnUpdate.setToolTipText("Update untuk mengubah status jadi dibatalkan");
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

    tblTampilBarangMasuk.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null}
        },
        new String [] {
            "id", "tanggal", "nama_supplier", "total_item", "total_harga", "status"
        }
    ) {
        Class[] types = new Class [] {
            java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
        };

        public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
        }
    });
    jScrollPane1.setViewportView(tblTampilBarangMasuk);

    btnCetak.setBackground(new java.awt.Color(102, 102, 102));
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

    btnDetail.setBackground(new java.awt.Color(153, 153, 255));
    btnDetail.setForeground(new java.awt.Color(255, 255, 255));
    btnDetail.setText("Detail");
    btnDetail.setBorderPainted(false);
    btnDetail.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnDetailActionPerformed(evt);
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnNotifikasi))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDetail)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(38, Short.MAX_VALUE))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createSequentialGroup()
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(32, 32, 32)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel3)
                .addComponent(btnNotifikasi, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(14, 14, 14)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnDetail))
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

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        
        SupplierList suppliers_page = new SupplierList(this, this, usernameForPage, levelForPage, null);
        suppliers_page.setVisible(true);
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        
        int pilihBaris = tblTampilBarangMasuk.getSelectedRow();

        if (pilihBaris == -1) {
            JOptionPane.showMessageDialog(this, "Barang Masuk Tidak Ditemukan\nPilih Barang Masuk Dulu", "Barang Masuk Tidak Ditemukan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int barangMasukId = (int) tblTampilBarangMasuk.getValueAt(pilihBaris, 0);

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin membatalkan barang masuk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        try {
            conn.setAutoCommit(false);

            pst = conn.prepareStatement("SELECT kode_barang, jumlah FROM detail_barang_masuk WHERE barang_masuk_id = ?");
            pst.setInt(1, barangMasukId);
            rslt = pst.executeQuery();

            PreparedStatement pstUpdateStok = conn.prepareStatement(
                "UPDATE products SET stok = stok - ? WHERE kode_barang = ?"
            );

            while (rslt.next()) {
                String kodeBarang = rslt.getString("kode_barang");
                int jumlah = rslt.getInt("jumlah");

                pstUpdateStok.setInt(1, jumlah);
                pstUpdateStok.setString(2, kodeBarang);
                pstUpdateStok.addBatch();
            }

            pstUpdateStok.executeBatch();

            pst = conn.prepareStatement("UPDATE barang_masuk SET status = 'dibatalkan' WHERE id = ?");
            pst.setInt(1, barangMasukId);
            pst.executeUpdate();

            conn.commit();

            JOptionPane.showMessageDialog(this, "Barang masuk berhasil dibatalkan dan stok dikembalikan.");
            Fetch();

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal membatalkan barang masuk: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
            try {
                int selectedRow = tblTampilBarangMasuk.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Pilih data barang masuk yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int barangMasukId = (int) tblTampilBarangMasuk.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data barang masuk ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                conn.setAutoCommit(false);

                pst = conn.prepareStatement("DELETE FROM detail_barang_masuk WHERE barang_masuk_id = ?");
                pst.setInt(1, barangMasukId);
                pst.executeUpdate();

                pst = conn.prepareStatement("DELETE FROM barang_masuk WHERE id = ?");
                pst.setInt(1, barangMasukId);
                int k = pst.executeUpdate();

                conn.commit();

                if (k == 1) {
                    JOptionPane.showMessageDialog(this, "Data barang masuk berhasil dihapus.");
                    Fetch();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data barang masuk.");
                }

            } catch (SQLException ex) {
                try { conn.rollback(); } catch (SQLException e2) { e2.printStackTrace(); }
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
        // TODO add your handling code here:
        
            try {
                StringBuilder html = new StringBuilder();
                html.append("<html><head>");
                html.append("<style>")
                    .append("body { font-family: sans-serif; margin: 30px; }")
                    .append("table { width: 100%; border-collapse: collapse; }")
                    .append("th, td { border: 1px solid #444; padding: 8px; text-align: left; }")
                    .append("th { background-color: #f2f2f2; }")
                    .append("h2 { text-align: center; }")
                    .append(".footer { margin-top: 20px; font-size: 12px; }")
                    .append("</style>");
                html.append("</head><body>");
                html.append("<h2>LAPORAN DATA BARANG MASUK</h2>");
                html.append("<table>");

                html.append("<tr>");
                for (int i = 0; i < tblTampilBarangMasuk.getColumnCount(); i++) {
                    html.append("<th>").append(tblTampilBarangMasuk.getColumnName(i)).append("</th>");
                }
                html.append("</tr>");

                for (int row = 0; row < tblTampilBarangMasuk.getRowCount(); row++) {
                    html.append("<tr>");
                    for (int col = 0; col < tblTampilBarangMasuk.getColumnCount(); col++) {
                        Object value = tblTampilBarangMasuk.getValueAt(row, col);
                        html.append("<td>").append(value == null ? "" : value.toString()).append("</td>");
                    }
                    html.append("</tr>");
                }

                String tanggalCetak = new java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm").format(new java.util.Date());
                html.append("</table>");
                html.append("<div class='footer'>Dicetak pada: ").append(tanggalCetak).append("</div>");
                html.append("</body></html>");

                JEditorPane editorPane = new JEditorPane("text/html", html.toString());
                editorPane.setEditable(false);
                boolean complete = editorPane.print();

                if (complete) {
                    JOptionPane.showMessageDialog(this, "Laporan berhasil dicetak.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan.", "Print Cancelled", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mencetak:\n" + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
    }//GEN-LAST:event_btnCetakActionPerformed

    private void btnNotifikasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNotifikasiActionPerformed
        // TODO add your handling code here:

        Notications notifikasi_page = new Notications(usernameForPage);
        notifikasi_page.setVisible(true);
    }//GEN-LAST:event_btnNotifikasiActionPerformed

    private void btnDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetailActionPerformed
        // TODO add your handling code here:
        
        int selectedRow = tblTampilBarangMasuk.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih data Barang Masuk terlebih dahulu!", "Tidak ada data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int barangMasukId = (int) tblTampilBarangMasuk.getValueAt(selectedRow, 0);

        DetailBarangMasuk detailFrame = new DetailBarangMasuk(usernameForPage, levelForPage, barangMasukId);
        detailFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDetailActionPerformed

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

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
        
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_P)
        {
            btnCetak.doClick();
        }
    }//GEN-LAST:event_formKeyPressed

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
            java.util.logging.Logger.getLogger(BarangMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BarangMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BarangMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BarangMasuk.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BarangMasuk("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCashier;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDetail;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNotifikasi;
    private javax.swing.JButton btnPengeluaran;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSalesTransactions;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JTable tblTampilBarangMasuk;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
