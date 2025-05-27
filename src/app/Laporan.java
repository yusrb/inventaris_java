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
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author yusrb
 */public class Laporan extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    
    private final String usernameForPage;
    private final String levelForPage;
    
    private int currentYear;
    private int currentMonth;

    public Laporan(String username, String level) {
        initComponents();

        javax.swing.table.JTableHeader headerBM = tblTampilLaporan.getTableHeader();
        headerBM.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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
        getSettings();

        usernameForPage = username;
        levelForPage = level;
        
        batasiAkses();
        txtUsernameForPage.setText(usernameForPage);
        txtLevelForPage.setText(levelForPage);

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

        tblTampilLaporan.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {"ID", "Tanggal", "Penjualan", "Pembelian", "Pengeluaran", "Pendapatan"}
        ));

        Calendar now = Calendar.getInstance();
        currentYear = now.get(Calendar.YEAR);
        currentMonth = now.get(Calendar.MONTH) + 1;
        Fetch(currentYear, currentMonth);
    }

    public void Fetch(int year, int month) {
        DefaultTableModel model = (DefaultTableModel) tblTampilLaporan.getModel();
        model.setRowCount(0);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, month - 1);
        start.set(Calendar.DAY_OF_MONTH, 1);

        Calendar end = (Calendar) start.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", new java.util.Locale("id", "ID"));

        double totalPendapatan = 0;
        int nomor = 1;

        while (!start.after(end)) {
            String tanggal = sdf.format(start.getTime());

            double penjualan = 0;
            double pembelian = 0;
            double pengeluaran = 0;

            try {
                pst = conn.prepareStatement("SELECT SUM(total_harga) AS total FROM transaksi WHERE DATE(tanggal) = ?");
                pst.setString(1, tanggal);
                rslt = pst.executeQuery();
                if (rslt.next()) penjualan = rslt.getDouble("total");

                pst = conn.prepareStatement("SELECT SUM(total_harga) AS total FROM barang_masuk WHERE DATE(tanggal) = ?");
                pst.setString(1, tanggal);
                rslt = pst.executeQuery();
                if (rslt.next()) pembelian = rslt.getDouble("total");


                pst = conn.prepareStatement("SELECT SUM(nominal) AS total FROM pengeluaran_operasional WHERE tanggal = ?");
                pst.setString(1, tanggal);
                rslt = pst.executeQuery();
                if (rslt.next()) pengeluaran = rslt.getDouble("total");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mengambil data tanggal " + tanggal + ": " + ex.getMessage());
            }

            double pendapatan = penjualan - (pembelian + pengeluaran);
            totalPendapatan += pendapatan;

            model.addRow(new Object[]{
                nomor++,
                displayFormat.format(start.getTime()),
                formatCurrency(penjualan),
                formatCurrency(pembelian),
                formatCurrency(pengeluaran),
                formatCurrency(pendapatan)
            });

            start.add(Calendar.DATE, 1);
        }

        model.addRow(new Object[]{"", "", "", "", "Total Pendapatan", formatCurrency(totalPendapatan)});

        JTableHeader header = tblTampilLaporan.getTableHeader();
        TableColumnModel colModel = header.getColumnModel();
        TableColumn tanggalColumn = colModel.getColumn(1);

        String headerText = "Tanggal (" +
                new SimpleDateFormat("MMMM yyyy", new java.util.Locale("id", "ID"))
                .format(new GregorianCalendar(year, month - 1, 1).getTime()) + ")";

        tanggalColumn.setHeaderValue(headerText);
        header.repaint();
    }

    // Format rupiah
    private String formatCurrency(double value) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        return formatter.format(value);
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
    
    private void cetakLaporan() {
        SimpleDateFormat sdfTgl = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        String tAwal = sdfTgl.format(dtanggal_awal.getDate());
        String tAkhir = sdfTgl.format(dtanggal_akhir.getDate());

        StringBuilder html = new StringBuilder();
        html.append("<html><head>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; font-size: 9px; margin: 0; padding: 0; }")
                .append("h2, h3 { text-align: center; margin: 2px 0; }")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 5px; }")
                .append("th, td { border: 1px solid #000; padding: 4px; font-size: 8px; }")
                .append("th { background-color: #f2f2f2; }")
                .append("p.footer { margin-top: 20px; text-align: right; font-style: italic; font-size: 8px; }")
                .append("</style>")
                .append("</head><body>");

        html.append("<h2>LAPORAN REKAP KEUANGAN HARIAN</h2>");
        html.append("<h3>Periode: ").append(tAwal).append(" s/d ").append(tAkhir).append("</h3>");
        html.append("<hr>");

        html.append("<table>");
        html.append("<tr>")
                .append("<th>No</th>")
                .append("<th>Tanggal</th>")
                .append("<th>Penjualan</th>")
                .append("<th>Pembelian</th>")
                .append("<th>Pengeluaran</th>")
                .append("<th>Pendapatan Bersih</th>")
                .append("</tr>");

        DefaultTableModel model = (DefaultTableModel) tblTampilLaporan.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String no = model.getValueAt(i, 0).toString();
            String tanggal = model.getValueAt(i, 1).toString();
            String penjualan = model.getValueAt(i, 2).toString();
            String pembelian = model.getValueAt(i, 3).toString();
            String pengeluaran = model.getValueAt(i, 4).toString();
            String pendapatan = model.getValueAt(i, 5).toString();

            if (no.isEmpty()) {
                html.append("<tr>")
                        .append("<td colspan='5' align='right'><strong>").append(pembelian).append("</strong></td>")
                        .append("<td align='right'><strong>").append(pendapatan).append("</strong></td>")
                        .append("</tr>");
            } else {
                html.append("<tr>")
                        .append("<td align='center'>").append(no).append("</td>")
                        .append("<td>").append(tanggal).append("</td>")
                        .append("<td align='right'>").append(penjualan).append("</td>")
                        .append("<td align='right'>").append(pembelian).append("</td>")
                        .append("<td align='right'>").append(pengeluaran).append("</td>")
                        .append("<td align='right'>").append(pendapatan).append("</td>")
                        .append("</tr>");
            }
        }

        html.append("</table>");

        html.append("<p class='footer'>Dicetak pada: ")
                .append(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()))
                .append("</p>");

        html.append("</body></html>");

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(html.toString());

        try {
            boolean done = textPane.print();
            if (done) {
                JOptionPane.showMessageDialog(this, "Laporan berhasil dicetak!");
            } else {
                JOptionPane.showMessageDialog(this, "Pencetakan dibatalkan.");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
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
        btnUsers = new javax.swing.JButton();
        txtNamePageBottom = new javax.swing.JLabel();
        btnSuppliers = new javax.swing.JButton();
        btnBrands = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnCategories = new javax.swing.JButton();
        btnCashier = new javax.swing.JButton();
        btnSalesTransactions = new javax.swing.JButton();
        btnPengeluaran = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
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
        tblTampilLaporan = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        btnTampil = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnPrevious = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Laporan Page");

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

    btnReports.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
    btnReports.setForeground(new java.awt.Color(255, 255, 255));
    btnReports.setText("Reports");
    btnReports.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2
        ), 
        javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    btnReports.setContentAreaFilled(false);
    btnReports.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    btnReports.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnReportsActionPerformed(evt);
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
            .addComponent(txtNamePageBottom)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addContainerGap(159, Short.MAX_VALUE))
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

    txtNamePageTop.setFont(new java.awt.Font("Palatino Linotype", 1, 40)); // NOI18N
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
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    tblTampilLaporan.setModel(new javax.swing.table.DefaultTableModel(
        new Object [][] {
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null},
            {null, null, null, null, null, null}
        },
        new String [] {
            "id", "tanggal", "penjualan", "pembelian", "pengeluaran", "pendapatan"
        }
    ));
    jScrollPane1.setViewportView(tblTampilLaporan);

    jLabel5.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
    jLabel5.setText("Laporan");

    btnTampil.setBackground(new java.awt.Color(51, 255, 0));
    btnTampil.setForeground(new java.awt.Color(255, 255, 255));
    btnTampil.setText("Tampil");
    btnTampil.setBorderPainted(false);
    btnTampil.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnTampilActionPerformed(evt);
        }
    });

    btnClear.setBackground(new java.awt.Color(102, 102, 102));
    btnClear.setForeground(new java.awt.Color(255, 255, 255));
    btnClear.setText("Reset");
    btnClear.setBorderPainted(false);
    btnClear.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnClearActionPerformed(evt);
        }
    });

    btnNext.setText(">");
    btnNext.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnNextActionPerformed(evt);
        }
    });

    btnPrevious.setText("<");
    btnPrevious.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnPreviousActionPerformed(evt);
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
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1)
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
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                            .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNext)
                    .addComponent(btnPrevious)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        
        cetakLaporan();
    }//GEN-LAST:event_btnCetakActionPerformed

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

    private void btnTampilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTampilActionPerformed
        // TODO add your handling code here:

    Date dateAwal = dtanggal_awal.getDate();
    Date dateAkhir = dtanggal_akhir.getDate();

    if (dateAwal == null || dateAkhir == null) {
        JOptionPane.showMessageDialog(this, "Silakan pilih tanggal awal dan akhir.");
        return;
    }

    DefaultTableModel model = (DefaultTableModel) tblTampilLaporan.getModel();
    model.setRowCount(0);

    Calendar start = Calendar.getInstance();
    start.setTime(dateAwal);

    Calendar end = Calendar.getInstance();
    end.setTime(dateAkhir);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", new java.util.Locale("id", "ID"));

    double totalPendapatan = 0;
    int nomor = 1;

    while (!start.after(end)) {
        String tanggal = sdf.format(start.getTime());

        double penjualan = 0;
        double pembelian = 0;
        double pengeluaran = 0;

        try {
            pst = conn.prepareStatement("SELECT SUM(total_harga) AS total FROM transaksi WHERE DATE(tanggal) = ?");
            pst.setString(1, tanggal);
            rslt = pst.executeQuery();
            if (rslt.next()) penjualan = rslt.getDouble("total");

            pst = conn.prepareStatement("SELECT SUM(total_harga) AS total FROM barang_masuk WHERE DATE(tanggal) = ?");
            pst.setString(1, tanggal);
            rslt = pst.executeQuery();
            if (rslt.next()) pembelian = rslt.getDouble("total");

            pst = conn.prepareStatement("SELECT SUM(nominal) AS total FROM pengeluaran_operasional WHERE tanggal = ?");
            pst.setString(1, tanggal);
            rslt = pst.executeQuery();
            if (rslt.next()) pengeluaran = rslt.getDouble("total");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data tanggal " + tanggal + ": " + ex.getMessage());
        }

        double pendapatan = penjualan - (pembelian + pengeluaran);
        totalPendapatan += pendapatan;

        model.addRow(new Object[]{
            nomor++,
            displayFormat.format(start.getTime()),
            formatCurrency(penjualan),
            formatCurrency(pembelian),
            formatCurrency(pengeluaran),
            formatCurrency(pendapatan)
        });

        start.add(Calendar.DATE, 1);
    }

    model.addRow(new Object[]{"", "", "", "", "Total Pendapatan", formatCurrency(totalPendapatan)});

    JTableHeader header = tblTampilLaporan.getTableHeader();
    TableColumnModel colModel = header.getColumnModel();
    TableColumn tanggalColumn = colModel.getColumn(1);

    String headerText = "Tanggal (" +
        new SimpleDateFormat("dd MMM yyyy", new java.util.Locale("id", "ID")).format(dateAwal) + " - " +
        new SimpleDateFormat("dd MMM yyyy", new java.util.Locale("id", "ID")).format(dateAkhir) + ")";

    tanggalColumn.setHeaderValue(headerText);
    header.repaint();
    }//GEN-LAST:event_btnTampilActionPerformed

    private void btnPengeluaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPengeluaranActionPerformed
        // TODO add your handling code here:

        Pengeluaran pengeluaran_page = new Pengeluaran(usernameForPage, levelForPage);
        pengeluaran_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPengeluaranActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:
        
        Laporan laporan_page = new Laporan(usernameForPage, levelForPage);
        laporan_page.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPreviousActionPerformed
        // TODO add your handling code here:
        
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }
        Fetch(currentYear, currentMonth);
    }//GEN-LAST:event_btnPreviousActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        
        currentMonth--;
        if (currentMonth < 1) {
            currentMonth = 12;
            currentYear--;
        }
        Fetch(currentYear, currentMonth);
    }//GEN-LAST:event_btnNextActionPerformed

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
            java.util.logging.Logger.getLogger(Laporan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Laporan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Laporan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Laporan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new Laporan("Username", "").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrands;
    private javax.swing.JButton btnCashier;
    private javax.swing.JButton btnCategories;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPengeluaran;
    private javax.swing.JButton btnPrevious;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSalesTransactions;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnSuppliers;
    private javax.swing.JButton btnTampil;
    private javax.swing.JButton btnTransactions;
    private javax.swing.JButton btnUsers;
    private com.toedter.calendar.JDateChooser dtanggal_akhir;
    private com.toedter.calendar.JDateChooser dtanggal_awal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelLogo;
    private javax.swing.JTable tblTampilLaporan;
    private javax.swing.JLabel txtLevelForPage;
    private javax.swing.JLabel txtNamePageBottom;
    private javax.swing.JLabel txtNamePageTop;
    private javax.swing.JLabel txtUsernameForPage;
    // End of variables declaration//GEN-END:variables
}
