package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramCreateRequest {
    @NotBlank(message = "Program name is required")
    private String programName;
    private String objectives;
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private ActivationStatus programStatus;
    @Positive(message = "Program duration must be a positive number")
    @Max(value = 48, message = "Program duration must not exceed 48 months.")
    private BigInteger programDuration;
    private DeliveryType deliveryType;
    private ProgramMode programMode;
    private DurationType durationStatus;

    public void setProgramName(@NotBlank(message = "Program name is required") String programName) {
        this.programName = programName.trim();
    }
}
