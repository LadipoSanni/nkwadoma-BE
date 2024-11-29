package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
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
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationIdentity {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private int numberOfPrograms;
    private boolean isEnabled;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime timeUpdated;
    private List<ServiceOffering> serviceOfferings;
    private List<OrganizationEmployeeIdentity> organizationEmployees;
    private int numberOfLoanees;
    private BigDecimal totalDebtRepaid;
    private BigDecimal totalCurrentDebt;
    private BigDecimal totalHistoricalDebt;
    private double repaymentRate;
    private int pageSize;
    private int pageNumber;

    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;

    public void validate() throws MeedlException {
        log.info("The organization being validated : {}", this.name);
        log.info("{}",this.serviceOfferings);
        MeedlValidator.validateObjectName(this.name);
        MeedlValidator.validateEmail(this.email);
        MeedlValidator.validateDataElement(this.rcNumber);
        MeedlValidator.validateDataElement(this.phoneNumber);

        if (this.serviceOfferings == null
                || this.serviceOfferings.isEmpty()
                || this.serviceOfferings.get(0).getIndustry() == null) {
            log.error("{} : {}", INVALID_INDUSTRY_OR_SERVICE_OFFERING.getMessage(), this.serviceOfferings);
            throw new IdentityException(INVALID_INDUSTRY_OR_SERVICE_OFFERING.getMessage());
        }
        MeedlValidator.validateDataElement(this.serviceOfferings.get(0).getIndustry().name());
        log.info("Organization identity validation completed successfully {}", this.name);

    }

}
