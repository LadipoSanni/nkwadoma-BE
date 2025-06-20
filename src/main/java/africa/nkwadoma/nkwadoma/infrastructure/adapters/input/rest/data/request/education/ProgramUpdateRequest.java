package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProgramUpdateRequest {
    @NotBlank(message = "Program Id is required")
    private String id;
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private String name;
    private DurationType durationType;
    @Min(value = 1, message = "Program duration must be at least 1 month.")
    @Max(value = 48, message = "Program duration must not exceed 48 months.")
    private Integer duration;
    private ProgramMode mode;
    private DeliveryType deliveryType;

    public void setName(String name) {
        this.name = name.trim();
    }
}
