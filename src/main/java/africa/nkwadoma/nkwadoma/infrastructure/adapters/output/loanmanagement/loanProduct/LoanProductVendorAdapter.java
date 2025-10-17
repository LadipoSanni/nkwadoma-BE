package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductVendorOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanProductMessage;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProductVendor;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.LoanProductVendorMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.VendorMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductVendorEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoanProductVendorAdapter implements LoanProductVendorOutputPort {
    private final VendorMapper vendorMapper;
    private final LoanProductVendorRepository loanProductVendorRepository;
    private final LoanProductMapper loanProductMapper;
    private final LoanProductVendorMapper loanProductVendorMapper;

    @Override
    public List<LoanProductVendor> save(List<Vendor> vendors, LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateCollection(vendors, "Vendors to save to loan product cannot be empty");
        MeedlValidator.validateObjectInstance(loanProduct, LoanProductMessage.LOAN_PRODUCT_REQUIRED.getMessage());
        MeedlValidator.validateUUID(loanProduct.getId(), "Loan product id is required when adding vendors");
        validateVendorsId(vendors);
        LoanProductEntity loanProductEntity = loanProductMapper.map(loanProduct);

        log.info("Started saving loan product vendors ");
        return vendors.stream()
                    .map(vendorMapper::map)
                    .map(vendorEntity -> {
                        Optional<LoanProductVendorEntity> foundLoanProductVendor = loanProductVendorRepository.findByLoanProductEntityAndVendorEntity(loanProductEntity, vendorEntity);
                        String loanProductVendorId = null;
                        if (foundLoanProductVendor.isPresent()) {
                            loanProductVendorId = foundLoanProductVendor.get().getId();
                            log.info("Loan product vendor found with id {}", loanProductVendorId);
                        }


                        LoanProductVendorEntity loanProductVendorEntity = loanProductVendorRepository.save(LoanProductVendorEntity.builder()
                                .loanProductEntity(loanProductEntity)
                                .id(loanProductVendorId)
                                .vendorEntity(vendorEntity)
                                .build());
                        return loanProductVendorMapper.map(loanProductVendorEntity);
                    })
                    .toList();
    }

    private void validateVendorsId(List<Vendor> vendors) throws MeedlException {
        for (Vendor vendor: vendors){
            vendor.validateId();
        }
    }

    @Override
    public void deleteById(String loanProductVendorId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductVendorId, "Invalid loan product vendor id provided");
        loanProductVendorRepository.deleteById(loanProductVendorId);
    }
//    @Override
//    public LoanProductVendor findByLoanProductVendorId(String loanProductVendorId) throws MeedlException {
//        MeedlValidator.validateUUID(loanProductVendorId, "Invalid loan product vendor id provided");
//        return loanProductVendorRepository.findById(loanProductVendorId);
//    }
    @Override
    public List<Vendor> getVendorsByLoanProductId(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());
        List<LoanProductVendorEntity> loanProductVendorEntities = loanProductVendorRepository.findAllByLoanProductEntity_Id(loanProductId);

        return loanProductVendorEntities.stream()
                .map(LoanProductVendorEntity::getVendorEntity)
                .peek(vendorEntity -> log.info("The vendor name : {}, cost {}", vendorEntity.getVendorName(), vendorEntity.getCostOfService()))
                .map(vendorMapper::map)
                .toList();
    }

    @Override
    public List<LoanProductVendor> findAllByLoanProductId(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanProductMessage.INVALID_LOAN_PRODUCT_ID.getMessage());

        List<LoanProductVendorEntity> loanProductVendorEntities =
                loanProductVendorRepository.findAllByLoanProductEntity_Id(loanProductId);

        return loanProductVendorEntities.stream()
                .map(loanProductVendorMapper::map)
                .toList();
    }

    @Override
    public void deleteMultipleById(List<String> loanProductVendorIds) throws MeedlException {
        MeedlValidator.validateCollection(loanProductVendorIds, "Loan product vendor ids required");
        loanProductVendorRepository.deleteAllById(loanProductVendorIds);
    }

}
