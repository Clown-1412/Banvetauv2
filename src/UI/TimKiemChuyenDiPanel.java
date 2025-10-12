package UI;

import dao.ChuyenDi_Dao;
import model.ChuyenDi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Giao diện tra cứu chuyến tàu với bộ lọc và bảng kết quả.
 */
public class TimKiemChuyenDiPanel extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ChuyenDi_Dao dao = new ChuyenDi_Dao();
    private final JTextField txtMaChuyenDi = new JTextField();
    private final JComboBox<String> cboGaDi = new JComboBox<>();
    private final JComboBox<String> cboGaDen = new JComboBox<>();
    private final JCheckBox chkKhoiHanhTu = new JCheckBox("Từ");
    private final JCheckBox chkKhoiHanhDen = new JCheckBox("Đến");
    private final JSpinner spKhoiHanhTu = makeDateSpinner();
    private final JSpinner spKhoiHanhDen = makeDateSpinner();
    private final JButton btnTim = new JButton("Tìm chuyến đi");
    private final JButton btnLamMoi = new JButton("Làm mới");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{
                    "Mã chuyến đi",
                    "Ga đi",
                    "Ga đến",
                    "Thời gian khởi hành",
                    "Thời gian kết thúc",
                    "Tên tàu",
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblKetQua = new JTable(tableModel);

    public TimKiemChuyenDiPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadStations();
        performSearch();
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int col = 0;
        addFilter(panel, gbc, 0, col++, new JLabel("Mã chuyến đi:"), txtMaChuyenDi);
        addFilter(panel, gbc, 0, col++, new JLabel("Ga đi:"), cboGaDi);
        addFilter(panel, gbc, 0, col++, new JLabel("Ga đến:"), cboGaDen);
        addDateFilter(panel, gbc, 1, 0, chkKhoiHanhTu, spKhoiHanhTu);
        addDateFilter(panel, gbc, 1, 1, chkKhoiHanhDen, spKhoiHanhDen);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.add(btnLamMoi);
        actionPanel.add(btnTim);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(actionPanel, gbc);

        btnTim.addActionListener(this::onSearch);
        btnLamMoi.addActionListener(this::onReset);
        chkKhoiHanhTu.addActionListener(e -> spKhoiHanhTu.setEnabled(chkKhoiHanhTu.isSelected()));
        chkKhoiHanhDen.addActionListener(e -> spKhoiHanhDen.setEnabled(chkKhoiHanhDen.isSelected()));
        spKhoiHanhTu.setEnabled(false);
        spKhoiHanhDen.setEnabled(false);

        return panel;
    }

    private JPanel buildTablePanel() {
        tblKetQua.setRowHeight(26);
        tblKetQua.setAutoCreateRowSorter(true);
        tblKetQua.getTableHeader().setReorderingAllowed(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Kết quả tra cứu"));
        panel.add(new JScrollPane(tblKetQua), BorderLayout.CENTER);
        return panel;
    }

    private void onSearch(ActionEvent event) {
        performSearch();
    }

    private void onReset(ActionEvent event) {
        txtMaChuyenDi.setText("");
        if (cboGaDi.getItemCount() > 0) {
            cboGaDi.setSelectedIndex(0);
        }
        if (cboGaDen.getItemCount() > 0) {
            cboGaDen.setSelectedIndex(0);
        }
        chkKhoiHanhTu.setSelected(false);
        chkKhoiHanhDen.setSelected(false);
        spKhoiHanhTu.setValue(new Date());
        spKhoiHanhDen.setValue(new Date());
        spKhoiHanhTu.setEnabled(false);
        spKhoiHanhDen.setEnabled(false);
        performSearch();
    }

    private void performSearch() {
        Date from = chkKhoiHanhTu.isSelected() ? (Date) spKhoiHanhTu.getValue() : null;
        Date to = chkKhoiHanhDen.isSelected() ? (Date) spKhoiHanhDen.getValue() : null;

        try {
            List<ChuyenDi> data = dao.search(
                    txtMaChuyenDi.getText(),
                    (String) cboGaDi.getSelectedItem(),
                    (String) cboGaDen.getSelectedItem(),
                    from,
                    to
            );

            tableModel.setRowCount(0);
            for (ChuyenDi cd : data) {
                tableModel.addRow(new Object[]{
                        cd.getMaChuyenTau(),
                        safeString(cd.getGaDi()),
                        safeString(cd.getGaDen()),
                        formatTime(cd.getThoiGianKhoiHanh()),
                        formatTime(cd.getThoiGianKetThuc()),
                        safeString(cd.getTenTau())
                        
                });
            }
            tableModel.fireTableDataChanged();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải dữ liệu chuyến đi: " + ex.getMessage(),
                    "Lỗi cơ sở dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStations() {
        cboGaDi.removeAllItems();
        cboGaDen.removeAllItems();
        cboGaDi.addItem("Tất cả");
        cboGaDen.addItem("Tất cả");
        try {
            for (String ga : dao.getAllGaDi()) {
                cboGaDi.addItem(ga);
            }
            for (String ga : dao.getAllGaDen()) {
                cboGaDen.addItem(ga);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải danh sách ga: " + ex.getMessage(),
                    "Lỗi cơ sở dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JSpinner makeDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm"));
        return spinner;
    }

    private static void addFilter(JPanel panel, GridBagConstraints gbc, int row, int col, JComponent label, JComponent field) {
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private static void addDateFilter(JPanel panel, GridBagConstraints gbc, int row, int col, JCheckBox checkbox, JSpinner spinner) {
        gbc.gridx = col * 2;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(checkbox, gbc);

        gbc.gridx = col * 2 + 1;
        gbc.weightx = 1;
        panel.add(spinner, gbc);
    }

    private static String formatTime(LocalDateTime time) {
        return time != null ? DATE_TIME_FMT.format(time) : "";
    }

    private static String formatCurrency(BigDecimal value) {
        if (value == null) {
            return "";
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return nf.format(value);
    }

    private static String safeString(String value) {
        return value != null ? value : "";
    }
}