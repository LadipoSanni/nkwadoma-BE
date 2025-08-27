package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.OrganizationType;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.*;
import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.INVALID_INDUSTRY_OR_SERVICE_OFFERING;

@Slf4j
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationIdentity {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String taxIdentity;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private int numberOfPrograms;
    private boolean isEnabled;
    private String requestedBy;
    private LocalDateTime requestedInvitationDate;
    private String createdBy;
    private String updatedBy;
    private int loanRequestCount;
    private int loanDisbursalCount;
    private int loanOfferCount;
    private int loanReferralCount;
    private LoanType loanType;
    private LocalDateTime timeUpdated;
    private ActivationStatus activationStatus;
    private UserIdentity userIdentity;
    private List<OrganizationEmployeeIdentity> organizationEmployees;
    private int numberOfLoanees;
    private int stillInTraining;
    private int numberOfCohort;
    private BigDecimal totalDebtRepaid;
    private BigDecimal totalCurrentDebt;
    private BigDecimal totalHistoricalDebt;
    private BigDecimal totalAmountReceived = BigDecimal.ZERO;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private double repaymentRate;
    private double debtPercentage;
    private int pendingLoanOfferCount;
    private int pageSize;
    private int pageNumber;
    private String actorId;
    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;
    private OrganizationType organizationType;
    private List<ServiceOffering> serviceOfferings;
    private List<BankDetail> bankDetails;

    public void validate() throws MeedlException {
        log.info("The organization being validated : {}", this.name);
        log.info("{}",this.serviceOfferings);
        MeedlValidator.validateObjectName(this.name,"Organization name cannot be empty","Organization");
        MeedlValidator.validateEmail(this.email);
        MeedlValidator.validateDataElement(this.rcNumber, OrganizationMessages.RC_NUMBER_IS_REQUIRED.getMessage());
        MeedlValidator.validateRCNumber(this.rcNumber);
        MeedlValidator.validateTin(this.tin);
        MeedlValidator.validateDataElement(this.phoneNumber, "Phone number is required");
    }

    public void validateCooperateOrganization() throws MeedlException {
        MeedlValidator.validateObjectName(this.name,"Organization name cannot be empty","Organization");
        MeedlValidator.validateEmail(this.email);
        log.info("Validation for cooperate organization done");
    }

}
