package africa.nkwadoma.nkwadoma.domain.model.identity;

import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_IDENTITY_CANNOT_BE_NULL;
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

    private int pageSize;
    private int pageNumber;

    private String logoImage;
    private String bannerImage;
    private String address;
    private String officeAddress;

    public void validate() throws MeedlException {
        log.info("The organization being validated : {}", this.name);
        log.info("{}",this.serviceOfferings);

        if (this.serviceOfferings == null
                || this.serviceOfferings.isEmpty()
                || this.serviceOfferings.get(0).getIndustry() == null) {
            log.error("{} : {}", INVALID_INDUSTRY_OR_SERVICE_OFFERING.getMessage(), this.serviceOfferings);
            throw new IdentityException(INVALID_INDUSTRY_OR_SERVICE_OFFERING.getMessage());
        }
        MeedlValidator.validateEmail(this.email);
        MeedlValidator.validateDataElement(this.name);
        MeedlValidator.validateDataElement(this.serviceOfferings.get(0).getIndustry().name());
        MeedlValidator.validateDataElement(this.rcNumber);
        MeedlValidator.validateDataElement(this.phoneNumber);
        log.info("Organization identity validation completed successfully {}", this.name);

    }

}
