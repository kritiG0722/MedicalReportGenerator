

//package com.pationImage.com.reg;
//
//
//
//import javax.swing.*;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.io.*;
//import java.sql.*;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import com.toedter.calendar.JDateChooser;
//import com.itextpdf.text.*;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.pdf.*;
//
//public class registerPage extends JFrame {
//
//    private JTable table;
//    private DefaultTableModel model;
//    private JTextField txtUserId;
//    private JDateChooser fromDateChooser, toDateChooser;
//	protected int row;
//
//    public registerPage() {
//        setTitle("Medical Report Generator");
//        setSize(950, 550);
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JPanel panel = new JPanel(new BorderLayout(10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
//        // --- Input Panel ---
//        JPanel inputPanel = new JPanel();
//        inputPanel.add(new JLabel("User ID:"));
//        txtUserId = new JTextField(8);
//        inputPanel.add(txtUserId);
//
//        inputPanel.add(new JLabel("From Date:"));
//        fromDateChooser = new JDateChooser();
//        fromDateChooser.setDateFormatString("yyyy-MM-dd");
//        inputPanel.add(fromDateChooser);
//
//        inputPanel.add(new JLabel("To Date:"));
//        toDateChooser = new JDateChooser();
//        toDateChooser.setDateFormatString("yyyy-MM-dd");
//        inputPanel.add(toDateChooser);
//
//        JButton btnLoad = new JButton("Load Data");
//        inputPanel.add(btnLoad);
//
//        panel.add(inputPanel, BorderLayout.NORTH);
//
//        // --- JTable setup ---
//     	model = new DefaultTableModel(
//    		    new String[]{ "UserID", "ImageType", "ImageData", "medicalTestName", "Result", "Machine", "Date","Download"}, 0
//    		);
//
//    	
//        table = new JTable(model);
//        table.setRowHeight(30);
//        JScrollPane scroll = new JScrollPane(table);
//        panel.add(scroll, BorderLayout.CENTER);
//        add(panel);
//
//        // Add "Download" Button Column
//        Action downloadAction = new AbstractAction("Download") {
//            public void actionPerformed(ActionEvent e) {
//            
//                int row = Integer.parseInt(e.getActionCommand());
//                String userId = table.getValueAt(row, 0).toString();
//                generateUserPDF(userId);
//            }
//        };
//     new ButtonColumn(table, downloadAction, 7);
//
//        
//        
//        // --- Load Data Button Action ---
//        btnLoad.addActionListener(e -> loadData());
//    }
//
//    // ================= LOAD DATA =================
//    private void loadData() {
//        model.setRowCount(0);
//        String userId = txtUserId.getText().trim();
//
//        if (userId.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Enter User ID!");
//            return;
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String fromDate = fromDateChooser.getDate() != null ? sdf.format(fromDateChooser.getDate()) : null;
//        String toDate = toDateChooser.getDate() != null ? sdf.format(toDateChooser.getDate()) : null;
//
//        try (Connection conn = DriverManager.getConnection(
//                "jdbc:sqlserver://localhost:1433;"
//                + "databaseName=lab;"
//                + "encrypt=true;"
//                + "trustServerCertificate=true",
//                "sa", "lab@2017")) {
//        	
//        	
//        	
//       
//        	
//
//            String sql = "{CALL Graph_test_info(?, ?, ?)}";
//            try (CallableStatement stmt = conn.prepareCall(sql)) {
//                stmt.setString(1, userId);
//                stmt.setString(2, fromDate);
//                stmt.setString(3, toDate);
//
//                ResultSet rs = stmt.executeQuery();
//                while (rs.next()) {
//                	model.addRow(new Object[]{
//                    	    rs.getString("userID"),
//                    	   rs.getString("ImageType"),
//                    	    rs.getString("ImageData"), // Use correct column name
//                    	    rs.getString("medicalTestName"),
//                    	    rs.getString("medicalTestResult"),
//                    	    rs.getString("machineName"),
//                    	    rs.getString("insertdate"),
//                    	    "Download"
//                    	});
//
//                    
//                }
//            }
//
//            if (model.getRowCount() == 0)
//                JOptionPane.showMessageDialog(this, "No records found!");
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
//        }
//    }
//
//    // ================= PDF GENERATION =================
//    private void generateUserPDF(String userId) {
//        try {
//            java.util.List<Object[]> userRows = new ArrayList<>();
//
//            for (int i = 0; i < model.getRowCount(); i++) {
//                if (userId.equals(model.getValueAt(i, 0).toString())) {
//                    userRows.add(new Object[]{
//                            model.getValueAt(i, 1), // Test Name
//                            model.getValueAt(i, 2), 
//                            model.getValueAt(i, 3), // Result
//                            model.getValueAt(i, 4), // Machine
//                            model.getValueAt(i, 5), // Date
//                            model.getValueAt(i, 6)  // Base64 Image
//                    });
//                }
//            }
//
//            if (userRows.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No rows found for user " + userId);
//                return;
//            }
//
//            String saveDir = System.getProperty("user.home") + File.separator + "PatientReports";
//            new File(saveDir).mkdirs();
//            String pdfPath = saveDir + File.separator + "Patient_" + userId + "_Report.pdf";
//
//            Document doc = new Document(PageSize.A4, 36, 36, 60, 36);
//            PdfWriter.getInstance(doc, new FileOutputStream(pdfPath));
//            doc.open();
//
//            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
//            Paragraph title = new Paragraph("Patient Medical Test Report\n\n", titleFont);
//            title.setAlignment(Element.ALIGN_CENTER);
//            doc.add(title);
//
//            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12);
//            doc.add(new Paragraph("Patient ID: " + userId, infoFont));
//            doc.add(new Paragraph("Generated On: " + new java.util.Date(), infoFont));
//            doc.add(new Paragraph("\n-----------------------------------------------\n\n"));
//
//            // Display first 3 rows (RBC, WBC, PLT)
//            int limit = Math.min(3, userRows.size());
//            for (int i = 0; i < limit; i++) {
//                Object[] row = userRows.get(i);
//                PdfPTable pdfTable = new PdfPTable(2);
//                pdfTable.setWidthPercentage(100);
//                pdfTable.setWidths(new float[]{3f, 3f});
//
//                // Left cell (text)
//                PdfPCell textCell = new PdfPCell(new Paragraph(
//                        "Test Name: " + row[2] + "\n" +
//                        		"ImageType: " + row[0]+ "\n" +
//                         "Result: " + row[3] + "\n" +
//                         "Machine: " + row[4] + "\n" +
//                        "Date: " + row[5] + "\n", infoFont));
//                textCell.setPadding(10);
//                pdfTable.addCell(textCell);
//
//                // Right cell (image)
//                PdfPCell imgCell = new PdfPCell();
//                imgCell.setPadding(8);
//                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//                String base64 = (String) row[1];
//                if (base64 != null && !base64.isEmpty()) {
//                    try {
//                        if (base64.startsWith("ImageData"))
//                            base64 = base64.substring(base64.indexOf(',') + 1);
//                        byte[] bytes = Base64.getDecoder().decode(base64);
//                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(bytes);
//                        img.scaleToFit(220, 160);
//                        imgCell.addElement(img);
//                    } catch (Exception ex) {
//                        imgCell.addElement(new Phrase("Invalid Image"));
//                    }
//                } else {
//                    imgCell.addElement(new Phrase("No Image"));
//                }
//
//                pdfTable.addCell(imgCell);
//                doc.add(pdfTable);
//                doc.add(new Paragraph("\n"));
//            }
//
//            doc.close();
//            Desktop.getDesktop().open(new File(pdfPath));
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage());
//        }
//    }
//    
//    
//   
//    
//    // ================= ButtonColumn Class =================
//    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
//        private JTable table;
//        private Action action;
//        private JButton renderButton, editButton;
//        private String text;
//
//        public ButtonColumn(JTable table, Action action, int column) {
//            this.table = table;
//            this.action = action;
//
//            renderButton = new JButton("Download");
//            editButton = new JButton("Download");
//            editButton.setFocusPainted(false);
//            editButton.addActionListener(this);
//
//            TableColumnModel columnModel = table.getColumnModel();
//            columnModel.getColumn(column).setCellRenderer(this);
//            columnModel.getColumn(column).setCellEditor(this);
//        }
//
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            return renderButton;
//        }
//
//        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            text = (value == null) ? "Download" : value.toString();
//            editButton.setText(text);
//            return editButton;
//        }
//
//        public Object getCellEditorValue() {
//            return text;
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            int row = table.convertRowIndexToModel(table.getSelectedRow());
//            fireEditingStopped();
//            ActionEvent event = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "" + row);
//            action.actionPerformed(event);
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new registerPage().setVisible(true));
//   
//       
//        
//    } 
//    }





















