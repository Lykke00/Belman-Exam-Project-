package dk.belman.dal.dao;

import dk.belman.be.ReportImage;
import dk.belman.be.OperatorReport;
import dk.belman.dal.DBConnector;
import dk.belman.dal.IDBConnector;

import java.sql.*;

public class ReportDAO implements IReportDAO {
    private final IDBConnector connector;

    public ReportDAO() throws Exception {
        this.connector = new DBConnector();
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
}
