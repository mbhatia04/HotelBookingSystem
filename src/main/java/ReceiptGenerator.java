import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReceiptGenerator {

    public static void generateReceipt(int stayId, String outputDir) {
        try (Connection conn = DBUtil.getConnection()) {
            String stayQuery = """
                SELECT s.stay_id, s.check_in_date, s.actual_check_out_date, s.room_number,
                       g.first_name, g.last_name, g.email, g.phone
                FROM hbs.stay s
                JOIN hbs.guest g ON s.guest_id = g.guest_id
                WHERE s.stay_id = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(stayQuery);
            stmt.setInt(1, stayId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ No stay found for stay_id: " + stayId);
                return;
            }

            String fileName = outputDir + "/receipt_stay_" + stayId + ".pdf";
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();

            LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
            LocalDate checkOut = rs.getDate("actual_check_out_date").toLocalDate();
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

            doc.add(new Paragraph("MSB Hotel - Stay Receipt", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            doc.add(new Paragraph("Stay ID: " + stayId));
            doc.add(new Paragraph("Guest: " + rs.getString("first_name") + " " + rs.getString("last_name")));
            doc.add(new Paragraph("Email: " + rs.getString("email")));
            doc.add(new Paragraph("Phone: " + rs.getString("phone")));
            doc.add(new Paragraph("Room Number: " + rs.getString("room_number")));
            doc.add(new Paragraph("Check-in: " + checkIn));
            doc.add(new Paragraph("Check-out: " + checkOut));
            doc.add(Chunk.NEWLINE);

            Paragraph chargeHeader = new Paragraph("Charges", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            chargeHeader.setSpacingAfter(8f);
            doc.add(chargeHeader);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.addCell("Type");
            table.addCell("Amount");

            Map<String, Double> totals = new LinkedHashMap<>();
            Map<String, Integer> countMap = new LinkedHashMap<>();

            String chargesQuery = "SELECT charge_type, amount, charge_date FROM hbs.charge WHERE stay_id = ?";
            PreparedStatement chargesStmt = conn.prepareStatement(chargesQuery);
            chargesStmt.setInt(1, stayId);
            ResultSet crs = chargesStmt.executeQuery();
            double grandTotal = 0.0;

            while (crs.next()) {
                String type = crs.getString("charge_type");
                double amount = crs.getDouble("amount");

                if ("Room Service".equals(type)) {
                    table.addCell("Room Service on " + crs.getDate("charge_date"));
                    table.addCell(String.format("$%.2f", amount));
                    grandTotal += amount;
                } else {
                    totals.put(type, totals.getOrDefault(type, 0.0) + amount);
                    countMap.put(type, countMap.getOrDefault(type, 0) + 1);
                }
            }

            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                String type = entry.getKey();
                double amount = entry.getValue();
                int count = countMap.get(type);

                String label = ("Room Rate".equals(type)) ? type + " (" + count + " nights)" :
                        ("Room Tax 10%".equals(type)) ? type + " (" + count + " nights)" :
                                type;

                table.addCell(label);
                table.addCell(String.format("$%.2f", amount));
                grandTotal += amount;
            }

            doc.add(table);
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Total: $" + String.format("%.2f", grandTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.close();

            System.out.println("✅ PDF receipt saved to: " + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}