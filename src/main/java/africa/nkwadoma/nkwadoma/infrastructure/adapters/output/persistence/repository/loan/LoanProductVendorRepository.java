package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductVendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductVendorRepository extends JpaRepository<LoanProductVendor,String> {
    void deleteAllByVendorEntity(VendorEntity vendorEntity);

    void deleteAllByLoanProductEntity(LoanProductEntity loanProductEntity);
}
