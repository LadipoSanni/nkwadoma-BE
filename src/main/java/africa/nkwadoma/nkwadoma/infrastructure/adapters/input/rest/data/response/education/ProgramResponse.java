package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;


import africa.nkwadoma.nkwadoma.domain.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.*;
import java.time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgramResponse{
    private String id;
    private String programDescription;
    private String name;
    private String objectives;
    private DurationType durationType;
    private LocalDate programStartDate;
    private int duration;
    private ProgramMode mode;
    private ProgramType programType;
    private DeliveryType deliveryType;
    private ActivationStatus programStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String organizationId;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;
    private BigDecimal totalAmountDisbursed = BigDecimal.ZERO;
    private BigDecimal totalAmountOutstanding = BigDecimal.ZERO;
}
