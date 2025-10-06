package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanProduct;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanProduct.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanOfferMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeLoanDetailMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.loanProduct.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductRepository;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductVendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductRepository loanProductRepository;
    private final LoanProductVendorRepository loanProductVendorRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct save(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        loanProduct.validateLoanProductDetails();
        LoanProductEntity loanProductEntity = loanProductMapper.map(loanProduct);
        loanProductEntity.setCreatedAt(LocalDateTime.now());
        loanProductEntity.setTotalNumberOfLoanProduct(loanProductEntity.getTotalNumberOfLoanProduct() +BigInteger.ONE.intValue());
        LoanProductEntity savedLoanProductEntity = loanProductRepository.save(loanProductEntity);
        log.info("Loan product created {}",  loanProduct);
        loanProduct = loanProductMapper.map(savedLoanProductEntity);
        return loanProduct;
    }

    @Override
    public LoanProduct findLoanProductByLoanOfferId(String loanOfferId) throws MeedlException {
        MeedlValidator.validateUUID(loanOfferId, LoanOfferMessages.INVALID_LOAN_OFFER_ID.getMessage());
        LoanProductEntity loanProductEntity = loanProductRepository.findByLoanOfferId(loanOfferId);
        return loanProductMapper.map(loanProductEntity);
    }

    @Override
    public LoanProduct findByLoaneeLoanDetailId(String loaneeLoanDetailId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeLoanDetailId, LoaneeLoanDetailMessages.INVALID_LOANEE_LOAN_DETAIL_ID.getMessage());
        LoanProductEntity loanProductEntity = loanProductRepository.findLoanProductByLoaneeLoanDetailId(loaneeLoanDetailId);
        log.info("found loan product {}", loanProductEntity);
        return loanProductMapper.map(loanProductEntity);
    }

    @Override
    public int countLoanOfferFromLoanProduct(String loanProductId, List<LoanDecision> loanDecisions) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());
        return loanProductRepository.countLoanOfferFromLoanProduct(loanProductId, loanDecisions);
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
        log.info("loan product sponsors {}",entity);
        return loanProductMapper.map(entity);
    }

    @Override
    public LoanProduct findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, LoanMessages.LOAN_PRODUCT_NAME_REQUIRED.getMessage());
        LoanProductEntity entity = loanProductRepository.findByNameIgnoreCase(name).orElseThrow(()-> new LoanException("Loan product doesn't exist' whit this name " + name));
        return loanProductMapper.map(entity);
    }

    @Override
    public Page<LoanProduct> findAllLoanProduct(LoanProduct loanProduct) {
        int defaultPageSize = BigInteger.TEN.intValue();
        int size = loanProduct.getPageSize() <= BigInteger.ZERO.intValue() ? defaultPageSize : loanProduct.getPageSize();
        Pageable pageRequest = PageRequest.of(loanProduct.getPageNumber(), size, Sort.by(Sort.Order.desc("createdAt")));
        Page<LoanProductEntity> loanProductEntities = loanProductRepository.findAll(pageRequest);
        return loanProductEntities.map(loanProductMapper::map);
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
        return loanProductEntities.map(loanProductMapper::map);
    }

    @Override
    public LoanProduct findByCohortLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.INVALID_LOAN_PRODUCT_ID.getMessage());

        LoanProductEntity loanProductEntity = loanProductRepository.findByCohortLoaneeId(id);

        return loanProductMapper.map(loanProductEntity);
    }

}
