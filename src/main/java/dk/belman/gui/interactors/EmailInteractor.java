package dk.belman.gui.interactors;

import dk.belman.bll.EmailSender;
import dk.belman.bll.ReportManager;
import dk.belman.gui.common.ReportItemModel;
import dk.belman.gui.modals.sendemail.SendEmailModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;
import dk.belman.gui.utils.PDFGenerator;

import java.util.function.Consumer;

public class EmailInteractor {
    private SendEmailModel sendEmailModel;
    private EmailSender emailSender;

    private ReportManager reportManager;

    public EmailInteractor() {
        this.sendEmailModel = new SendEmailModel();
        try {
            this.emailSender = new EmailSender();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Email Sender Initialization failed", "Failed to initialize the email sender. Please check your configuration.", e);
        }

        try {
            this.reportManager = new ReportManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Report Manager Initialization failed", "Failed to initialize the report manager. Please check your configuration.", e);
        }
    }

    public void fetchImagesForReport() {
        ReportItemModel report = sendEmailModel.getReportItemModel();

        if (report == null) {
            DialogHandler.showExceptionError("Email Error", "No report selected for sending.", null);
            return;
        }

        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> reportManager.getReport(report),
                dbReport -> {
                    this.sendEmailModel.reportItemModelProperty().set(ReportItemModel.fromEntity(dbReport));
                },
                error -> {
                    DialogHandler.showExceptionError("Image Fetching Error",
                            "An error occurred while fetching images for the QC report.", error);
                },
                loading -> {
                    sendEmailModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    public void sendQCReportEmail(String recipientEmail, String extraMessage, Consumer<Boolean> callback) {
        ReportItemModel report = sendEmailModel.getReportItemModel();

        if (report == null) {
            DialogHandler.showExceptionError("Email Error", "No report selected for sending.", null);
            callback.accept(false);
            return;
        }

        if (emailSender == null) {
            DialogHandler.showExceptionError("Email Error", "Email sender not initialized.", null);
            callback.accept(false);
            return;
        }

        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> {
                    byte[] pdfBytes = PDFGenerator.generatePdfWithImages(report.getOrderNumber(), report.getInspectedBy(), report.getImages());

                    if (pdfBytes.length == 0)
                        throw new Exception("Failed to generate PDF");

                    return emailSender.sendQCReport(
                            recipientEmail,
                            report.getOrderNumber(),
                            extraMessage,
                            pdfBytes
                    );
                },
                callback,
                error -> {
                    callback.accept(false);
                    DialogHandler.showExceptionError("Email Sending Error",
                            "An error occurred while sending the QC report email.", error);
                },
                loading -> {
                    sendEmailModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    public SendEmailModel getSendEmailModel() {
        return sendEmailModel;
    }
}
