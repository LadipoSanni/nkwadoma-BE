package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CohortResponse {

    private String id;
    private String programId;
    private String cohortDescription;
    private String name;
    private ActivationStatus cohortStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime startDate;
    private LocalDateTime expectedEndDate;

}
