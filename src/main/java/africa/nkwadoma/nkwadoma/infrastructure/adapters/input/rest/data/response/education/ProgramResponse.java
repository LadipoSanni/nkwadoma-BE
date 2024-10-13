package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;


import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramResponse{
    private String id;
    private String name;
    private String objectives;
    private String organizationId;
    private String creatorId;
    private LocalDateTime createdAt;
    private String updatedBy;
    private String programDescription;
    private ActivationStatus programStatus;
    private int duration;
    private DeliveryType deliveryType;
    private ProgramMode mode;
    private ProgramType programType;
    private DurationType durationType;
}
