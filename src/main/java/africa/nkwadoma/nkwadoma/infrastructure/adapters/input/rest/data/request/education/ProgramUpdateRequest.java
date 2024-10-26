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
    private String programDescription;
    private String name;
    private DurationType durationType;
    private int duration;
    private ProgramMode mode;
    private DeliveryType deliveryType;

    public void setId(String id) {
        this.id = id.trim();
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription.trim();
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setDurationType(DurationType durationType) {
        if (durationType!= null) {
            String programDurationType = durationType.name().trim().toUpperCase();
            this.durationType = DurationType.valueOf(programDurationType);
        }
    }

    public void setMode(ProgramMode mode) {
        if (mode != null) {
            String programMode = mode.name().trim().toUpperCase();
            this.mode = ProgramMode.valueOf(programMode);
        }
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        if (deliveryType!= null) {
            String programDeliveryType = deliveryType.name().trim().toUpperCase();
            this.deliveryType = DeliveryType.valueOf(programDeliveryType);
        }
    }
}
