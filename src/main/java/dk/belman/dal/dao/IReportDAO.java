package dk.belman.dal.dao;

import dk.belman.be.OperatorReport;
import dk.belman.be.Report;
import dk.belman.be.ReportImage;
import dk.belman.gui.pages.common.ReportItemModel;

import java.util.List;

public interface IReportDAO {
    /**
     * Opretter en ny rapport i databasen.
     *
     * @param report Rapporten som skal oprettes.
     * @return Retunerer sandt hvis oprettet, falsk hvis ikke.
     */
    boolean createReport(OperatorReport report) throws Exception;

    List<Report> getAll() throws Exception;

    Report getReport(ReportItemModel report) throws Exception;

    List<ReportImage> getReportImages(Report report) throws Exception;
}
