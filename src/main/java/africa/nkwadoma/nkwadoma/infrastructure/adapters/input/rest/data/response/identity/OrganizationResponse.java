package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import lombok.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import lombok.Getter;
import lombok.Setter;

import java.math.*;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationResponse {
    private String id;
    private String name;
    private String email;
    private String websiteAddress;
    private String invitedDate;
    private String rcNumber;
    private String tin;
    private String phoneNumber;
    private int numberOfPrograms;
    private ActivationStatus status;
    private String createdBy;
    private int numberOfLoanees;
    private int numberOfCohort;
    private int loanRequestCount;
    private BigDecimal totalDebtRepaid;
    private BigDecimal totalCurrentDebt;
    private BigDecimal totalHistoricalDebt;
    private double repaymentRate;
    private List<ServiceOffering> serviceOfferings;
    private List<OrganizationEmployeeIdentity> organizationEmployees;
    private String logoImage;
    private String bannerImage;
    private String address;
}
