package ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.util.*;

public class ManChonGheNgoi extends JPanel {

    // ==== Màu dùng chung ====
    private static final Color BLUE_PRIMARY   = new Color(0x1976D2);
    private static final Color BLUE_LIGHT     = new Color(0xE3F2FD);
    private static final Color BLUE_BORDER    = new Color(0x90CAF9);
    private static final Color GREEN_PRIMARY  = new Color(0x2E7D32);
    private static final Color GREEN_SOFT     = new Color(0x43A047);
    private static final Color RED_PRIMARY    = new Color(0xE53935);
    private static final Color RED_SOFT       = new Color(0xEF9A9A);
    private static final Color SEAT_FREE      = new Color(0xBFE3FF);
    private static final Color SEAT_SELECTED  = new Color(0xFF6F61);
    private static final Color SEAT_SOLD      = new Color(0xBDBDBD);
    private static final Color COMPART_BAR    = new Color(0x214A7A); // thanh ngăn khoang (đậm)
    private static final Color TOA_BORDER     = new Color(0x205A9B); // viền toa

    // == Seat size tuning (nút nhỏ) ==
    private static final int   SEAT_W = 48;
    private static final int   SEAT_H = 44;
    private static final float SEAT_FONT_PT = 10.5f;
    private static final int   SEAT_GAP = 8;          // khoảng cách giữa các nút
    private static final int   SEAT_INNER_PAD = 1;    // đệm trong

    public ManChonGheNgoi() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(Color.WHITE);

        root.add(buildStepBar(), BorderLayout.NORTH);

        // === Center: cố định 70/30, không có thanh kéo ===
        JComponent left  = buildLeft();
        JComponent right = buildRight();

        left.setMinimumSize(new Dimension(0, 0));
        right.setMinimumSize(new Dimension(0, 0));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(new LineBorder(new Color(230,230,230)));
        GridBagConstraints cc = new GridBagConstraints();
        cc.fill = GridBagConstraints.BOTH;
        cc.gridy = 0;
        cc.weighty = 1.0;

        // Trái 70%
        cc.gridx = 0;
        cc.weightx = 0.70;
        center.add(left, cc);

        // Phải 30%
        cc.gridx = 1;
        cc.weightx = 0.30;
        center.add(right, cc);

        root.add(center, BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    // ---------------- Step Bar ----------------
    private JComponent buildStepBar() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 8);

        p.add(stepButton("1", "CHỌN CHUYẾN", false), gbc);
        p.add(stepButton("2", "CHI TIẾT VÉ", true), gbc);
        p.add(stepButton("3", "THANH TOÁN", false), gbc);

