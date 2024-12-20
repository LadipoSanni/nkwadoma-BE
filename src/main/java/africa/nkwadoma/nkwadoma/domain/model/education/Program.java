package africa.nkwadoma.nkwadoma.domain.model.education;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.apache.commons.lang3.*;

import java.math.*;
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
    private int pageNumber;
    private int pageSize;
    private String organizationId;

    public int getPageSize() {
        int defaultPageSize = BigInteger.TEN.intValue();
        return this.pageSize == 0 ? defaultPageSize : this.pageSize;
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectName(this.name,"Name cannot be empty");
        MeedlValidator.validateUUID(this.createdBy,  MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        if (this.duration > 48){
            throw new MeedlException("Program duration must not exceed 48 months");
        }
        if (this.programDescription.length() > 2500) {
            throw new MeedlException("Program duration must not exceed 2500 characters");
        }
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
}
