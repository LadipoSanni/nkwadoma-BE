package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import java.math.BigDecimal;

public interface ProgramProjection {

     String getId();
     BigDecimal getTotalAmountRepaid();
     BigDecimal getTotalAmountDisbursed();
     BigDecimal getTotalAmountOutstanding();
     BigDecimal getTotalAmountRequested();
}
