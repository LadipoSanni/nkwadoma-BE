package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceAlreadyExistsException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.isEmptyString;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductEntityRepository loanProductEntityRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct save(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        if (existsByName(loanProduct.getName())){
            throw new ResourceAlreadyExistsException("Loan product " + loanProduct.getName() + " already exists");
        }
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(loanProduct);
        loanProductEntity.setCreatedAt(LocalDateTime.now());
        loanProductEntity.setTotalNumberOfLoanProduct(loanProductEntity.getTotalNumberOfLoanProduct() +BigInteger.ONE.intValue());
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
        log.info("Loan product created {}",  loanProduct);
        return loanProductMapper.mapEntityToLoanProduct(savedLoanProductEntity);
    }
    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateUUID(loanProduct.getId());
        LoanProduct foundLoanProduct = findById(loanProduct.getId());
        updateValues(foundLoanProduct, loanProduct);
        loanProduct.validateLoanProductDetails();
        if (foundLoanProduct.getTotalNumberOfLoanees() > BigInteger.ZERO.intValue()){
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(foundLoanProduct);
        loanProductEntity.setUpdatedAt(LocalDateTime.now());
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
        log.info("Loan product updated {}",  loanProduct);

        return loanProductMapper.mapEntityToLoanProduct(savedLoanProductEntity);
    }

    private void updateValues(LoanProduct foundLoanProduct, LoanProduct loanProduct) {
        if (isEmptyString(loanProduct.getName())) {
            loanProduct.setName(foundLoanProduct.getName());
        }
        if (isEmptyString(loanProduct.getTermsAndCondition())) {
            loanProduct.setTermsAndCondition(foundLoanProduct.getTermsAndCondition());
        }
        if (isEmptyString(loanProduct.getMandate())) {
            loanProduct.setMandate(foundLoanProduct.getMandate());
        }
        if(ObjectUtils.isEmpty(loanProduct.getLoanProductSize())){
            if (loanProduct.getLoanProductSize().compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
                loanProduct.setLoanProductSize(foundLoanProduct.getLoanProductSize());
            }
        }
        if(ObjectUtils.isEmpty(loanProduct.getObligorLoanLimit())){
            if (loanProduct.getObligorLoanLimit().compareTo(BigDecimal.ZERO) <= BigDecimal.ZERO.intValue()){
                loanProduct.setObligorLoanLimit(foundLoanProduct.getObligorLoanLimit());
            }
        }

    }

    @Override
    public void deleteById(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        loanProductEntityRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        return loanProductEntityRepository.existsByName(name);
    }

    @Override
    public LoanProduct findById(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        LoanProductEntity entity = loanProductEntityRepository.findById(id).orElseThrow(()-> new LoanException("Loan product doesn't exist"));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }

    @Override
    public LoanProduct findByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        LoanProductEntity entity = loanProductEntityRepository.findByName(name).orElseThrow(()-> new LoanException("Loan product doesn't exist' whit this name " + name));
        return loanProductMapper.mapEntityToLoanProduct(entity);
    }
}
