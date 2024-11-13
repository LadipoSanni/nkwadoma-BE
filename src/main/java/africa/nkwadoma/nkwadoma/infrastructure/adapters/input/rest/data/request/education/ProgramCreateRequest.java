package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
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
    private String programDescription;
    private ActivationStatus programStatus;
    @Positive(message = "Program duration must be a positive number")
    private int programDuration;
    private DeliveryType deliveryType;
    private ProgramMode programMode;
    private DurationType durationStatus;

    public void setProgramName(@NotBlank(message = "Program name is required") String programName) {
        this.programName = programName.trim();
    }
}
