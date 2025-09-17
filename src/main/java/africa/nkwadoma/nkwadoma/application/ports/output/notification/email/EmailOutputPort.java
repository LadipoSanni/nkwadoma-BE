package africa.nkwadoma.nkwadoma.application.ports.output.notification.email;


import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.notification.*;
import org.thymeleaf.context.*;

public interface EmailOutputPort {
    void sendEmail(Email email) throws MeedlException;

    Context getNameAndLinkContext(String link, String firstName);

    Context getFirstNameAndCompanyAndLinkContext(String link, String firstName, String companyName);

    Context getNameAndLinkContextAndIndustryName(String link, UserIdentity userIdentity, String industryName);

    Context getNameAndLinkContextAndInvestmentVehicleName(String link, String firstName, String investmentVehicleName);

    Context getNameAndLinkContextAndLoanOfferId(String firstName, String loanOfferId);

    Context getNameAndLinkContextAndLoanOfferIdAndLoaneeId(String firstName,String loanOfferId);
    Context getNameAndLinkContextAndIndustryNameAndLoanReferralId(String link,String loanReferralId, String firstName, String organizationName);


    Context getNameAndLinkContextAndIndustryNameAndCohortLoaneeId(String link, String cohortLoaneeId, String firstName, String organizationName);

    Context getNameAndDeactivationReasonContext(String firstName, String deactivationReason);

    Context getNameAndReactivationReasonContext(String link, String firstName, String reactivationReason);

    Context getFirstNameAndCompanyNameAndLinkContextAndInvestmentVehicleName(String financierToVehicleLink, String firstName, String investmentVehicleName, String companyName);

    Context getDeactivateOrganizationContext(String firstName, String name, String deactivationReason);
}
