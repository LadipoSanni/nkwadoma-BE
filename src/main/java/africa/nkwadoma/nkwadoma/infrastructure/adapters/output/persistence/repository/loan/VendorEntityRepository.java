package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VendorEntityRepository extends JpaRepository<VendorEntity,String> {
    VendorEntity findByVendorName(String vendorName);

    @Query("SELECT DISTINCT ps FROM VendorEntity v JOIN v.providerServices ps")
    Page<String> findAllProviderService(Pageable pageRequest);
}
