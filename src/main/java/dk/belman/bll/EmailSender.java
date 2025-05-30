package dk.belman.bll;

import com.resend.*;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Properties;

public class EmailSender {

    private Resend resend;

    public EmailSender() throws Exception {
        this("config/config.settings");
    }

    public EmailSender(String propertiesFilePath) throws Exception {
        var properties = new Properties();
        properties.load(new FileInputStream(propertiesFilePath));

        resend = new Resend(properties.getProperty("resend.apiKey"));
    }

    private String loadCssFromResource() {
        StringBuilder contentBuilder = new StringBuilder();
        try (InputStream inputStream = this.getClass().getResourceAsStream("/css/email.css");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null)
                contentBuilder.append(line).append("\n");

        } catch (IOException e) {
            throw new RuntimeException("Error trying to read /css/email.css file\n " + e);
        }

        return contentBuilder.toString();
    }

    private TemplateEngine templateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("html/");
        templateResolver.setSuffix(".html");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }

    private boolean sendEmailWithAttachment(String recipient, String subject, String content, Attachment attachment) throws ResendException {
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("Belman <belman@leet.dk>")
                .to(recipient)
                .subject(subject)
                .addAttachment(attachment)
                .html(content)
                .build();
        try {
            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            return true;
        } catch (ResendException e) {
            throw new ResendException("Error occurred trying to send email:\n" + e);
        }
    }

    public Attachment addImageAttachment(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String encodedContent = Base64.getEncoder().encodeToString(fileContent);
        return Attachment.builder()
                .fileName(file.getName())
                .content(encodedContent)
                .build();
    }

    public Attachment addPdfAttachment(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String encodedContent = Base64.getEncoder().encodeToString(fileContent);
        return Attachment.builder()
                .fileName(file.getName())
                .content(encodedContent)
                .build();
    }

    public Attachment addPdfBytes(byte[] fileContent, String fileName) {
        String encodedContent = Base64.getEncoder().encodeToString(fileContent);
        return Attachment.builder()
                .fileName(fileName)
                .content(encodedContent)
                .build();
    }

    /**
     * Send QC Report with PDF generated from PictureItemModel data
     */
    public boolean sendQCReport(String recipient, String orderId, String extraMessage, byte[] pdfBytes) throws Exception {
        Attachment pdfAttachment = addPdfBytes(pdfBytes, "QC_Report_" + orderId + ".pdf");

        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("cssContent", loadCssFromResource());
        if (!extraMessage.isEmpty())
            context.setVariable("extraMsg", extraMessage);

        String pickedTemplate = extraMessage.isEmpty() ? "report" : "reportWithMsg";
        String emailContent = templateEngine().process(pickedTemplate, context);

        return sendEmailWithAttachment(
                recipient,
                "QC Report for Order: " + orderId,
                emailContent,
                pdfAttachment
        );
    }

    private boolean sendEmail(String recipient, String subject, String content) throws ResendException {
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("belman@leet.dk")
                .to(recipient)
                .subject(subject)
                .html(content)
                .build();
        try {
            SendEmailResponse data = resend.emails().send(sendEmailRequest);
            return true;
        } catch (ResendException e) {
            throw new ResendException("Error occurred trying to send email:\n" + e);
        }
    }
}