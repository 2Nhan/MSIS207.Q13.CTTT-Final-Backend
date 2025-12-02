package com.crm.project.service;

import com.crm.project.dto.response.ProductResponse;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class PdfService {

    public byte[] generateQuotation(String receiver, String sender, String startDate, String endDate, List<ProductResponse> products) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // === Font hỗ trợ tiếng Việt ===
        BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font normal = new Font(baseFont, 10);
        Font boldFont = new Font(baseFont, 11, Font.BOLD);
        Font boldBlue = new Font(baseFont, 11, Font.BOLD, new Color(51, 102, 204));
        Font smallFont = new Font(baseFont, 9);
        Font companyNameFont = new Font(baseFont, 11, Font.BOLD);
        Font companyInfoFont = new Font(baseFont, 8);
        Font emailFont = new Font(baseFont, 8, Font.NORMAL, new Color(51, 102, 204));

        DecimalFormat df = new DecimalFormat("#,###");

        // === Header: Logo bên trái, company info bên phải ===
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{0.5f, 4f}); // Logo nhỏ, info lớn

        // Cột 1: Logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_TOP);
        try (InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/images/logo.jpg")) {
            if (logoStream != null) {
                com.lowagie.text.Image logo = com.lowagie.text.Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(65, 65);
                logoCell.addElement(logo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerTable.addCell(logoCell);

        // Cột 2: Company info
        PdfPCell infoCell = new PdfPCell();
        infoCell.setBorder(Rectangle.NO_BORDER);
        infoCell.setPadding(0);
        infoCell.setPaddingLeft(15f);
        infoCell.setVerticalAlignment(Element.ALIGN_TOP);

        Paragraph companyInfo = new Paragraph();
        companyInfo.add(new Chunk("CÔNG TY TNHH GIẢI PHÁP PHẦN MỀM XYZ\n", companyNameFont));
        companyInfo.add(new Chunk("Địa chỉ: 90 Hai Bà Trưng, Phường Dĩ An, Quận Thủ Đức, TP Hồ Chí Minh\n", companyInfoFont));
        companyInfo.add(new Chunk("VPĐD - 205/13/7 Lê Hồng Phong, P.Dĩ An, TP Hồ Chí Minh\n", companyInfoFont));
        companyInfo.add(new Chunk("Hotline: 090.999.1199 - Email: ", companyInfoFont));
        companyInfo.add(new Chunk("admin@crmsoft.site", emailFont));
        companyInfo.add(new Chunk(" - Website: ", companyInfoFont));
        companyInfo.add(new Chunk("vero.crmsoft.site", emailFont));
        companyInfo.setLeading(12f); // Khoảng cách giữa các dòng
        companyInfo.setSpacingAfter(20f);
        infoCell.addElement(companyInfo);
        headerTable.addCell(infoCell);

        document.add(headerTable);

        // Đường kẻ ngang dưới header
//        document.add(new Paragraph(" ", normal)); // Khoảng cách
        com.lowagie.text.pdf.draw.LineSeparator line = new com.lowagie.text.pdf.draw.LineSeparator();
        line.setLineWidth(1.5f);
        line.setLineColor(new Color(0, 0, 0));
        document.add(new Chunk(line));
        document.add(new Paragraph(" ", normal)); // Khoảng cách

        // === Tiêu đề "BÁO GIÁ DỊCH VỤ" (căn giữa) ===
        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        Paragraph title = new Paragraph("BẢNG BÁO GIÁ", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);

        // === Phần giới thiệu (italic) ===
        Font italicFont = new Font(baseFont, 10, Font.ITALIC);
        Paragraph intro = new Paragraph();
        intro.add(new Chunk("Kính gửi: ", new Font(baseFont, 10, Font.BOLDITALIC)));
        intro.add(new Chunk(receiver + "\n\n", new Font(baseFont, 10, Font.BOLDITALIC)));
        intro.add(new Chunk("Trân trọng cảm ơn Quý khách hàng đã quan tâm đến dịch vụ và sản phẩm do chúng tôi " +
                "cung cấp. Qua nội dung trao đổi, chúng tôi xin gửi đến Quý khách hàng bảng báo giá chi tiết, " +
                "trong giới vời nội dung chi tiết như sau:", italicFont));
        intro.setAlignment(Element.ALIGN_JUSTIFIED);
        intro.setSpacingAfter(20f);
        document.add(intro);


        // === Bảng thông tin 3 cột: Ngày báo giá, Ngày hết hạn, Chuyên viên sales ===
        PdfPTable infoTable = new PdfPTable(3);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1f, 1f, 1f});

        // Cột 1: Ngày báo giá
        PdfPCell cell1 = new PdfPCell();
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(5f);
        Paragraph p1 = new Paragraph();
        p1.add(new Chunk("Ngày báo giá\n", boldBlue));
        p1.add(new Chunk(startDate, normal));
        cell1.addElement(p1);
        infoTable.addCell(cell1);

        // Cột 2: Ngày hết hạn
        PdfPCell cell2 = new PdfPCell();
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(5f);
        Paragraph p2 = new Paragraph();
        p2.add(new Chunk("Ngày hết hạn\n", boldBlue));
        p2.add(new Chunk(endDate, normal));
        cell2.addElement(p2);
        infoTable.addCell(cell2);

        // Cột 3: Chuyên viên sales
        PdfPCell cell3 = new PdfPCell();
        cell3.setBorder(Rectangle.NO_BORDER);
        cell3.setPadding(5f);
        Paragraph p3 = new Paragraph();
        p3.add(new Chunk("Chuyên viên sales\n", boldBlue));
        p3.add(new Chunk(sender, normal));
        cell3.addElement(p3);
        infoTable.addCell(cell3);

        document.add(infoTable);
        document.add(new Paragraph(" ", normal));
        document.add(new Paragraph(" ", normal));

        // === Bảng sản phẩm ===
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 2.5f, 2.5f, 2.5f, 2.5f});

        // Header
        table.addCell(headerCell("Diễn giải", boldFont));
        table.addCell(headerCell("Số lượng", boldFont));
        table.addCell(headerCell("Đơn giá", boldFont));
        table.addCell(headerCell("Thuế", boldFont));
        table.addCell(headerCell("Số tiền", boldFont));

        double subtotal = 0;
        for (ProductResponse p : products) {
            double lineTotal = p.getQuantity() * p.getPrice().doubleValue();
            subtotal += lineTotal;

            table.addCell(normalCell(p.getName(), normal));
            table.addCell(normalCell(df.format(p.getQuantity()) + " Đơn vị", normal));
            table.addCell(normalCell(df.format(p.getPrice()) + ",00", normal));
            table.addCell(normalCell("VAT 10%", smallFont));
            table.addCell(amountCell(df.format(lineTotal) + ",000 ₫", normal));
        }

        document.add(table);

        // === Tổng cộng (bên phải) ===
        double tax = subtotal * 0.1;
        double total = subtotal + tax;

        document.add(new Paragraph(" ", normal));

        // Subtotal
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(50);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setWidths(new float[]{2f, 1.5f});

        totalTable.addCell(summaryLabelCell("Số tiền trước thuế", normal));
        totalTable.addCell(summaryAmountCell(df.format(subtotal) + ",000 ₫", normal));

        totalTable.addCell(summaryLabelCell("Thuế GTGT 10%", normal));
        totalTable.addCell(summaryAmountCell(df.format(tax) + ",000 ₫", normal));

        // Dòng tổng (in đậm, màu xanh)
        totalTable.addCell(summaryLabelCell("Tổng", boldBlue));
        totalTable.addCell(summaryAmountCell(df.format(total) + ",000 ₫", boldBlue));

        document.add(totalTable);

        document.close();
        return out.toByteArray();
    }

    // --- Helper cells ---
    private PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBackgroundColor(Color.WHITE);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderWidthBottom(1.5f);
        cell.setPadding(8f);
        cell.setPaddingBottom(10f);
        return cell;
    }

    private PdfPCell normalCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setPadding(8f);
        cell.setPaddingTop(15f);
        return cell;
    }

    private PdfPCell amountCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setPadding(8f);
        cell.setPaddingTop(15f);
        return cell;
    }

    private PdfPCell summaryLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5f);
        cell.setPaddingRight(15f);
        return cell;
    }

    private PdfPCell summaryAmountCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5f);
        return cell;
    }

    private Paragraph rightText(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_RIGHT);
        return p;
    }
}
