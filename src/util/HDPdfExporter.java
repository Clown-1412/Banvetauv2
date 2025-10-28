package util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

public class HDPdfExporter {

    // A4 dọc và lề thoáng
    private static final Rectangle PAGE_SIZE = PageSize.A4;
    private static final float MARGIN_L = 36f, MARGIN_R = 36f, MARGIN_T = 36f, MARGIN_B = 48f;

    // Đường dẫn font Unicode
    private static final String FONT_PATH = "C:/Windows/Fonts/times.ttf";

    // ====== Model đơn giản ======
    public static class InvoiceItem {
        public String maVe;
        public String tenDichVu;
        public int soLuong;
        public long donGia;              // VND
        public long thanhTienChuaThue;   // = soLuong * donGia (có thể tự truyền)
        public long thueGTGT;            // VND (ví dụ 10%)
        public long thanhTienCoThue;     // = chưa thuế + thuế
    }

    public static class Invoice {
        // Header
        public String tieuDe = "HÓA ĐƠN GIÁ TRỊ GIA TĂNG";
        public LocalDate ngayLap = LocalDate.now();

        // Thông tin bán hàng
        public String maHoaDon;
        public String donViBanHang;
        public String diaChi;
        public String nhanVienLap;
        public String dienThoaiNV;

        // Thông tin khách
        public String tenKhach;
        public String dienThoaiKhach;
        public String hinhThucThanhToan;

        // Dòng chi tiết
        public List<InvoiceItem> items = new ArrayList<>();

        // Ghi chú
        public String ghiChu = "";
    }

