package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;

import java.math.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Program {
    private String id;
    @Size(max = 2500, message = "Program description must not exceed 2500 characters")
    private String programDescription;
    private String name;
    private DurationType durationType;
    private LocalDate programStartDate;
    private int duration;
    private int numberOfLoanees;
    private int numberOfCohort;
    private ProgramMode mode;
    private DeliveryType deliveryType;
    private ActivationStatus programStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private BigDecimal totalAmountRepaid = BigDecimal.ZERO;
    private BigDecimal totalAmountDisbursed = BigDecimal.ZERO;
    private BigDecimal totalAmountOutstanding = BigDecimal.ZERO;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private double repaymentRate;
    private double debtPercentage;
    private int pageNumber;
    private int pageSize;
    private String organizationId;
    private OrganizationIdentity organizationIdentity;

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == 0 ? defaultPageSize : this.pageSize;
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(this.name,"Program name cannot be empty","Program");
        MeedlValidator.validateUUID(this.createdBy,  MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        if (this.duration > 48){
            throw new MeedlException("Program duration must not exceed 48 months");
        }
        if (this.programDescription.length() > 2500) {
            throw new MeedlException("Program duration must not exceed 2500 characters");
        }
    }

    public void validateViewProgramByNameInput() throws MeedlException {
        MeedlValidator.validateUUID(createdBy, UserMessages.INVALID_USER_ID.getMessage());
    }

    public void setName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            this.name = name.trim();
        }
    }

    public BigDecimal getTotalAmountRepaid() {
        return totalAmountRepaid == null ? BigDecimal.ZERO : totalAmountRepaid;
    }

    public BigDecimal getTotalAmountDisbursed() {
        return totalAmountDisbursed == null ? BigDecimal.ZERO : totalAmountDisbursed;
    }

    public BigDecimal getTotalAmountOutstanding() {
        return totalAmountOutstanding == null ? BigDecimal.ZERO : totalAmountOutstanding;
    }

    public void validateUpdateProgram(Program foundProgram) throws EducationException {
        log.info("Validating program duration before edit");
        if(duration <= 0){
            log.info("Program duration can not be less than or equal to zero. Duration passed is : {} \n previous duration is {}", duration, foundProgram.getDuration());
            setDuration(foundProgram.getDuration());
//            throw new EducationException(ProgramMessages.PROGRAM_DURATION_CANNOT_BE_NEGATIVE.getMessage());
        }
    }
}
