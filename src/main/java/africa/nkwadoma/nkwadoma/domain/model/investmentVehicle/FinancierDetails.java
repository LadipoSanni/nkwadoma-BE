package africa.nkwadoma.nkwadoma.domain.model.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class FinancierDetails {
    private String id;
    private UserIdentity userIdentity;
    private OrganizationIdentity organizationIdentity;
    private NextOfKin nextOfKin;
    @Enumerated(EnumType.STRING)
    private FinancierType financierType;
}
