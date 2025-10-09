package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.VendorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VendorEntityRepository extends JpaRepository<VendorEntity,String> {

    Page<VendorEntity> findAllByVendorName(String vendorName, Pageable pageRequest);

    @Query("SELECT DISTINCT ps FROM VendorEntity v JOIN v.providerServices ps")
    Page<String> findAllProviderService(Pageable pageRequest);

    @Query("SELECT DISTINCT ps " +
            "FROM VendorEntity v JOIN v.providerServices ps " +
            "WHERE LOWER(ps) LIKE LOWER(CONCAT('%', :providerServiceName, '%'))")
    Page<String> findAllProviderServiceByName(@Param("providerServiceName") String providerServiceName,
                                              Pageable pageRequest);
}
