package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Program {
    private String id;
    private String programDescription;
    private String name;
    private String objectives;
    private DurationType durationType;
    private LocalDate programStartDate;
    private int duration;
    private int numberOfTrainees;
    private int numberOfCohort;
    private ProgramMode mode;
    private ProgramType programType;
    private DeliveryType deliveryType;
    private ActivationStatus programStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String organizationId;

    public void validate() throws MeedlException {
        MeedlValidator.validateDataElement(this.name);
        MeedlValidator.validateDataElement(this.createdBy);
        MeedlValidator.validateDataElement(this.organizationId);
    }
}
