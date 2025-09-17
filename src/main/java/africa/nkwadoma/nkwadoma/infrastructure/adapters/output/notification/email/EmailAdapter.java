package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email;

import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.notification.ContextMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.meedlexception.MeedlNotificationException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.notification.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages.*;


@RequiredArgsConstructor
@Slf4j
public class EmailAdapter implements EmailOutputPort {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    @Value("${MAIL_SENDER}")
    private String mailSender;

    @Override
    public void sendEmail(Email email) throws MeedlException {
        try {
            String emailContent = templateEngine.process(email.getTemplate(), email.getContext());
            MimeMessage mailMessage = getMimeMessage(email, emailContent);
            mailMessage.setFrom(mailSender);
            javaMailSender.send(mailMessage);
        } catch (MessagingException | MailException | UnsupportedEncodingException exception) {
            throw new MeedlNotificationException(exception.getMessage());
        }
    }


    private MimeMessage getMimeMessage(Email email, String emailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailMessage, ENCODING_VALUE.getMessage());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setFrom(mailSender);
        mimeMessageHelper.setText(emailContent, true);
        log.info("{} ===>",mailMessage);
        log.info("{} ===>",mimeMessageHelper);

        return mailMessage;
    }
    @Override
    public Context getOrganizationNameAndUserNameAndLinkContext(String link, String firstName, String organizationName){
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(), organizationName);
        context.setVariable(ContextMessages.CONTEXT_CURRENT_YEAR.getMessage(), LocalDate.now().getYear());
        return context;
    }
    @Override
    public Context getUserFirstNameAndLinkContext(String link, String firstName){
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_CURRENT_YEAR.getMessage(), LocalDate.now().getYear());
        return context;
    }

    @Override
    public Context getFirstNameAndCompanyAndLinkContext(String link, String firstName, String companyName){
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(), companyName);
        context.setVariable(ContextMessages.CONTEXT_CURRENT_YEAR.getMessage(), LocalDate.now().getYear());
        return context;
    }

    @Override
    public Context getNameAndLinkContextAndIndustryName(String link, UserIdentity userIdentity, String organizationName) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), userIdentity.getFirstName());
        context.setVariable(ContextMessages.CONTEXT_ROLE.getMessage(),userIdentity.getRole().getRoleName());
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(),organizationName);
        return context;
    }
    @Override
    public Context getNameAndLinkContextAndIndustryNameAndLoanReferralId(String link,String loanReferralId, String firstName, String organizationName) {
        String requestParam = "?loanReferralId=" + loanReferralId;
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link+requestParam);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(),organizationName);
        return context;
    }
    @Override
    public Context getNameAndLinkContextAndIndustryNameAndCohortLoaneeId(String link, String cohortLoaneeId, String firstName, String organizationName) {
        String requestParam = "?cohortLoaneeId=" + cohortLoaneeId;
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link+requestParam);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(),organizationName);
        return context;
    }

    @Override
    public Context getNameAndDeactivationReasonContext(String firstName, String deactivationReason) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_DEACTIVATION_REASON.getMessage(), deactivationReason);
        return context;
    }
    @Override
    public Context getNameAndReactivationReasonContext(String link, String firstName, String reactivationReason) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_REACTIVATION_REASON.getMessage(), reactivationReason);
        context.setVariable(ContextMessages.CONTEXT_LINK.getMessage(), link);
        return context;
    }

    @Override
    public Context getFirstNameAndCompanyNameAndLinkContextAndInvestmentVehicleName(String financierToVehicleLink, String firstName, String investmentVehicleName, String companyName) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), financierToVehicleLink);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(), companyName);
        context.setVariable(ContextMessages.CONTEXT_VEHICLE_NAME.getMessage(),investmentVehicleName);
        return context;
    }

    @Override
    public Context getDeactivateOrganizationContext(String firstName, String name, String deactivationReason) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_DEACTIVATION_REASON.getMessage(), deactivationReason);
        context.setVariable(ContextMessages.CONTEXT_ORGANIZATION_NAME.getMessage(), name);
        return context;
    }

    @Override
    public Context getNameAndLinkContextAndInvestmentVehicleName(String link, String firstName, String investmentVehicleName) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_TOKEN.getMessage(), link);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        context.setVariable(ContextMessages.CONTEXT_VEHICLE_NAME.getMessage(),investmentVehicleName);
        return context;
    }

    @Override
    public Context getNameAndLinkContextAndLoanOfferId(String firstName, String loanOfferId) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_LOAN_OFFER_ID.getMessage(), loanOfferId);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        return context;
    }

    @Override
    public Context getNameAndLinkContextAndLoanOfferIdAndLoaneeId(String firstName,String loanOfferId) {
        Context context = new Context();
        context.setVariable(ContextMessages.CONTEXT_LOAN_OFFER_ID.getMessage(), loanOfferId);
        context.setVariable(ContextMessages.CONTEXT_FIRST_NAME.getMessage(), firstName);
        return context;
    }

}
