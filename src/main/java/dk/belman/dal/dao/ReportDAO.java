package dk.belman.dal.dao;

import dk.belman.be.Report;
import dk.belman.be.ReportImage;
import dk.belman.be.OperatorReport;
import dk.belman.dal.DBConnector;
import dk.belman.dal.IDBConnector;
import dk.belman.gui.pages.common.ReportItemModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {
    private final IDBConnector connector;

    public ReportDAO() throws Exception {
        this.connector = new DBConnector();
    }

    private Report ReportFromResultSet(ResultSet rs) throws Exception {
        int reportId = rs.getInt("id");
        String orderNumber = rs.getString("order_number");
        String status = rs.getString("status");
        Timestamp createdDate = rs.getTimestamp("created_date");
        Timestamp updatedDate = rs.getTimestamp("update_date");
        int operatorId = rs.getInt("operator_id");
        int inspectedBy = rs.getInt("inspected_by");

        return new Report(reportId, orderNumber, "" + operatorId, createdDate.toLocalDateTime(), updatedDate.toLocalDateTime(), status);
    }

    @Override
    public boolean createReport(OperatorReport report) throws Exception {
        try (Connection conn = connector.getConnection()) {
            conn.setAutoCommit(false);

            String insertReportSQL = """
                    INSERT INTO reports (order_number, created_date, operator_id)
                    VALUES (?, ?, ?)
                    """;

            try (PreparedStatement reportStmt = conn.prepareStatement(insertReportSQL, Statement.RETURN_GENERATED_KEYS)) {
                reportStmt.setString(1, report.getOrderNumber());
                reportStmt.setTimestamp(2, Timestamp.valueOf(report.getDate()));
                reportStmt.setInt(3, report.getOperator().getId());

                int affectedRows = reportStmt.executeUpdate();
                if (affectedRows == 0)
                    throw new Exception("Creating report failed, no rows affected.");

                int reportId;
                try (ResultSet generatedKeys = reportStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reportId = generatedKeys.getInt(1);
                    } else {
                        throw new Exception("Creating report failed, no ID obtained.");
                    }
                }

                String insertImageSQL = """
                        INSERT INTO reports_images (report_id, picture, comment, angle)
                        VALUES (?, ?, ?, ?)
                    """;

                try (PreparedStatement imageStmt = conn.prepareStatement(insertImageSQL)) {
                    for (ReportImage image : report.getPhotos()) {
                        imageStmt.setInt(1, reportId);
                        imageStmt.setBytes(2, image.getImage());
                        imageStmt.setString(3, image.getComment());
                        imageStmt.setString(4, image.getTakenFromAngle());
                        imageStmt.addBatch();
                    }

                    imageStmt.executeBatch();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new Exception("Creating report failed", e);
            }

        } catch (SQLException e) {
            throw new Exception("Database error", e);
        }
    }

    @Override
    public List<Report> getAll() throws Exception {
        List<Report> reports = new ArrayList<>();

        String query = """
                    SELECT * FROM reports
                """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(ReportFromResultSet(rs));
            }
            return reports;
        } catch (SQLException e) {
            throw new Exception("Couldn't fetch all reports from database", e);
        }
    }

    @Override
    public Report getReport(ReportItemModel report) throws Exception {
        String query = """
                SELECT * FROM reports
                WHERE id = ?
            """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, report.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Report reportFromDB = ReportFromResultSet(rs);
                    reportFromDB.setPhotos(getReportImages(reportFromDB));
                    return reportFromDB;
                } else {
                    throw new Exception("Report not found with ID: " + report.getId());
                }
            }
        }
    }

    @Override
    public List<ReportImage> getReportImages(Report report) throws Exception {
        List<ReportImage> reportImages = new ArrayList<>();
        String query = """
                    SELECT * FROM reports_images
                    WHERE report_id = ?
                """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, report.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    byte[] image = rs.getBytes("picture");
                    String comment = rs.getString("comment");
                    String angle = rs.getString("angle");

                    reportImages.add(new ReportImage(id, image, comment, angle));
                }
            }

            return reportImages;
        } catch (SQLException e) {
            throw new Exception("Couldn't fetch all report images from database", e);
        }
    }
}
