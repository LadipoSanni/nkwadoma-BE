package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorEntityRepository extends JpaRepository<VendorEntity,String> {
    VendorEntity findByVendorName(String vendorName);
}
