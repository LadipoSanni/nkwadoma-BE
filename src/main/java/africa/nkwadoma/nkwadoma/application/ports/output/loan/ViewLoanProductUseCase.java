package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import org.springframework.data.domain.Page;

public interface ViewLoanProductUseCase {

    Page<LoanProduct> viewAllLoanProduct(int pageSize, int pageNumber);
}
