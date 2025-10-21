package UI;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManQuanLiNhanVien extends JPanel {

    private static final Color BLUE_PRIMARY   = new Color(47, 107, 255);
    private static final Color BLUE_SOFT      = new Color(230, 240, 255);
    private static final Color BORDER_SOFT    = new Color(200, 220, 255);
    private static final Color TEXT_DARK      = new Color(30, 35, 45);
    private static final Color TABLE_HEADER_BG= new Color(27, 38, 77);

    private final JTextField txtMaNV   = new JTextField();
    private final JTextField txtTenNV  = new JTextField();
    private final JDateChooser dcNgaySinh = new JDateChooser();
    private final JTextField txtSDT    = new JTextField();
    private final JTextField txtEmail  = new JTextField();
    private final JComboBox<String> cboLoaiNV = new JComboBox<>(
            new String[]{"Quản trị", "Nhân viên bán vé"}
    );

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                    "Mã nhân viên", "Tên nhân viên", "Ngày sinh",
                    "Số điện thoại", "Email", "Loại nhân viên",
                    "cccd", "Ngày bắt đầu công việc"
            }, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final JButton btnThem = new JButton("Thêm");
    private final JButton btnSua  = new JButton("Cật nhật");
    private final JButton btnXoa  = new JButton("Xóa");

    public ManQuanLiNhanVien() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel leftForm = buildFormPanel();
        JPanel rightTable = buildTablePanel();

        // Cố định layout, thêm padding cho bảng
        JPanel leftHolder = new JPanel(new BorderLayout());
        leftHolder.setOpaque(false);
        leftForm.setPreferredSize(new Dimension(520, 0));
        leftForm.setMinimumSize(new Dimension(520, 0));
        leftForm.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));
        leftHolder.setPreferredSize(new Dimension(520, 0));
        leftHolder.add(leftForm, BorderLayout.CENTER);

        add(leftHolder, BorderLayout.WEST);
        add(rightTable, BorderLayout.CENTER);

        wireEvents();
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_SOFT),
                "THÔNG TIN NHÂN VIÊN",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)
        ));

        GridBagConstraints l = new GridBagConstraints();
        l.insets = new Insets(18, 14, 12, 8);
        l.anchor = GridBagConstraints.EAST;
        l.gridx = 0; l.gridy = 0;

        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(18, 0, 12, 14);
        f.fill = GridBagConstraints.HORIZONTAL;
        f.weightx = 1;
        f.gridx = 1; f.gridy = 0;

        var labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        java.util.function.Function<String, JLabel> L = (text) -> {
            JLabel lb = new JLabel(text, SwingConstants.RIGHT);
            lb.setFont(labelFont);
            lb.setForeground(TEXT_DARK);
            lb.setPreferredSize(new Dimension(130, 28));
            return lb;
        };
        java.util.function.Consumer<JComponent> styleField = (comp) -> {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            comp.setPreferredSize(new Dimension(420, 34));
            comp.setMinimumSize(new Dimension(380, 34));
            comp.setBackground(Color.WHITE);
            comp.setBorder(new CompoundBorder(new LineBorder(BORDER_SOFT), new EmptyBorder(6, 8, 6, 8)));
        };

        cboLoaiNV.setUI(new BasicComboBoxUI());
        dcNgaySinh.setDateFormatString("dd/MM/yyyy");
        dcNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dcNgaySinh.setOpaque(true);

        JTextFieldDateEditor editor = (JTextFieldDateEditor) dcNgaySinh.getDateEditor();
        editor.setEditable(true);
        editor.setColumns(20);
        Dimension fieldSize = new Dimension(420, 34);
        dcNgaySinh.setPreferredSize(fieldSize);
        dcNgaySinh.setMinimumSize(new Dimension(380, 34));
        int calBtnW = 34;
        editor.setPreferredSize(new Dimension(fieldSize.width - calBtnW, fieldSize.height));
        editor.setBorder(new CompoundBorder(new LineBorder(BORDER_SOFT), new EmptyBorder(6, 8, 6, 8)));

        JButton calBtn = dcNgaySinh.getCalendarButton();
        calBtn.setText("...");
        calBtn.setPreferredSize(new Dimension(calBtnW, fieldSize.height));

        p.add(L.apply("Mã nhân viên"), l); p.add(txtMaNV, f); styleField.accept(txtMaNV);
        l.gridy++; f.gridy++;
        p.add(L.apply("Tên nhân viên"), l); p.add(txtTenNV, f); styleField.accept(txtTenNV);
        l.gridy++; f.gridy++;
        p.add(L.apply("Ngày sinh"), l); p.add(dcNgaySinh, f);
        l.gridy++; f.gridy++;
        p.add(L.apply("Số điện thoại"), l); p.add(txtSDT, f); styleField.accept(txtSDT);
        l.gridy++; f.gridy++;
        p.add(L.apply("Email"), l); p.add(txtEmail, f); styleField.accept(txtEmail);
        l.gridy++; f.gridy++;
        p.add(L.apply("Loại nhân viên"), l); p.add(cboLoaiNV, f); styleField.accept(cboLoaiNV);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        btnBar.setOpaque(false);
        for (JButton b : new JButton[]{btnThem, btnSua, btnXoa}) {
            styleButton(b);
            btnBar.add(b);
        }
        GridBagConstraints gBtn = new GridBagConstraints();
        gBtn.gridx = 0; gBtn.gridy = ++l.gridy; gBtn.gridwidth = 2;
        gBtn.insets = new Insets(30, 14, 30, 14);
        gBtn.fill = GridBagConstraints.HORIZONTAL;
        p.add(btnBar, gBtn);

        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_SOFT),
                "DANH SÁCH NHÂN VIÊN",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), new Color(70, 130, 180)
        ));

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(new Color(235, 242, 255));
        table.setSelectionBackground(BLUE_SOFT);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setDefaultRenderer(new TableCellRenderer() {
            private final JLabel lbl = new JLabel();
            {
                lbl.setOpaque(true);
                lbl.setBackground(TABLE_HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new MatteBorder(0, 0, 1, 1, BORDER_SOFT));
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                lbl.setText(value == null ? "" : value.toString());
                return lbl;
            }
        });

        int[] widths = {110, 160, 110, 120, 180, 140, 130, 180};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }

        // Giữ viền mảnh cho bảng, nền trắng hoàn toàn (không xám)
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE); // nền trong bảng trắng
        scroll.setBackground(Color.WHITE);               // nền ngoài bảng trắng luôn
        scroll.setBorder(new CompoundBorder(
                new LineBorder(BORDER_SOFT),             // viền mảnh quanh bảng
                new EmptyBorder(10, 12, 10, 12)          // thêm padding nhẹ
        ));

        p.setBackground(Color.WHITE);                    // nền panel chứa bảng trắng
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }


    private void styleButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setForeground(BLUE_PRIMARY.darker());
        b.setBorder(new CompoundBorder(new LineBorder(BORDER_SOFT), new EmptyBorder(8, 25, 8, 25)));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(BLUE_SOFT); }
            @Override public void mouseExited (MouseEvent e) { b.setBackground(Color.WHITE); }
        });
    }

    private void wireEvents() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = table.getSelectedRow();
                if (r >= 0) {
                    int row = table.convertRowIndexToModel(r);
                    txtMaNV.setText(s(row, 0));
                    txtTenNV.setText(s(row, 1));
                    Date d = parseDate(s(row, 2));
                    dcNgaySinh.setDate(d);
                    txtSDT.setText(s(row, 3));
                    txtEmail.setText(s(row, 4));
                    cboLoaiNV.setSelectedItem(s(row, 5));
                }
            }
        });

        btnThem.addActionListener(e -> onThem());
        btnSua.addActionListener(e -> onSua());
        btnXoa.addActionListener(e -> onXoa());
    }

    private void onThem() {
        if (!validateForm()) return;
        Object[] row = new Object[]{
                txtMaNV.getText().trim(),
                txtTenNV.getText().trim(),
                fmt(dcNgaySinh.getDate()),
                txtSDT.getText().trim(),
                txtEmail.getText().trim(),
                cboLoaiNV.getSelectedItem(),
                "", ""
        };
        model.addRow(row);
        clearForm();
    }

    private void onSua() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để sửa"); return; }
        if (!validateForm()) return;
        int row = table.convertRowIndexToModel(r);
        model.setValueAt(txtMaNV.getText().trim(), row, 0);
        model.setValueAt(txtTenNV.getText().trim(), row, 1);
        model.setValueAt(fmt(dcNgaySinh.getDate()), row, 2);
        model.setValueAt(txtSDT.getText().trim(), row, 3);
        model.setValueAt(txtEmail.getText().trim(), row, 4);
        model.setValueAt(cboLoaiNV.getSelectedItem(), row, 5);
    }

    private void onXoa() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Chọn 1 dòng để xóa"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa nhân viên đã chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int row = table.convertRowIndexToModel(r);
            model.removeRow(row);
            clearForm();
        }
    }

    private void clearForm() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        dcNgaySinh.setDate(null);
        txtSDT.setText("");
        txtEmail.setText("");
        cboLoaiNV.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean validateForm() {
        if (txtMaNV.getText().isBlank()) { warn("Mã nhân viên không được trống"); return false; }
        if (txtTenNV.getText().isBlank()) { warn("Tên nhân viên không được trống"); return false; }
        if (dcNgaySinh.getDate() == null)   { warn("Vui lòng chọn ngày sinh"); return false; }
        if (txtSDT.getText().isBlank()) { warn("Số điện thoại không được trống"); return false; }
        if (txtEmail.getText().isBlank()) { warn("Email không được trống"); return false; }
        return true;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
    }

    private String s(int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String fmt(Date d) { return d == null ? "" : sdf.format(d); }
    private Date parseDate(String txt) {
        try { return txt == null || txt.isBlank() ? null : sdf.parse(txt); }
        catch (Exception e) { return null; }
    }
    
    //demo
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
//            JFrame f = new JFrame("Quản Lý Nhân Viên");
//            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//            f.setContentPane(new ManQuanLiNhanVien());
//            f.setSize(1200, 700);
//            f.setLocationRelativeTo(null);
//            f.setVisible(true);
//        });
//    }
}