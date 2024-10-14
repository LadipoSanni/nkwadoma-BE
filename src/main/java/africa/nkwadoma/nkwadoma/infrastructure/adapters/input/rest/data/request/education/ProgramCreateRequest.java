package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.DeliveryType;
import africa.nkwadoma.nkwadoma.domain.enums.ProgramMode;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ProgramType;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramCreateRequest {
    private String programName;
    private String objectives;
    private String instituteId;
    private String creatorId;
    private LocalDateTime createdAt;
    private String updatedBy;
    private String programDescription;
    private ActivationStatus programStatus;
    @Positive(message = "Duration must be a positive number")
    private int programDuration;
    private DeliveryType deliveryType;
    private ProgramMode programMode;
    private ProgramType programType;
    private String durationStatus;
}
