package africa.nkwadoma.nkwadoma.application.ports.output.email;


import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import org.thymeleaf.context.Context;

public interface EmailOutputPort {
    void sendEmail(Email email) throws MiddlException;
    Context getNameAndLinkContext(String link, String firstName);

}
