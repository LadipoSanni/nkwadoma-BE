package africa.nkwadoma.nkwadoma.domain.service.loan;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.CreateLoanProductUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanProductOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanProductMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanProductEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanProductEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanProductException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProduct;
import static africa.nkwadoma.nkwadoma.domain.validation.LoanValidator.validateLoanProductDetails;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductService implements CreateLoanProductUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException {
        validateLoanProduct(loanProduct);
        validateLoanProductDetails(loanProduct);
        return loanProductOutputPort.save(loanProduct);
    }

}
