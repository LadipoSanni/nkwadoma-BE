package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanAdapter implements LoanOutputPort {
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    @Override
    public Loan save(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        loan.validate();
        log.info("Loan: {}", loan);
        LoanEntity loanEntity  = loanMapper.mapToLoanEntity(loan);
        loanEntity = loanRepository.save(loanEntity);
        return loanMapper.mapToLoan(loanEntity);
    }

    @Override
    public void deleteById(String loanId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        loanRepository.deleteById(loanId);
        log.info("Loan with id {} deleted successfully",loanId);
    }

    @Override
    public Loan findLoanById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoanMessages.INVALID_LOAN_ID.getMessage());
        LoanEntity foundLoanEntity = loanRepository.findById(id).orElseThrow(()->new LoanException("Could not find Loan"));
        return loanMapper.mapToLoan(foundLoanEntity);
    }

    @Override
    public Page<Loan> findAllByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.name());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Page<LoanProjection> loanProjectionPage = loanRepository.findAllByOrganizationId
                (organizationId, PageRequest.of(pageNumber, pageSize));
        if (loanProjectionPage.isEmpty()) {
            log.info("Empty page returned");
            return Page.empty();
        }
        Page<Loan> loanPage = loanProjectionPage.map(loanMapper::mapProjectionToLoan);
        log.info("Mapped loan page: {}", loanPage);
        return loanPage;
    }
}
