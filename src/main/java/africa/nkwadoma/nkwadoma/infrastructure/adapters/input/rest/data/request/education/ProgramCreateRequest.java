package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.DeliveryType;
import africa.nkwadoma.nkwadoma.domain.enums.ProgramMode;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ProgramType;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Program name is required")
    private String programName;
    private String objectives;
    @NotBlank(message = "Organization ID is required")
    private String instituteId;
    private String programDescription;
    private ActivationStatus programStatus;
    @Positive(message = "Program duration must be a positive number")
    private int programDuration;
    private DeliveryType deliveryType;
    private ProgramMode programMode;
    private String durationStatus;

    public void setProgramName(@NotBlank(message = "Program name is required") String programName) {
        this.programName = programName.trim();
    }

    public void setInstituteId(@NotBlank(message = "Organization ID is required") @Positive(message = "Organization ID must be a positive number") String instituteId) {
        this.instituteId = instituteId.trim();
    }
}
