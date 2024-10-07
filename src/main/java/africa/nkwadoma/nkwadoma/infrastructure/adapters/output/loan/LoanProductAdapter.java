package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProduct;
import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProductDetails;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductEntityRepository loanProductEntityRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct save(LoanProduct loanProduct)  {
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(loanProduct);
        loanProductEntity.setCreatedAtDate(LocalDateTime.now());
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
        loanProduct.setId(savedLoanProductEntity.getId());
        return loanProduct;
    }

}
