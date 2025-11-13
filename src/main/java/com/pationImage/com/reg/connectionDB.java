//
//
////---------------------------------------------------------------------------------
//
//
//package com.pationImage.com.reg;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class connectionDB {
//
//    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=lab;encrypt=true;trustServerCertificate=true";
//    private static final String DB_USER = "sa";
//    private static final String DB_PASS = "lab@2017";
//
//    public static class PatientImage {
//        public int id;
//        public String userID;
//        public String imageType;
//        public String ImageData;           // ✅ changed
//        public String medicalTestName;
//        public String medicalTestResult;
//        public String machineName;
//        public Date createDate;
//    }
//
//    public List<PatientImage> getPatientImages(String userID, java.sql.Date fromDate, java.sql.Date toDate) {
//        List<PatientImage> list = new ArrayList<>();
//
//        String sql = "{CALL Graph_test_info(?, ?, ?)}";
//
//        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
//             CallableStatement cs = con.prepareCall(sql)) {
//
//            cs.setString(1, userID);
//            if (fromDate != null) cs.setDate(2, fromDate); else cs.setNull(2, Types.DATE);
//            if (toDate != null) cs.setDate(3, toDate); else cs.setNull(3, Types.DATE);
//
//            System.out.println("Executing Graph_test_info with userID=" + userID + 
//                ", fromDate=" + fromDate + ", toDate=" + toDate);
//
//            ResultSet rs = cs.executeQuery();
//
//            while (rs.next()) {
//                PatientImage pi = new PatientImage();
//                pi.id = rs.getInt("Id");
//                pi.userID = rs.getString("userID");
//                pi.imageType = rs.getString("ImageType");
//                pi.ImageData = rs.getString("ImagePath");    // ✅ changed
//                pi.medicalTestName = rs.getString("medicalTestName");
//                pi.medicalTestResult = rs.getString("medicalTestResult");
//                pi.machineName = rs.getString("machineName");
//                pi.createDate = rs.getDate("CreateDate");
//                list.add(pi);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        if (list.isEmpty()) {
//            System.out.println("⚠️ No records found!");
//        }
//
//        return list;
//    }
//}
