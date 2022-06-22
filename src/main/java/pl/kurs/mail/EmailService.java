package pl.kurs.mail;

import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.model.dto.ViolationToMailDto;
import freemarker.template.Configuration;
import pl.kurs.model.entity.TrafficViolation;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender sender;
    private final ModelMapper modelMapper;
    private final Configuration configuration;

    public String getEmailContent(ViolationToMailDto violation) throws IOException, TemplateException {
        StringWriter writer = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("violation", violation);
        configuration.getTemplate("email.ftlh").process(model, writer);
        return writer.getBuffer().toString();
    }

    @Async
    public void sendMail(TrafficViolation violation) throws TemplateException, IOException {
        try {
            ViolationToMailDto dto = modelMapper.map(violation, ViolationToMailDto.class);
            String emailContent = getEmailContent(dto);

            logger.info("Sending Email to {}", violation.getPerson().getEmail());

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setText(emailContent, true);
            mimeMessageHelper.setTo(violation.getPerson().getEmail());
            mimeMessageHelper.setSubject("Warning about your driving license");
            sender.send(mimeMessage);
        } catch (MessagingException ex) {
            logger.error("Failed to send email to {}", violation.getPerson().getEmail());
        }
    }

}
