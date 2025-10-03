package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductVendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LoanProductVendorRepository extends JpaRepository<LoanProductVendor,String> {
    void deleteAllByVendorEntity(VendorEntity vendorEntity);

    void deleteAllByLoanProductEntity(LoanProductEntity loanProductEntity);

    @Transactional
    void deleteByVendorEntityId(String id);

    List<LoanProductVendor> findAllByLoanProductEntity_Id(String loanProductEntityId);

    Optional<LoanProductVendor> findByLoanProductEntityAndVendorEntity(LoanProductEntity savedLoanProductEntity, VendorEntity vendorEntity);
}
