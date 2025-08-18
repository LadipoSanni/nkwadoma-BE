package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramCreateRequest {
    @NotBlank(message = "Program name is required")
    @Size( max = 200, message = "Program name must not exceed 200 characters")
    private String programName;
    private String objectives;
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private ActivationStatus programStatus;
    @Positive(message = "Program duration must be a positive number")
    @NotNull(message = "Program duration is required.")
    @Min(value = 1, message = "Program duration must be at least 1 month.")
    @Max(value = 48, message = "Program duration must not exceed 48 months.")
    private BigInteger programDuration;
    private DeliveryType deliveryType;
    private ProgramMode programMode;
    private DurationType durationStatus;

    public void setProgramName(@NotBlank(message = "Program name is required") String programName) {
        this.programName = programName.trim();
    }
}
