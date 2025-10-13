package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.DisbursementInterval;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EditDisbursementRuleRequest {
    private String id;
    private String name;
    private DisbursementInterval interval;
    private List<Double> percentageDistribution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
