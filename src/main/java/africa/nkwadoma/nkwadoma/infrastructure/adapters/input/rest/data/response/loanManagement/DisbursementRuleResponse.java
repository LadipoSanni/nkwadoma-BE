package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class DisbursementRuleResponse {
    private String id;
    private String name;
    private DisbursementInterval interval;
    private List<Double> percentageDistribution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
//    private LocalDateTime dateCreated;
//    private String createdBy;
    private ActivationStatus activationStatus;
}