//---------------------------------------new report ---------------------------------------------------------

package com.pationImage.com.reg;
import com.pationImage.com.reg.SimplePdfServer;


import javax.swing.*;

import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64;
import java.util.Date;
import java .util.List;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;



import com.toedter.calendar.JDateChooser;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.text.Image;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;


public class registerPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtUserId;
    private JDateChooser fromDateChooser, toDateChooser;

    public registerPage() {
        setTitle("Medical Report Generator");
        setSize(950, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("User ID:"));
        txtUserId = new JTextField(8);
        inputPanel.add(txtUserId);
        
        

        
        inputPanel.add(new JLabel("From Date:"));
        fromDateChooser = new JDateChooser();
        fromDateChooser.setDateFormatString("yyyy-MM-dd");
        // ✅ Set default to today's date
        fromDateChooser.setDate(new java.util.Date());
        inputPanel.add(fromDateChooser);

        inputPanel.add(new JLabel("To Date:"));
        toDateChooser = new JDateChooser();
        toDateChooser.setDateFormatString("yyyy-MM-dd");
        // ✅ Set default to today's date
        toDateChooser.setDate(new java.util.Date());
        inputPanel.add(toDateChooser);

        JButton btnLoad = new JButton("Load Data");
        inputPanel.add(btnLoad);

        panel.add(inputPanel, BorderLayout.NORTH);


        
        model = new DefaultTableModel(
                new String[]{"UserID", "ImageType", "ImageData", "medicalTestName",
                             "Result", "Machine", "Date", "Download"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);
        add(panel);

        Action downloadAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                String userId = model.getValueAt(row, 0).toString();
                generateUserPDF(userId);
            }
        };
        new ButtonColumn(table, downloadAction, 7);

        btnLoad.addActionListener(e -> loadData());
    }

    private void loadData() {
        model.setRowCount(0);
        String userId = txtUserId.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter User ID!");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = fromDateChooser.getDate() != null ? sdf.format(fromDateChooser.getDate()) : null;
        String toDate = toDateChooser.getDate() != null ? sdf.format(toDateChooser.getDate()) : null;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=lab;encrypt=true;trustServerCertificate=true",
                "sa", "lab@2017")) {

            String sql = "{CALL Graph_test_info(?, ?, ?)}";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, userId);
                stmt.setString(2, fromDate);
                stmt.setString(3, toDate);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("userID"),
                            rs.getString("ImageType"),
                            rs.getString("ImageData"),
                            rs.getString("medicalTestName"),
                            rs.getString("medicalTestResult"),
                            rs.getString("machineName"),
                            rs.getString("insertdate"),
                            "Download"
                    });
                }
            }
            if (model.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No records found!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    
    
    //==================PDF Generate Section=======================  
    
    private void generateUserPDF(String userId) {
        Map<String, List<TestRecord>> dataMap = new HashMap<>();
        Map<String, String> imageMap = new HashMap<>();

        // ✅ New fields for patient info
        String patName = "";
        String gender = "";
        String refDr = "";
        String labname="";
        String dr="";

        // =============== FETCH DATA FROM PROCEDURE ===============
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://localhost:1433;databaseName=lab;encrypt=true;trustServerCertificate=true",
                "sa", "lab@2017")) {

            String sql = "{CALL Graph_test_info(?, NULL, NULL)}";
            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // ✅ Fetch patient details once
                    if (patName.isEmpty()) {
                        patName = rs.getString("patName");
                        gender = rs.getString("gender");
                        refDr = rs.getString("refDr");
                        labname = rs.getString("labname");
                        dr = rs.getString("dr");
                    }

                    String imgType = rs.getString("ImageType");
                    String base64Img = rs.getString("ImageData");
                    String testName = rs.getString("medicalTestName");
                    String result = rs.getString("medicalTestResult");

                    if (testName == null || testName.trim().isEmpty()) continue;

                    String upperName = testName.toUpperCase(Locale.ROOT);
                    String groupKey = null;

                    if (upperName.matches(".*(RBC|HEMOGLOBIN|HCT|MCV|MCH|MCHC|RDW|RETIC).*"))
                        groupKey = "RBC";
                    else if (upperName.matches(".*(WBC|NEUT|LYMPH|MONO|EOS|BASO|GRAN|IG).*"))
                        groupKey = "WBC";
                    else if (upperName.matches(".*(PLT|PLATELET|MPV|PDW|PCT).*"))
                        groupKey = "PLT";
                    else
                        continue;

                    // group test data
                    dataMap.computeIfAbsent(groupKey, k -> new ArrayList<>())
                            .add(new TestRecord(imgType, base64Img, testName, result));

                    // ✅ Improved Base64 image mapping (Flexible & Single per Type)
                    if (imgType != null && base64Img != null && !base64Img.trim().isEmpty()) {
                        String imgKey = imgType.trim().toUpperCase();
                        if (imgKey.contains("RBC") && !imageMap.containsKey("RBC")) {
                            imageMap.put("RBC", base64Img);
                        } else if (imgKey.contains("WBC") && !imageMap.containsKey("WBC")) {
                            imageMap.put("WBC", base64Img);
                        } else if (imgKey.contains("PLT") && !imageMap.containsKey("PLT")) {
                            imageMap.put("PLT", base64Img);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            return;
        }

        // =============== PDF GENERATION ===============
        try {
            String fileName = "Blood_Report_" + userId + ".pdf";
            PdfWriter writer = new PdfWriter(fileName);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A3);
            Document doc = new Document(pdfDoc);
            PdfFont font = PdfFontFactory.createFont();
            doc.setFont(font);

            // ---------- HEADER ----------
//            doc.add(new Paragraph("Visionary Diagnostics Laboratory")
//                    .setBold().setFontSize(14)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setFontColor(new DeviceRgb(179, 0, 0)));
            
            
         // ✅ Dynamic Lab Name from DB
            doc.add(new Paragraph((labname != null && !labname.trim().isEmpty()) ? labname : "Visionary Diagnostics Laboratory")
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(179, 0, 0)));

            

            doc.add(new Paragraph("Comprehensive Blood Test Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10).setMarginBottom(8));

            // ✅ Patient info section
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(10)
                    .setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 2))
                    .setBorderRadius(new BorderRadius(10));

            infoTable.addCell(new Cell().add(new Paragraph("Patient ID: " + userId))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Referred By: " + (refDr != null ? refDr : "N/A")))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Name: " + (patName != null ? patName : "N/A")))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Date: " +
                    new SimpleDateFormat("dd-MMM-yyyy").format(new Date())))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph("Age / Gender: " + (gender != null ? gender : "N/A")))
                    .setBorder(Border.NO_BORDER));
            infoTable.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
            doc.add(infoTable);

            // ---------- SECTION LOOP (RBC, WBC, PLT) ----------
            for (String type : Arrays.asList("RBC", "WBC", "PLT")) {
                List<TestRecord> tests = dataMap.getOrDefault(type, new ArrayList<>());
                if (tests.isEmpty()) continue;

                doc.add(new Paragraph(type + " Analysis")
                        .setBold().setFontSize(13).setUnderline()
                        .setMarginBottom(5)
                        .setTextAlignment(TextAlignment.LEFT));

                Table section = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setBorder(Border.NO_BORDER)
                        .setMarginBottom(10);

                // ---------- IMAGE CELL ----------
                Cell imgCell = new Cell().setBorder(Border.NO_BORDER);
                String base64Img = imageMap.get(type);

                if (base64Img != null && !base64Img.trim().isEmpty()) {
                    try {
                        String cleanBase64 = base64Img
                                .replaceAll("data:image/[^;]+;base64,", "")
                                .replaceAll("\\s+", "")
                                .replaceAll("[^A-Za-z0-9+/=]", "");
                        byte[] imgBytes = Base64.getDecoder().decode(cleanBase64);

                        com.itextpdf.layout.element.Image image =
                                new com.itextpdf.layout.element.Image(ImageDataFactory.create(imgBytes))
                                        .setAutoScale(true)
                                        .scaleToFit(180, 140)
                                        .setMarginBottom(5)
                                        .setHorizontalAlignment(HorizontalAlignment.CENTER);

                        imgCell.add(new Paragraph(type + " Microscopic Image")
                                .setBold().setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(2));
                        imgCell.add(image);

                    } catch (Exception ex) {
                        imgCell.add(new Paragraph("⚠️ Error decoding " + type + " image")
                                .setFontColor(new DeviceRgb(255, 0, 0))
                                .setFontSize(9));
                    }
                } else {
                    imgCell.add(new Paragraph("No Image Available for " + type)
                            .setItalic().setFontSize(9));
                }

                // ---------- DATA CELL ----------
                Cell dataCell = new Cell().setBorder(Border.NO_BORDER);
                Table dataTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2}))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setBorder(Border.NO_BORDER);

                dataTable.addHeaderCell(new Cell().add(new Paragraph("Test Name").setBold()).setBorder(Border.NO_BORDER));
                dataTable.addHeaderCell(new Cell().add(new Paragraph("Result").setBold()).setBorder(Border.NO_BORDER));
                dataTable.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()).setBorder(Border.NO_BORDER));

                Set<String> seen = new HashSet<>();
                for (TestRecord r : tests) {
                    if (r.testName == null || r.result == null) continue;
                    String testName = r.testName.trim();
                    String resultStr = r.result.trim();
                    if (!resultStr.matches(".*\\d.*")) continue;
                    if (seen.contains(testName.toLowerCase())) continue;
                    seen.add(testName.toLowerCase());

                    String numericStr = resultStr.replaceAll("[^0-9.]", "");
                    double numeric = numericStr.isEmpty() ? 0 : Double.parseDouble(numericStr);

                    String status = "Normal";
                    DeviceRgb color = new DeviceRgb(0, 0, 0);
                    String upper = testName.toUpperCase();

                    if (upper.contains("HEMOGLOBIN")) {
                        if (numeric < 12) { status = "Low"; color = new DeviceRgb(0, 0, 255); }
                        else if (numeric > 17) { status = "High"; color = new DeviceRgb(255, 0, 0); }
                    } else if (upper.contains("RBC")) {
                        if (numeric < 4) { status = "Low"; color = new DeviceRgb(0, 0, 255); }
                        else if (numeric > 6) { status = "High"; color = new DeviceRgb(255, 0, 0); }
                    } else if (upper.contains("WBC")) {
                        if (numeric < 4) { status = "Low"; color = new DeviceRgb(0, 0, 255); }
                        else if (numeric > 11) { status = "High"; color = new DeviceRgb(255, 0, 0); }
                    } else if (upper.contains("PLT")) {
                        if (numeric < 150) { status = "Low"; color = new DeviceRgb(0, 0, 255); }
                        else if (numeric > 450) { status = "High"; color = new DeviceRgb(255, 0, 0); }
                    }

                    dataTable.addCell(new Cell().add(new Paragraph(testName)).setBorder(Border.NO_BORDER));
                    Paragraph resultPara = new Paragraph(numericStr);
                    if (!"Normal".equals(status)) resultPara.setBold().setFontColor(color);

                    dataTable.addCell(new Cell().add(resultPara).setBorder(Border.NO_BORDER));
                    dataTable.addCell(new Cell().add(new Paragraph(status).setBold().setFontColor(color)).setBorder(Border.NO_BORDER));
                }

                dataCell.add(dataTable);
                section.addCell(dataCell);
                section.addCell(imgCell);
                doc.add(section);
            }

            // ---------- FOOTER ----------
            
         // ---------- FOOTER LINE + SIGNATURE SECTION ----------

         // Add horizontal footer line
         SolidLine footerLine = new SolidLine(1f); // 1f = thickness of line
         footerLine.setColor(new DeviceRgb(179, 0, 0)); // optional red (same as header)
         doc.add(new LineSeparator(footerLine));

         // Add small spacing after line
         doc.add(new Paragraph("\n"));
            
