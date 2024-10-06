package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loan;

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

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanProductAdapter implements LoanProductOutputPort {
    private final LoanProductEntityRepository loanProductEntityRepository;
    private final LoanProductMapper loanProductMapper;
    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException {
        validateLoanProduct(loanProduct);
        validateLoanProductDetails(loanProduct);
        LoanProductEntity loanProductEntity = loanProductMapper.mapLoanProductToEntity(loanProduct);
        LoanProductEntity savedLoanProductEntity = loanProductEntityRepository.save(loanProductEntity);
        loanProduct.setId(savedLoanProductEntity.getId());
        return loanProduct;
    }

    private void validateLoanProductDetails(LoanProduct loanProduct) throws MiddlException{
        if (StringUtils.isEmpty(loanProduct.getName())
                ||StringUtils.isEmpty(loanProduct.getMandate())
                ||loanProduct.getSponsors() == null
                ||loanProduct.getSponsors().isEmpty()
                ||loanProduct.getLoanProductSize() == null
//            ||loanProduct.getLoanProductSize().equals(0)
                ||loanProduct.getObligorLoanLimit() == null
//            ||loanProduct.getObligorLoanLimit().equals(0)
                ||loanProduct.getInterestRate() < 0
                ||loanProduct.getMoratorium() < 0
                ||loanProduct.getTenor() < 0
                ||loanProduct.getMinRepaymentAmount() == null
                ||StringUtils.isEmpty(loanProduct.getTermsAndCondition())
        ) {
            log.error("Invalid or empty request details to create loan product {} ",loanProduct);
            throw new LoanProductException("Invalid or empty request details to create loan product");
        }
    }

    private void validateLoanProduct(LoanProduct loanProduct)throws MiddlException {
        if (loanProduct == null) throw new LoanProductException("Invalid details provided");
    }
}
