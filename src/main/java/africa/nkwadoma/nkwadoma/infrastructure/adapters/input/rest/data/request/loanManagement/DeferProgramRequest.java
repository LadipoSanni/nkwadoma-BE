package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
public class DeferProgramRequest {
    private String loaneeId;
    private String programId;
    private String cohortId;
    private String loanId;
    private String deferReason;
    private LocalDateTime deferredDateAndTime;
}
