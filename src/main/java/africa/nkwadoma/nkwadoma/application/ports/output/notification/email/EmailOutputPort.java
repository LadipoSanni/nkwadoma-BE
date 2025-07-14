package africa.nkwadoma.nkwadoma.application.ports.output.notification.email;


import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.*;
import org.thymeleaf.context.*;

public interface EmailOutputPort {
    void sendEmail(Email email) throws MeedlException;

    Context getNameAndLinkContext(String link, String firstName);
    Context getNameAndLinkContextAndIndustryName(String link, String firstName, String industryName);

    Context getNameAndLinkContextAndInvestmentVehicleName(String link, String firstName, String investmentVehicleName);

    Context getNameAndLinkContextAndLoanOfferId(String firstName, String loanOfferId);

    Context getNameAndLinkContextAndLoanOfferIdAndLoaneeId(String firstName,String loanOfferId);
    Context getNameAndLinkContextAndIndustryNameAndLoanReferralId(String link,String loanReferralId, String firstName, String organizationName);


    Context getNameAndDeactivationReasonContext(String firstName, String deactivationReason);

    Context getDeactivatedOrganizationContext(String firstName,  String deactivationReason, String organizationName);
}