    // ====== MAIN DEMO ======
    public static void main(String[] args) {
        try {
            Invoice inv = new Invoice();
            inv.maHoaDon = "HD-2025-0001";
            inv.donViBanHang = "Công ty CP Vận tải Đường sắt Sài Gòn";
            inv.diaChi = "01 Nguyễn Thông, P.9, Q.3, TP.HCM";
            inv.nhanVienLap = "Nguyễn Minh Khoa";
            inv.dienThoaiNV = "0903 123 456";

            inv.tenKhach = "Trần Thị B";
            inv.dienThoaiKhach = "0987 654 321";
            inv.hinhThucThanhToan = "Tiền mặt";

            // 2 dòng mẫu
            InvoiceItem i1 = new InvoiceItem();
            i1.maVe = "VLZ8CZK3NS";
            i1.tenDichVu = "Vé tàu SE001 SG → HN (Toa 03 / Ghế 06B)";
            i1.soLuong = 1;
            i1.donGia = 1_250_000;
            i1.thanhTienChuaThue = i1.soLuong * i1.donGia;
            i1.thueGTGT = Math.round(i1.thanhTienChuaThue * 0.10);
            i1.thanhTienCoThue = i1.thanhTienChuaThue + i1.thueGTGT;
            inv.items.add(i1);

            InvoiceItem i2 = new InvoiceItem();
            i2.maVe = "ABCD123456";
            i2.tenDichVu = "Phụ phí đổi vé";
            i2.soLuong = 1;
            i2.donGia = 50_000;
            i2.thanhTienChuaThue = i2.soLuong * i2.donGia;
            i2.thueGTGT = Math.round(i2.thanhTienChuaThue * 0.10);
            i2.thanhTienCoThue = i2.thanhTienChuaThue + i2.thueGTGT;
            inv.items.add(i2);

            export(inv, "invoice.pdf");
            System.out.println("✅ Đã xuất: " + new java.io.File("invoice.pdf").getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====== Export ======
    public static void export(Invoice inv, String outPath) throws Exception {
        BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font fTitle = new Font(bf, 16, Font.BOLD);
        Font fSub   = new Font(bf, 11, Font.NORMAL);
        Font fLabel = new Font(bf, 11, Font.NORMAL);
        Font fBold  = new Font(bf, 11, Font.BOLD);
        Font fCell  = new Font(bf, 10, Font.NORMAL);
        Font fCellB = new Font(bf, 10, Font.BOLD);
        Font fSmall = new Font(bf, 9, Font.NORMAL);

        Document doc = new Document(PAGE_SIZE, MARGIN_L, MARGIN_R, MARGIN_T, MARGIN_B);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outPath));
        doc.open();

        // ===== Tiêu đề + ngày =====
        Paragraph title = new Paragraph(inv.tieuDe, fTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        String ngayText = "Ngày " + inv.ngayLap.getDayOfMonth() + " tháng " + inv.ngayLap.getMonthValue() + " năm " + inv.ngayLap.getYear();
        Paragraph ngay = new Paragraph(ngayText, fSub);
        ngay.setAlignment(Element.ALIGN_CENTER);
        ngay.setSpacingAfter(10f);
        doc.add(ngay);

        // ===== Khối thông tin người bán =====
        PdfPTable seller = new PdfPTable(new float[]{20, 80});
        seller.setWidthPercentage(100);
        seller.setHorizontalAlignment(Element.ALIGN_LEFT);

        seller.addCell(labelCell("Mã Hóa Đơn:", fLabel));
        seller.addCell(fillLineCell(inv.maHoaDon, fLabel));

        seller.addCell(labelCell("Đơn vị bán hàng:", fLabel));
        seller.addCell(fillLineCell(inv.donViBanHang, fLabel));

        seller.addCell(labelCell("Địa chỉ:", fLabel));
        seller.addCell(fillLineCell(inv.diaChi, fLabel));

        seller.addCell(labelCell("Nhân viên lập hóa đơn:", fLabel));
        seller.addCell(fillLineCell(inv.nhanVienLap, fLabel));

        seller.addCell(labelCell("Điện thoại Nhân viên:", fLabel));
        seller.addCell(fillLineCell(inv.dienThoaiNV, fLabel));

        seller.setSpacingAfter(8f);
        doc.add(seller);

        // ===== Khối thông tin khách + Hình thức thanh toán =====
        PdfPTable buyer = new PdfPTable(new float[]{18, 32, 22, 28});
        buyer.setWidthPercentage(100);

        buyer.addCell(labelCell("Họ tên khách hàng:", fLabel));
        buyer.addCell(fillLineCell(inv.tenKhach, fLabel));
        buyer.addCell(labelCell("Điện thoại Khách hàng:", fLabel));
        buyer.addCell(fillLineCell(inv.dienThoaiKhach, fLabel));

        buyer.addCell(labelCell("Hình thức thanh toán:", fLabel));
        PdfPCell pay = fillLineCell(inv.hinhThucThanhToan, fLabel);
        pay.setColspan(3);
        pay.setPaddingLeft(10f);
        buyer.addCell(pay);

        buyer.setSpacingAfter(8f);
        doc.add(buyer);

        // ===== Bảng chi tiết =====
        PdfPTable tbl = new PdfPTable(new float[]{6, 14, 32, 9, 12, 13, 10, 14});
        tbl.setWidthPercentage(100);

        // Header
        headerCell(tbl, "STT", fCellB);
        headerCell(tbl, "Mã vé", fCellB);
        headerCell(tbl, "Tên dịch vụ", fCellB);
        headerCell(tbl, "Số lượng", fCellB);
        headerCell(tbl, "Đơn giá", fCellB);
        headerCell(tbl, "Thành tiền\nchưa có thuế", fCellB);
        headerCell(tbl, "Thuế\nGTGT", fCellB);
        headerCell(tbl, "TT có thuế", fCellB);

        NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        long sumChuaThue = 0, sumThue = 0, sumCoThue = 0;

        for (int i = 0; i < inv.items.size(); i++) {
            InvoiceItem it = inv.items.get(i);
            sumChuaThue += it.thanhTienChuaThue;
            sumThue     += it.thueGTGT;
            sumCoThue   += it.thanhTienCoThue;

            bodyCell(tbl, String.valueOf(i + 1), fCell, Element.ALIGN_CENTER);
            bodyCell(tbl, nullToEmpty(it.maVe), fCell, Element.ALIGN_LEFT);
            bodyCell(tbl, nullToEmpty(it.tenDichVu), fCell, Element.ALIGN_LEFT);
            bodyCell(tbl, String.valueOf(it.soLuong), fCell, Element.ALIGN_RIGHT);
            bodyCell(tbl, vnd.format(it.donGia), fCell, Element.ALIGN_RIGHT);
            bodyCell(tbl, vnd.format(it.thanhTienChuaThue), fCell, Element.ALIGN_RIGHT);
            bodyCell(tbl, vnd.format(it.thueGTGT), fCell, Element.ALIGN_RIGHT);
            bodyCell(tbl, vnd.format(it.thanhTienCoThue), fCell, Element.ALIGN_RIGHT);
        }

        // Dòng tổng cộng (nếu muốn hiển thị)
        PdfPCell totalLab = new PdfPCell(new Phrase("Tổng cộng", fCellB));
        totalLab.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLab.setColspan(5);
        styleBody(totalLab);
        tbl.addCell(totalLab);

        PdfPCell totalChua = new PdfPCell(new Phrase(vnd.format(sumChuaThue), fCellB));
        totalChua.setHorizontalAlignment(Element.ALIGN_RIGHT); styleBody(totalChua); tbl.addCell(totalChua);

        PdfPCell totalThue = new PdfPCell(new Phrase(vnd.format(sumThue), fCellB));
        totalThue.setHorizontalAlignment(Element.ALIGN_RIGHT); styleBody(totalThue); tbl.addCell(totalThue);

        PdfPCell totalCo = new PdfPCell(new Phrase(vnd.format(sumCoThue), fCellB));
        totalCo.setHorizontalAlignment(Element.ALIGN_RIGHT); styleBody(totalCo); tbl.addCell(totalCo);

        tbl.setSpacingAfter(6f);
        doc.add(tbl);

        // ===== Ghi chú =====
        PdfPTable sign = new PdfPTable(new float[]{50, 50});

        // trước đây: full width nên trông dạt về 2 bên
        // sign.setWidthPercentage(100);

        sign.setWidthPercentage(60);                   // ⬅️ thu còn ~60% chiều ngang trang
        sign.setHorizontalAlignment(Element.ALIGN_CENTER); // ⬅️ đặt cả bảng ở giữa
        sign.setSpacingBefore(12f);                    // (tuỳ chọn) tạo khoảng trống phía trên

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.addElement(new Paragraph("Người mua hàng", fBold));
        left.addElement(new Paragraph("(Ký, ghi rõ họ tên)", fSmall));
        left.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.addElement(new Paragraph("Người bán hàng", fBold));
        right.addElement(new Paragraph("(Ký, ghi rõ họ tên)", fSmall));
        right.setHorizontalAlignment(Element.ALIGN_CENTER);

        sign.addCell(left);
        sign.addCell(right);
        doc.add(sign);

        // ===== Footer – Ngày in ghim dưới đáy trang =====
        String printed = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy").format(LocalDateTime.now());
        Phrase foot = new Phrase("(Ngày in/Printed date:)  " + printed, fSmall);
        PdfContentByte canvas = writer.getDirectContent();
        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                foot,
                (doc.left() + doc.right()) / 2f,
                doc.bottom() - 10f,
                0
        );

        doc.close();
        writer.close();
    }

    // ===== Helpers =====
    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    private static PdfPCell labelCell(String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(3f);
        c.setNoWrap(true);          // ⬅️
        c.setUseAscender(true);
        c.setUseDescender(true);
        return c;
    }

    // Ô có gạch chân bên dưới để điền (giống “dòng kẻ”)
    private static PdfPCell fillLineCell(String content, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(content == null ? "" : content, f));
        c.setBorder(Rectangle.BOTTOM);
        c.setPadding(3f);
        c.setBorderWidthBottom(0.8f);
        c.setNoWrap(true);          // ⬅️ chống xuống dòng
        c.setUseAscender(true);
        c.setUseDescender(true);
        c.setMinimumHeight(16f);    // cao dòng ổn định
        return c;
    }

    private static void headerCell(PdfPTable t, String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(5f);
        c.setBackgroundColor(new Color(240, 245, 255));
        c.setBorderWidth(1f);
        t.addCell(c);
    }

    private static void bodyCell(PdfPTable t, String text, Font f, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        styleBody(c);
        t.addCell(c);
    }

    private static void styleBody(PdfPCell c) {
        c.setPadding(5f);
        c.setBorderWidthLeft(1f);
        c.setBorderWidthRight(1f);
        c.setBorderWidthTop(0.7f);
        c.setBorderWidthBottom(0.7f);
        // kẻ chấm nhẹ trong ô (như mẫu) → dùng line thấp
        // Nếu muốn "dotted", bạn có thể custom draw, ở đây dùng border mảnh là đủ.
    }
}