        return p;
    }

    private JComponent stepButton(String index, String text, boolean active) {
        JButton b = new JButton(index + "  " + text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(Font.BOLD, 14f));
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setEnabled(false);
        if (active) {
            b.setBackground(BLUE_PRIMARY);
            b.setForeground(Color.WHITE);
        } else {
            b.setBackground(new Color(0xEEEEEE));
            b.setForeground(Color.DARK_GRAY);
        }
        b.setBorder(new CompoundBorder(new LineBorder(new Color(210,210,210)), b.getBorder()));
        return b;
    }

    // ---------------- LEFT: Chọn vị trí ----------------
    private JComponent buildLeft() {
        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);

        // Tiêu đề tuyến
        JLabel route = new JLabel("  Tuyến An Hòa -> Bảo Sơn, Ngày 14/06 16:47");
        route.setOpaque(true);
        route.setBackground(new Color(0xE8F0FE));
        route.setForeground(new Color(0x1565C0));
        route.setFont(route.getFont().deriveFont(Font.BOLD, 14f));
        route.setBorder(new CompoundBorder(new LineBorder(BLUE_BORDER),
                new EmptyBorder(10, 10, 10, 10)));
        left.add(route, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);
        center.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("CHỌN VỊ TRÍ", SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        lbl.setForeground(new Color(0x1976D2));
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));
        center.add(lbl);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        legend.setBackground(Color.WHITE);
        legend.add(legendItem(new Color(0x4DB6AC), "Giường Nằm Khoang 6 Điều Hòa"));
        legend.add(legendItem(new Color(0x81C784), "Giường Nằm Khoang 4 Điều Hòa"));
        legend.add(legendItem(SEAT_FREE, "Ghế Ngồi Mềm Điều Hòa"));
        legend.add(legendItem(SEAT_SELECTED, "Ghế Đang Chọn"));
        legend.add(legendItem(SEAT_SOLD, "Ghế Đã Chọn"));
        legend.setBorder(new CompoundBorder(new LineBorder(new Color(230,230,230)),
                new EmptyBorder(8, 8, 8, 8)));
        center.add(legend);

        // ======= Danh sách toa (cuộn) =======
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        list.add(new ToaPanel("Toa số 1: Ngồi mềm điều hòa", 1));
        list.add(Box.createVerticalStrut(14));
        list.add(new ToaPanel("Toa số 2: Ngồi mềm điều hòa", 37));

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(new CompoundBorder(new LineBorder(new Color(230,230,230)), new EmptyBorder(0,0,0,0)));
        sp.getVerticalScrollBar().setUnitIncrement(18);

        center.add(Box.createVerticalStrut(6));
        center.add(sp);

        left.add(center, BorderLayout.CENTER);
        return left;
    }

    private JComponent legendItem(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);
        JLabel box = new JLabel("  ");
        box.setOpaque(true);
        box.setBackground(color);
        box.setPreferredSize(new Dimension(24, 18));
        box.setBorder(new LineBorder(new Color(200,200,200)));
        JLabel t = new JLabel(text);
        t.setForeground(new Color(0x455A64));
        p.add(box);
        p.add(t);
        return p;
    }

    // ================== TOA PANEL (6 khoang) ==================
    private class ToaPanel extends JPanel {
        ToaPanel(String title, int startSeatNumber) {
            setOpaque(false);
            setLayout(new BorderLayout());

            // Header giữa
            JLabel t = new JLabel(title, SwingConstants.CENTER);
            t.setFont(t.getFont().deriveFont(Font.BOLD, 14f));
            t.setForeground(new Color(0x1E88E5));
            t.setBorder(new EmptyBorder(4, 0, 8, 0));
            add(t, BorderLayout.NORTH);

            // Khung bo góc dày xung quanh toa
            JPanel rounded = new RoundedBorderPanel();
            rounded.setLayout(new BorderLayout());
            rounded.setOpaque(false);
            rounded.setBorder(new EmptyBorder(8, 10, 8, 10)); // padding trong toa

            // Hàng khoang: 6 khoang nằm ngang
            JPanel row = new JPanel();
            row.setOpaque(false);
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setAlignmentX(Component.CENTER_ALIGNMENT);

            // sinh 6 khoang; mỗi khoang 6 ghế (2x3)
            int seat = startSeatNumber;
            for (int k = 1; k <= 6; k++) {
                if (k > 1) {
                    row.add(Box.createHorizontalStrut(6));
                    row.add(new CompartmentSeparator());
                    row.add(Box.createHorizontalStrut(6));
                }
                row.add(new KhoangPanel("Khoang " + k, seat));
                seat += 6;
            }

            rounded.add(row, BorderLayout.CENTER);
            add(rounded, BorderLayout.CENTER);
        }
    }

    // Panel vẽ đường viền bo góc dày cho TOA
    private class RoundedBorderPanel extends JPanel {
        RoundedBorderPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int arc = 20; // bo nhẹ hơn một chút cho cân đối
    int pad = 3;  // giảm offset từ 6 xuống 3 để thu hẹp khoảng trống
    g2.setColor(Color.WHITE);
    g2.fillRoundRect(pad, pad, getWidth() - pad * 2, getHeight() - pad * 2, arc, arc);
    g2.setColor(TOA_BORDER);
    g2.setStroke(new BasicStroke(3.0f)); // viền mảnh hơn 1 chút
    g2.drawRoundRect(pad, pad, getWidth() - pad * 2, getHeight() - pad * 2, arc, arc);
    g2.dispose();
        }
    }

    // Thanh ngăn cách giữa các khoang (cao ~ ngang ma trận ghế)
    private class CompartmentSeparator extends JComponent {
        private static final int BAR_W = 6;

        // cao ≈ 3 hàng * chiều cao nút + (mỗi nút có top/bottom = SEAT_GAP)
        private int barHeight() {
            return 3 * SEAT_H + 6 * SEAT_GAP + 4; // +4px đệm
        }

        @Override public Dimension getPreferredSize() {
            return new Dimension(BAR_W, barHeight());
        }

        @Override public Dimension getMaximumSize() {
            // không cho BoxLayout kéo giãn theo chiều dọc
            return getPreferredSize();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COMPART_BAR);
            g2.fillRoundRect(0, 0, BAR_W, getHeight(), 6, 6);
            g2.dispose();
        }
    }

    // Một khoang: nhãn + lưới 3x2 (6 ghế) dùng GridBagLayout để không kéo giãn nút
    private class KhoangPanel extends JPanel {
        KhoangPanel(String title, int startSeat) {
            setOpaque(false);
            setLayout(new BorderLayout(4, 4));

            JLabel l = new JLabel(title, SwingConstants.CENTER);
            l.setForeground(new Color(0x607D8B));
            l.setFont(l.getFont().deriveFont(Font.PLAIN, 12f));
            add(l, BorderLayout.NORTH);

            JPanel grid = new JPanel(new GridBagLayout());
            grid.setOpaque(false);
            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(SEAT_GAP, SEAT_GAP, SEAT_GAP, SEAT_GAP);
            gc.anchor = GridBagConstraints.CENTER;
            gc.fill = GridBagConstraints.NONE;
            gc.weightx = 0; gc.weighty = 0;

            // 3 hàng x 2 cột
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 2; c++) {
                    int seatNumber = startSeat + r*2 + c;
                    gc.gridx = c; gc.gridy = r;
                    grid.add(seatButton(seatNumber, false, false), gc);
                }
            }
            add(grid, BorderLayout.CENTER);

            setBorder(new EmptyBorder(6, 6, 6, 6)); // padding quanh khoang
        }
    }

    // Nút ghế bo góc, không vẽ khung stroke
    private static class SeatButton extends JToggleButton {
        private final int arc;
        SeatButton(String text, boolean selected, int arc) {
            super(text, selected);
            this.arc = arc;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Nền bo góc
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Viền mảnh 1px (tuỳ chọn)
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(new Color(255,255,255,60));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JToggleButton seatButton(int number, boolean selected, boolean sold) {
        JToggleButton b = new SeatButton(String.valueOf(number), selected, /*arc:*/ 12);
        Dimension d = new Dimension(SEAT_W, SEAT_H);
        b.setPreferredSize(d);
        b.setMinimumSize(d);                     // khóa không cho nhỏ hơn
        b.setMaximumSize(d);                     // khóa không cho kéo giãn
        b.setFont(b.getFont().deriveFont(Font.BOLD, SEAT_FONT_PT));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // tắt UI mặc định để không có hiệu ứng lạ
        b.setFocusable(false);
        b.setRolloverEnabled(false);
        b.setUI(new BasicToggleButtonUI());

        // đệm nhẹ bên trong
        b.setBorder(BorderFactory.createEmptyBorder(SEAT_INNER_PAD, SEAT_INNER_PAD,
                                                   SEAT_INNER_PAD, SEAT_INNER_PAD));

        if (sold) {
            b.setBackground(SEAT_SOLD);
            b.setForeground(Color.DARK_GRAY);
            b.setEnabled(false);
        } else if (selected) {
            b.setBackground(SEAT_SELECTED);
            b.setForeground(Color.WHITE);
        } else {
            b.setBackground(SEAT_FREE);
            b.setForeground(new Color(0x174A7A));
        }

        b.addItemListener(e -> {
            if (b.isSelected()) {
                b.setBackground(SEAT_SELECTED);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(SEAT_FREE);
                b.setForeground(new Color(0x174A7A));
            }
        });
        return b;
    }

    // ---------------- RIGHT: Thông tin khách hàng ----------------
    private JComponent buildRight() {
        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBackground(Color.WHITE);

        right.add(infoHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(8, 8, 8, 8));

        body.add(customerFormRow());
        body.add(Box.createVerticalStrut(8));
        body.add(ticketDetailCard("Toa số: 1, Khoang: 2, Ghế: 9"));
        body.add(Box.createVerticalStrut(8));
        body.add(ticketDetailCard("Toa số: 1, Khoang: 2, Ghế: 13"));
        body.add(Box.createVerticalStrut(8));
        body.add(bottomButtons());

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(new LineBorder(new Color(230,230,230)));
        sp.getVerticalScrollBar().setUnitIncrement(18);

        right.add(sp, BorderLayout.CENTER);
        return right;
    }

    private JComponent infoHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BLUE_LIGHT);
        p.setBorder(new CompoundBorder(new MatteBorder(0,0,1,0, BLUE_BORDER),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel icon = new JLabel("\u2139");
        icon.setFont(icon.getFont().deriveFont(Font.BOLD, 16f));
        icon.setForeground(BLUE_PRIMARY);

        JLabel t = new JLabel("THÔNG TIN KHÁCH HÀNG");
        t.setFont(t.getFont().deriveFont(Font.BOLD, 15f));
        t.setForeground(BLUE_PRIMARY);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(icon);
        left.add(t);

        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JComponent customerFormRow() {
        JPanel g = new JPanel(new GridBagLayout());
        g.setBackground(Color.WHITE);
        g.setBorder(new CompoundBorder(new LineBorder(new Color(235,235,235)),
                new EmptyBorder(10,10,10,10)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Họ tên
        c.gridx=0; c.gridy=0; c.weightx=0;
        g.add(new JLabel("Họ Tên"), c);
        c.gridx=1; c.weightx=1;
        g.add(new JTextField(), c);

        // SĐT
        c.gridx=0; c.gridy=1; c.weightx=0;
        g.add(new JLabel("Số Điện Thoại"), c);
        c.gridx=1; c.weightx=1;
        g.add(new JTextField(), c);

        // CCCD
        c.gridx=2; c.gridy=1; c.weightx=0;
        g.add(new JLabel("CCCD"), c);
        c.gridx=3; c.weightx=1;
        g.add(new JTextField(), c);

        // căn cột
        c.gridx=2; c.gridy=0; c.weightx=0; g.add(new JLabel(""), c);
        c.gridx=3; c.gridy=0; c.weightx=1; g.add(Box.createHorizontalStrut(10), c);

        return g;
    }

    private JComponent ticketDetailCard(String titleText) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(RED_SOFT),
                new EmptyBorder(8, 10, 10, 10)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Chi Tiết Vé:  " + titleText);
        title.setForeground(RED_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13.5f));

        JButton quick = new JButton("Điền nhanh");
        quick.setFocusPainted(false);
        quick.setBackground(GREEN_SOFT);
        quick.setForeground(Color.WHITE);
        quick.setBorder(new EmptyBorder(4,10,4,10));

        c.gridx=0; c.gridy=0; c.gridwidth=3; c.weightx=1;
        card.add(title, c);
        c.gridx=3; c.gridy=0; c.gridwidth=1; c.weightx=0;
        card.add(quick, c);

        c.gridx=0; c.gridy=1; c.weightx=0; card.add(new JLabel("Họ Tên"), c);
        c.gridx=1; c.weightx=1; card.add(new JTextField(16), c);
        c.gridx=2; c.gridy=1; c.weightx=0; card.add(new JLabel("Năm Sinh"), c);
        c.gridx=3; c.weightx=0.6;
        JComboBox<String> year = new JComboBox<>();
        for (int y=1950; y<=2025; y++) year.addItem(String.valueOf(y));
        year.setSelectedItem("1990");
        card.add(year, c);

        c.gridx=0; c.gridy=2; c.weightx=0; card.add(new JLabel("CCCD"), c);
        c.gridx=1; c.weightx=1; card.add(new JTextField(16), c);
        c.gridx=2; c.gridy=2; c.weightx=0; card.add(new JLabel("Loại Vé"), c);
        c.gridx=3; c.weightx=0.6;
        JComboBox<String> type = new JComboBox<>(new String[]{
            "Vé dành cho học sinh, sinh viên","Vé người lớn","Vé trẻ em"
        });
        card.add(type, c);

        c.gridx=0; c.gridy=3; c.weightx=0;
        JLabel priceLabel = new JLabel("Tiền Vé");
        priceLabel.setForeground(RED_PRIMARY);
        card.add(priceLabel, c);
        c.gridx=1; c.weightx=0.6;
        JTextField price = new JTextField("81060.0");
        price.setHorizontalAlignment(JTextField.RIGHT);
        card.add(price, c);

        return card;
    }

    private JComponent bottomButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        p.setBackground(Color.WHITE);

        JButton back = new JButton("Quay Lại");
        back.setBackground(new Color(0x64B5F6));
        back.setForeground(Color.WHITE);
        back.setFocusPainted(false);
        back.setBorder(new EmptyBorder(8, 20, 8, 20));

        JButton next = new JButton("Tiếp Tục");
        next.setBackground(GREEN_PRIMARY);
        next.setForeground(Color.WHITE);
        next.setFocusPainted(false);
        next.setBorder(new EmptyBorder(8, 20, 8, 20));

        p.add(back);
        p.add(next);
        return p;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chi tiết vé tàu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new ManChonGheNgoi());
            frame.setMinimumSize(new Dimension(1200, 720));
            frame.setSize(new Dimension(1200, 720));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // mở tối đa ngay
        });
    }
}
