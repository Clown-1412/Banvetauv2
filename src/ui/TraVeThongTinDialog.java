package ui;

import dao.TraVe_Dao;
import entity.TicketExchangeInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class TraVeThongTinDialog extends JDialog {
    private final TraVe_Dao traVeDao = new TraVe_Dao();
    private final JButton btnXacNhan = Ui.primary("Xác nhận");
    private final JButton btnDong = Ui.primary("Đóng");
    private Runnable onCancelled;

    public TraVeThongTinDialog(Window owner, TicketExchangeInfo info,
                               String giaVeText, String phiKhauTruText, String soTienHoanText,
                               BigDecimal feeRate, String thoiGianKhoiHanh, Runnable onCancelled) {
        super(owner, "Trả vé", ModalityType.APPLICATION_MODAL);
        this.onCancelled = onCancelled;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(0xF5F7FB));

        container.add(Ui.banner("XÁC NHẬN TRẢ VÉ"), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 16, 16, 16));

        body.add(Ui.card(buildTicketInfo(info, thoiGianKhoiHanh), "THÔNG TIN VÉ"));
        body.add(Box.createVerticalStrut(16));
        body.add(Ui.card(buildRefundInfo(giaVeText, phiKhauTruText, soTienHoanText, feeRate), "THÔNG TIN HOÀN TIỀN"));

        container.add(body, BorderLayout.CENTER);
        container.add(buildActions(info), BorderLayout.SOUTH);

        setContentPane(container);
        pack();
        setLocationRelativeTo(owner);
    }

    private JComponent buildTicketInfo(TicketExchangeInfo info, String thoiGianKhoiHanh) {
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);

        grid.add(makeLabel("Mã vé:"));
        grid.add(makeValue(safe(info.getMaVe())));

        grid.add(makeLabel("Hành khách:"));
        grid.add(makeValue(safe(info.getHoTen())));

        grid.add(makeLabel("Số điện thoại:"));
        grid.add(makeValue(safe(info.getSoDienThoai())));

        grid.add(makeLabel("Tuyến:"));
        grid.add(makeValue(safe(info.getGaDi()) + " → " + safe(info.getGaDen())));

        grid.add(makeLabel("Khởi hành:"));
        grid.add(makeValue(thoiGianKhoiHanh));

        grid.add(makeLabel("Tàu/Toa/Ghế:"));
        grid.add(makeValue("Tàu " + safe(info.getTenTau()) + ", Toa " + info.getSoToa() + ", Ghế " + safe(info.getSoGhe())));

        grid.add(makeLabel("Khoang:"));
        grid.add(makeValue(safe(info.getTenKhoang())));

        grid.add(makeLabel("Loại vé:"));
        grid.add(makeValue(safe(info.getTenLoaiVe())));

        return grid;
    }

    private JComponent buildRefundInfo(String giaVeText, String phiKhauTruText,
                                       String soTienHoanText, BigDecimal feeRate) {
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
        grid.setOpaque(false);

        grid.add(makeLabel("Giá vé gốc:"));
        grid.add(makeValue(giaVeText + " đ"));

        grid.add(makeLabel("Mức khấu trừ:"));
        grid.add(makeValue(formatPercent(feeRate)));

        grid.add(makeLabel("Phí khấu trừ:"));
        grid.add(makeValue(phiKhauTruText + " đ"));

        JLabel hoanLabel = makeLabel("Số tiền hoàn lại:");
        hoanLabel.setFont(hoanLabel.getFont().deriveFont(Font.BOLD));
        grid.add(hoanLabel);

        JLabel hoanValue = makeValue(soTienHoanText + " đ");
        hoanValue.setFont(hoanValue.getFont().deriveFont(Font.BOLD, 16f));
        hoanValue.setForeground(new Color(0x0B8043));
        grid.add(hoanValue);

        return grid;
    }

    private JPanel buildActions(TicketExchangeInfo info) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        actions.setOpaque(false);

        btnDong.addActionListener(e -> dispose());
        btnXacNhan.addActionListener(e -> handleConfirm(info));

        actions.add(btnDong);
        actions.add(btnXacNhan);
        return actions;
    }

    private void handleConfirm(TicketExchangeInfo info) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Xác nhận hoàn tiền và hủy vé " + info.getMaVe() + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        btnXacNhan.setEnabled(false);
        try {
            boolean updated = traVeDao.cancelTicket(info.getMaVe());
            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Đã cập nhật trạng thái vé sang 'Đã hủy'.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (onCancelled != null) {
                    onCancelled.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể cập nhật trạng thái vé. Vui lòng kiểm tra lại.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                btnXacNhan.setEnabled(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể cập nhật trạng thái vé: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            btnXacNhan.setEnabled(true);
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lb = new JLabel(text);
        lb.setForeground(new Color(0x1F2937));
        return lb;
    }

    private JLabel makeValue(String text) {
        JLabel lb = new JLabel(text);
        lb.setForeground(new Color(0x111827));
        lb.setFont(lb.getFont().deriveFont(Font.BOLD));
        return lb;
    }

    private String formatPercent(BigDecimal rate) {
        BigDecimal percent = rate.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP);
        return percent.toPlainString() + "%";
    }

    private String safe(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }
}