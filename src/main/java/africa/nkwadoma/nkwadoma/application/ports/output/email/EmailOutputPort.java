package africa.nkwadoma.nkwadoma.application.ports.output.email;


import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.email.*;
import org.thymeleaf.context.*;

public interface EmailOutputPort {
    void sendEmail(Email email) throws MeedlException;

    Context getNameAndLinkContext(String link, String firstName);

}
