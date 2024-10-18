package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
        loanProductEntity.setCreatedAtDate(LocalDateTime.now());
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
        return loanProductMapper.mapEntityToLoanProduct(savedLoanProductEntity);
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
