package dk.belman.dal.dao;

import dk.belman.be.Report;
import dk.belman.be.ReportImage;
import dk.belman.be.OperatorReport;
import dk.belman.dal.DBConnector;
import dk.belman.dal.IDBConnector;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.common.ReportItemModel;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO implements IReportDAO {
    private final IDBConnector connector;

    public ReportDAO(IDBConnector connector) {
        this.connector = connector;
    }

    public ReportDAO() throws Exception {
        this.connector = new DBConnector();
    }

    private Report ReportFromResultSet(ResultSet rs) throws Exception {
        int reportId = rs.getInt("id");
        String orderNumber = rs.getString("order_number");
        String status = rs.getString("status");
        Timestamp createdDate = rs.getTimestamp("created_date");
        Timestamp updatedDate = rs.getTimestamp("update_date");
        //int operatorId = rs.getInt("operator_id");
        String workerId = rs.getString("workerId");
        int inspectedBy = rs.getInt("inspected_by");
        String inspectorComment = rs.getString("inspector_comment");

        LocalDateTime updatedDateConvert = updatedDate != null ? updatedDate.toLocalDateTime() : null;

        return new Report(reportId, orderNumber, workerId, createdDate.toLocalDateTime(), updatedDateConvert, status, inspectorComment);
    }

    /* * Denne metode opretter en rapport i databasen.
     * Den tager en OperatorReport som parameter, som indeholder information om rapporten og billederne.
     * Den håndterer transaktioner ved at sætte autocommit til false, og ruller tilbage ved fejl.
     */
    @Override
    public boolean createReport(OperatorReport report) throws Exception {
        // try-with-resources for at den automatisk lukker forbindelsen
        try (Connection conn = connector.getConnection()) {

            // sæt autocommit til false for at håndtere transaktioner
            conn.setAutoCommit(false);

            // SQL for at indsætte rapporten
            String insertReportSQL = """
                    INSERT INTO reports (order_number, created_date, operator_id)
                    VALUES (?, ?, ?)
                    """;

            // Forbered statement for at indsætte rapporten for at undgå SQL injektion
            try (PreparedStatement reportStmt = conn.prepareStatement(insertReportSQL, Statement.RETURN_GENERATED_KEYS)) {

                // Sæt data på spørgsmålstegnene i querien
                reportStmt.setString(1, report.getOrderNumber());
                reportStmt.setTimestamp(2, Timestamp.valueOf(report.getDate()));
                reportStmt.setInt(3, report.getOperator().getId());

                // prøv at kør query og se om der er nogle rækker der blev påvirket
                int affectedRows = reportStmt.executeUpdate();
                if (affectedRows == 0)
                    throw new Exception("Creating report failed, no rows affected.");

                int reportId;

                // tjekker resultatet for at få fat i den genereret primær nøgle, som databasen har lavet
                // altså id
                try (ResultSet generatedKeys = reportStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reportId = generatedKeys.getInt(1);
                    } else {
                        throw new Exception("Creating report failed, no ID obtained.");
                    }
                }

                // SQL for at indsætte billederne
                String insertImageSQL = """
                        INSERT INTO reports_images (report_id, picture, comment, angle)
                        VALUES (?, ?, ?, ?)
                    """;

                // Forbered statement for at indsætte billederne
                try (PreparedStatement imageStmt = conn.prepareStatement(insertImageSQL)) {
                    // looper igennem hvert billede fra rapporten og tilføjer den som en batch
                    for (ReportImage image : report.getPhotos()) {
                        imageStmt.setInt(1, reportId);
                        imageStmt.setBytes(2, image.getImage());
                        imageStmt.setString(3, image.getComment());
                        imageStmt.setString(4, image.getTakenFromAngle());

                        // tilføj til batchen
                        imageStmt.addBatch();
                    }

                    // når loopet er kørt igennem, så kør batchen
                    imageStmt.executeBatch();
                }

                // hvis vi når hertil, så er alt gået godt, og vi kan committe transaktionen
                conn.commit();
                return true;
            } catch (SQLException e) {
                // hvis der sker en fejl under indsættelse af rapporten eller billederne, så ruller vi tilbage
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
                SELECT *, u.workerId FROM reports
               JOIN users AS u ON reports.operator_id = u.id
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
                SELECT *, u.workerId FROM reports
                JOIN users AS u ON reports.operator_id = u.id
                WHERE reports.id = ?
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

    @Override
    public boolean updateReportStatus(Report report, ReportStatus status) throws Exception {
        String query = """
                UPDATE reports
                SET status = ?, update_date = ?
                WHERE id = ?
            """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status.toString());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, report.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                report.setStatus(status);
                return true;
            } else {
                throw new Exception("Failed to update report status");
            }
        } catch (Exception e) {
            throw new Exception("Failed to update report status", e);
        }
    }

    @Override
    public boolean updateInspectorComment(Report report, String comment) throws Exception {
        String query = """
                UPDATE reports
                SET inspector_comment = ?
                WHERE id = ?
            """;

        try (Connection conn = connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, comment);
            stmt.setInt(2, report.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                report.setInspectorComment(comment);
                return true;
            } else {
                throw new Exception("Failed to update inspector comment");
            }
        } catch (Exception e) {
            throw new Exception("Failed to update inspector comment", e);
        }
    }
}