//            String now = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
//            doc.add(new Paragraph("Report generated on: " + now)
//                    .setTextAlignment(TextAlignment.RIGHT)
//                    .setFontSize(11)
//                    .setFontColor(new DeviceRgb(128, 128, 128)));

            doc.add(new Paragraph("Verified by: " + dr)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14)
                    .setFontColor(new DeviceRgb(128, 128, 128)));
            
            
         
            
            
            //=========================QR CODE SECTION================================== 
            
        
            
         // Start local server (only once)
            SimplePdfServer.startServer();

            // Get system IP (so phone can reach it)
            String ip = SimplePdfServer.getLocalIp();

            // Generate QR link for the phone
            String qrContent = "http://" + ip + ":8081/download?userId=" + userId;

            // --- your existing QR creation code here ---
            BitMatrix matrix = new MultiFormatWriter().encode(qrContent, BarcodeFormat.QR_CODE, 130, 130);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] qrBytes = baos.toByteArray();

            com.itextpdf.layout.element.Image qrCodeImg =
                    new com.itextpdf.layout.element.Image(ImageDataFactory.create(qrBytes))
                            .setWidth(90)
                            .setHeight(90)
                            .setHorizontalAlignment(HorizontalAlignment.CENTER);

            doc.add(new Paragraph("📱 Scan to View or Download Report")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));
            doc.add(qrCodeImg);


            //==========QR END====

            doc.close();
            JOptionPane.showMessageDialog(this, "✅ PDF Generated: " + fileName);
            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "PDF Error: " + e.getMessage());
        }
    }

    
    
    //-----------------------------------------------------------------------------------
    

    // Bold HIGH/LOW
    private String formatResult(String result, String testName) {
        try {
            String num = result == null ? "" : result.replaceAll("[^0-9.]", "");
            if (num.isEmpty()) return result;
            double value = Double.parseDouble(num);

            double low = 0, high = 0;
            switch (testName == null ? "" : testName.toLowerCase()) {
                case "rbc count": case "rbc": low = 4.5; high = 5.9; break;
                case "hemoglobin": low = 13.5; high = 17.5; break;
                case "hematocrit": low = 40; high = 50; break;
                case "wbc count": case "wbc": low = 4.0; high = 11.0; break;
                case "platelet count": case "plt": low = 150; high = 450; break;
                default: return result;
            }
            if (value < low) return "LOW: " + result;
            if (value > high) return "HIGH: " + result;
        } catch (Exception e) { }
        return result;
    }

    private static class TestRecord {
        final String imageType, imageData, testName, result;
        TestRecord(String imageType, String imageData, String testName, String result) {
            this.imageType = imageType; this.imageData = imageData;
            this.testName = testName; this.result = result;
        }
    }

    
    
    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener {
        private final JTable table;
        private final Action action;
        private final JButton renderButton = new JButton("Download");
        private final JButton editButton = new JButton("Download");
        private String text;

        public ButtonColumn(JTable table, Action action, int column) {
            this.table = table; this.action = action;
            editButton.setFocusPainted(false);
            editButton.addActionListener(this);
            TableColumnModel cm = table.getColumnModel();
            cm.getColumn(column).setCellRenderer(this);
            cm.getColumn(column).setCellEditor(this);
        }

        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
            renderButton.setText(v == null ? "Download" : v.toString());
            return renderButton;
        }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean isSel, int r, int c) {
            text = (v == null) ? "Download" : v.toString();
            editButton.setText(text);
            return editButton;
        }

        @Override public Object getCellEditorValue() { return text; }

        @Override public void actionPerformed(ActionEvent e) {
            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
            fireEditingStopped();
            ActionEvent ev = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, String.valueOf(modelRow));
            action.actionPerformed(ev);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new registerPage().setVisible(true));
    }
}
