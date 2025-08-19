package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.DeliveryType;
import africa.nkwadoma.nkwadoma.domain.enums.ProgramMode;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProgramProjection {

     String getId();
     BigDecimal getTotalAmountRepaid();
     BigDecimal getTotalAmountDisbursed();
     BigDecimal getTotalAmountOutstanding();
     BigDecimal getTotalAmountRequested();
     LocalDate getProgramStartDate();
     String getProgramDescription();
     String getName();
     ProgramMode getMode();
     DeliveryType getDeliveryType();
     Integer getNumberOfCohort();
     Integer getNumberOfLoanees();
     Integer getDuration();
     ActivationStatus getProgramStatus();

}
