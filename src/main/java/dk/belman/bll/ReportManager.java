package dk.belman.bll;

import dk.belman.be.OperatorReport;
import dk.belman.be.Report;
import dk.belman.dal.dao.IReportDAO;
import dk.belman.dal.dao.ReportDAO;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.common.ReportItemModel;

import java.util.List;

public class ReportManager {
    private final IReportDAO reportDAO;

    public ReportManager(IReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    public ReportManager() throws Exception {
        this.reportDAO = new ReportDAO();
    }

    public List<Report> getAll() throws Exception {
        return reportDAO.getAll();
    }

    public boolean createReport(OperatorReport report) throws Exception {
        return reportDAO.createReport(report);
    }

    public Report getReport(ReportItemModel report) throws Exception {
        return reportDAO.getReport(report);
    }

    public boolean updateReportStatus(Report report, ReportStatus status) throws Exception
    {
        return reportDAO.updateReportStatus(report, status);
    }

    public boolean updateInspectorComment(Report report, String comment) throws Exception
    {
        return reportDAO.updateInspectorComment(report, comment);
    }
}
