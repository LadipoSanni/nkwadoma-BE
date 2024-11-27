package africa.nkwadoma.nkwadoma.application.ports.output.email;


import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import org.thymeleaf.context.Context;

public interface EmailOutputPort {
    void sendEmail(Email email) throws MeedlException;
    Context getNameAndLinkContext(String link, String firstName);
    Context getNameAndLinkContextAndIndustryName(String link, String firstName,String industryName);

}
