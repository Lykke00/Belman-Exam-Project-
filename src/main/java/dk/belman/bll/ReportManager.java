package dk.belman.bll;

import dk.belman.be.OperatorReport;
import dk.belman.dal.dao.IReportDAO;
import dk.belman.dal.dao.ReportDAO;

public class ReportManager {
    private final IReportDAO reportDAO;

    public ReportManager() throws Exception {
        this.reportDAO = new ReportDAO();
    }

    public boolean createReport(OperatorReport report) throws Exception {
        return reportDAO.createReport(report);
    }
}
