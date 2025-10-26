package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.plaf.basic.BasicButtonUI;

/** Giao diện "Tìm kiếm vé đổi" theo mẫu ảnh (chỉ UI) */
public class DoiVe extends JPanel {

    private final JTextField tfMaVeSearch = Ui.field();
    private final JButton btnTimVe = Ui.primary("Tìm Vé");

    // form thông tin vé (bên phải)
    private final Map<String, JTextField> fields = new LinkedHashMap<>();
    private final JButton btnChonVeMoi = Ui.primary("Chọn Vé Mới");

    public DoiVe(){
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F7FB));

        // Loại bỏ painter Nimbus gây “lớp trắng”
        for (JButton b : new JButton[]{ btnTimVe, btnChonVeMoi }) {
            b.setUI(new BasicButtonUI());
            b.setContentAreaFilled(true);
            b.setOpaque(true);
            b.setFocusPainted(false);
        }

        add(Ui.banner("TÌM KIẾM VÉ ĐỔI"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14,14,14,14));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0,0,0,12);
        gc.gridx = 0; gc.gridy = 0; gc.fill = GridBagConstraints.BOTH; gc.weightx = 0.42; gc.weighty = 1;

        // Cột trái: Quy định + ô tìm
        body.add(leftColumn(), gc);

        // Cột phải: Thông tin vé
        gc.gridx = 1; gc.weightx = 0.58;
        body.add(rightColumn(), gc);

        add(body, BorderLayout.CENTER);
    }

    private JComponent leftColumn(){
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JPanel rules = Ui.card(Ui.infoBox(
                "Thời điểm yêu cầu đổi: hành khách phải thực hiện đổi trước giờ tàu chạy ghi trên vé ít nhất thời gian tối thiểu quy định.",
                "Ga đi và ga đến của vé mới phải giống hoặc nằm trong cùng tuyến/điểm xuất phát-đích với vé cũ theo quy định “cùng ga đi – cùng ga đến”.",
                "Người yêu cầu đổi phải là người mua vé hoặc hành khách trên vé (hoặc được ủy quyền hợp pháp) và thông tin hành khách trên vé phải trùng với giấy tờ tùy thân.",
                "Thông tin cá nhân hành khách không bị thay đổi khi đổi vé (không đổi tên, số giấy tờ tùy thân, số hành khách…).",
                "",
                "📌 Phụ phí khi đổi vé:",
                "• Đổi ≥ 24 giờ: Phí 10% giá vé cũ.",
                "• Đổi 4–24 giờ: Phí 20% giá vé cũ.",
                "• Đổi < 4 giờ: Không được đổi vé."
        ), "QUY ĐỊNH ĐỔI VÉ");
        col.add(rules);
        col.add(Box.createVerticalStrut(16));

        // Card tìm vé
        JPanel search = new JPanel();
        search.setOpaque(false);
        search.setLayout(new BoxLayout(search, BoxLayout.Y_AXIS));

        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line.setOpaque(false);
        JLabel lb = new JLabel("Mã Vé");
        lb.setBorder(new EmptyBorder(0,2,4,12));
        tfMaVeSearch.setPreferredSize(new Dimension(300, 34));
        line.add(lb); line.add(tfMaVeSearch);
        search.add(line);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        btnRow.add(btnTimVe);
        search.add(btnRow);

        col.add(Ui.card(search, ""));
        return col;
    }

    private JComponent rightColumn(){
        JPanel col = new JPanel(new BorderLayout());
        col.setOpaque(false);

        JPanel form = new JPanel(new GridLayout(0,2,12,10));
        form.setOpaque(false);
        String[] names = {
                "Mã Vé:","Họ Tên Hành Khách:","Năm Sinh:","Số CCCD:",
                "Chuyến Tàu:","Tàu Di Chuyển:","Số Toa:","Số Khoang:",
                "Loại Ghế:","Số Ghế:","Loại Vé:","Tiền Vé:"
        };
        for (String n : names){
            JTextField f = Ui.field(); 
            f.setEditable(false);
            fields.put(n, f);
            form.add(new JLabel(n)); 
            form.add(f);
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.setOpaque(false);
        btnChonVeMoi.setEnabled(false);
        btnRow.add(btnChonVeMoi);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(form, BorderLayout.CENTER);
        wrap.add(btnRow, BorderLayout.SOUTH);

        col.add(Ui.card(wrap, "THÔNG TIN VÉ"), BorderLayout.CENTER);

        // sự kiện demo
        btnTimVe.addActionListener(e -> fillMockTicket());
        return col;
    }

    private void fillMockTicket(){
        String[][] demo = {
                {"Mã Vé:","DV012345"},
                {"Họ Tên Hành Khách:","Nguyễn Minh Phúc"},
                {"Năm Sinh:","1999"},
                {"Số CCCD:","010001002345"},
                {"Chuyến Tàu:","Sài Gòn → Hà Nội (08/10/2025)"},
                {"Tàu Di Chuyển:","SE5"}, {"Số Toa:","02"}, {"Số Khoang:","01"},
                {"Loại Ghế:","Ngồi mềm điều hòa"}, {"Số Ghế:","18"},
                {"Loại Vé:","Vé dành cho học sinh, sinh viên"},
                {"Tiền Vé:","320.000₫"}
        };
        for (String[] kv : demo){
            JTextField f = fields.get(kv[0]);
            if (f != null) f.setText(kv[1]);
        }
        btnChonVeMoi.setEnabled(true);
    }
}
