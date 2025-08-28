package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductVendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductRepository loanProductRepository;
    private final LoanProductVendorRepository loanProductVendorRepository;
    private final VendorEntityRepository vendorEntityRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct save(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        loanProduct.validateLoanProductDetails();
        List<Vendor> vendors = saveVendors(loanProduct);
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(loanProduct);
        loanProductEntity.setCreatedAt(LocalDateTime.now());
        loanProductEntity.setTotalNumberOfLoanProduct(loanProductEntity.getTotalNumberOfLoanProduct() +BigInteger.ONE.intValue());
        LoanProductEntity savedLoanProductEntity = loanProductRepository.save(loanProductEntity);
        savedLoanProductVendors(vendors, savedLoanProductEntity);
        log.info("Loan product created {}",  loanProduct);
        loanProduct = loanProductMapper.mapEntityToLoanProduct(savedLoanProductEntity);
        loanProduct.setVendors(vendors);
        return loanProduct;
    }
    private List<LoanProductVendor> savedLoanProductVendors(List<Vendor> vendors, LoanProductEntity savedLoanProductEntity) {
        if (vendors != null) {
            return vendors.stream()
                    .map(loanProductMapper::mapVendorToVendorEntity)
                    .map(vendorEntity -> loanProductVendorRepository.save(LoanProductVendor.builder()
                            .loanProductEntity(savedLoanProductEntity)
                            .vendorEntity(vendorEntity)
                            .build()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<Vendor> saveVendors(LoanProduct loanProduct) {
        if(loanProduct.getVendors() != null ) {
            return loanProduct.getVendors().stream()
                    .map(loanProductMapper::mapVendorToVendorEntity)
                    .map(vendorEntityRepository::save)
                    .map(loanProductMapper::mapVendorEntityToVendor)
                    .toList();
        }
        return List.of();
    }

    @Override
    public List<Vendor> getVendorsByLoanProductId(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        List<LoanProductVendor> loanProductVendors = loanProductVendorRepository.findAllByLoanProductEntity_Id(loanProductId);
        return loanProductVendors.stream()
                .map(LoanProductVendor::getVendorEntity)
                .map(loanProductMapper::mapVendorEntityToVendor)
                .toList();
    }

    @Transactional
    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        LoanProductEntity loanProductEntity = loanProductRepository.findById(id).orElseThrow(()-> new LoanException("Loan product doesn't exist"));
        loanProductVendorRepository.deleteAllByLoanProductEntity(loanProductEntity);
        loanProductRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) throws MeedlException {
        log.info("Checking if loan product with name {} exist on the platform", name);
        MeedlValidator.validateDataElement(name, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        return loanProductRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public LoanProduct findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        LoanProductEntity entity = loanProductRepository.findById(id).orElseThrow(()-> new LoanException("Loan product not found"));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }

    @Override
    public LoanProduct findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        LoanProductEntity entity = loanProductRepository.findByNameIgnoreCase(name).orElseThrow(()-> new LoanException("Loan product doesn't exist' whit this name " + name));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }

    @Override
    public Page<LoanProduct> findAllLoanProduct(LoanProduct loanProduct) {
        int defaultPageSize = BigInteger.TEN.intValue();
        int size = loanProduct.getPageSize() <= BigInteger.ZERO.intValue() ? defaultPageSize : loanProduct.getPageSize();
        Pageable pageRequest = PageRequest.of(loanProduct.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));
        Page<LoanProductEntity> loanProductEntities = loanProductRepository.findAll(pageRequest);
        return loanProductEntities.map(loanProductMapper::mapEntityToLoanProduct);
    }

    @Override
    public Page<LoanProduct> search(String loanProductName, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductName, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,Sort.by(Sort.Order.desc("createdAt")));
        Page<LoanProductEntity> loanProductEntities =
                loanProductRepository.findByNameContainingIgnoreCase(loanProductName,pageRequest);
        if (loanProductEntities.isEmpty()){
            return Page.empty();
        }
        return loanProductEntities.map(loanProductMapper::mapEntityToLoanProduct);
    }

    @Override
    public LoanProduct findByCohortLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());

        LoanProductEntity loanProductEntity = loanProductRepository.findByCohortLoaneeId(id);

        return loanProductMapper.mapEntityToLoanProduct(loanProductEntity);
    }

}
