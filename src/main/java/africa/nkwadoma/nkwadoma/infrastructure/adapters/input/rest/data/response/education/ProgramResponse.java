package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;


import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import jakarta.validation.constraints.Size;
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
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private String name;
    private DurationType durationType;
    private LocalDate programStartDate;
    private int duration;
    private ProgramMode mode;
    private DeliveryType deliveryType;
    private ActivationStatus programStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int numberOfLoanees;
    private int numberOfCohort;
    private String createdBy;
    private String updatedBy;
    private String organizationId;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;
    private BigDecimal totalAmountDisbursed = BigDecimal.ZERO;
    private BigDecimal totalAmountOutstanding = BigDecimal.ZERO;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal repaymentRate;
    private BigDecimal debtPercentage;
}
