package dk.belman.dal.dao;

import dk.belman.be.OperatorReport;

public interface IReportDAO {
    /**
     * Opretter en ny rapport i databasen.
     *
     * @param report Rapporten som skal oprettes.
     * @return Retunerer sandt hvis oprettet, falsk hvis ikke.
     */
    boolean createReport(OperatorReport report) throws Exception;
}
