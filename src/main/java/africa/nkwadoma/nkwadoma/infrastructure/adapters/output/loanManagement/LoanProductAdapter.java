package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceAlreadyExistsException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.Vendor;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductVendor;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.VendorEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductEntityRepository loanProductEntityRepository;
    private final LoanProductVendorRepository loanProductVendorRepository;
    private final VendorEntityRepository vendorEntityRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct save(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        if (existsByName(loanProduct.getName())){
            throw new ResourceAlreadyExistsException("Loan product " + loanProduct.getName() + " already exists");
        }
        List<Vendor> vendors = saveVendors(loanProduct);
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(loanProduct);
        loanProductEntity.setCreatedAt(LocalDateTime.now());
        loanProductEntity.setTotalNumberOfLoanProduct(loanProductEntity.getTotalNumberOfLoanProduct() +BigInteger.ONE.intValue());
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
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

    @Transactional
    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        LoanProductEntity loanProductEntity = loanProductEntityRepository.findById(id).orElseThrow(()-> new LoanException("Loan product doesn't exist"));
        loanProductVendorRepository.deleteAllByLoanProductEntity(loanProductEntity);
        loanProductEntityRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        return loanProductEntityRepository.existsByName(name);
    }

    @Override
    public LoanProduct findById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        LoanProductEntity entity = loanProductEntityRepository.findById(id).orElseThrow(()-> new LoanException("Loan product not found"));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }

    @Override
    public LoanProduct findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        LoanProductEntity entity = loanProductEntityRepository.findByName(name).orElseThrow(()-> new LoanException("Loan product doesn't exist' whit this name " + name));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }

    @Override
    public Page<LoanProduct> findAllLoanProduct(LoanProduct loanProduct) {
        int defaultPageSize = BigInteger.TEN.intValue();
        int size = loanProduct.getPageSize() <= BigInteger.ZERO.intValue() ? defaultPageSize : loanProduct.getPageSize();
        Pageable pageRequest = PageRequest.of(loanProduct.getPageNumber(), size, Sort.by(Sort.Order.asc("createdAt")));
        Page<LoanProductEntity> loanProductEntities = loanProductEntityRepository.findAll(pageRequest);
        return loanProductEntities.map(loanProductMapper::mapEntityToLoanProduct);
    }

    @Override
    public List<LoanProduct> search(String loanProductName) throws MeedlException {
        MeedlValidator.validateDataElement(loanProductName, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        List<LoanProductEntity> loanProductEntities = loanProductEntityRepository.findByNameContainingIgnoreCase(loanProductName);
        if (loanProductEntities.isEmpty()){
            return List.of();
        }
        return loanProductEntities.stream().map(loanProductMapper::mapEntityToLoanProduct).toList();
    }

}
