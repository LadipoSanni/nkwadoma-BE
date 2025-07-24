package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanAdapter implements LoanOutputPort {
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    private final LoaneeLoanDetailRepository loaneeLoanDetailRepository;

    @Override
    public Loan save(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan, LoanMessages.LOAN_CANNOT_BE_EMPTY.getMessage());
        loan.validate();
        log.info("Loan input: {}", loan);
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
    public Optional<Loan> viewLoanById(String loanId) throws MeedlException {
        MeedlValidator.validateUUID(loanId, LoanMessages.INVALID_LOAN_ID.getMessage());
        LoanProjection loanById = loanRepository.findLoanById(loanId).orElse(null);
        Loan loan = loanMapper.mapProjectionToLoan(loanById);
        log.info("Loan details returned: {}", loan);
        return Optional.ofNullable(loan);
    }

    @Override
    public Optional<Loan> findLoanByLoanOfferId(String loaneeId) {
        Optional<LoanEntity> loanEntity = loanRepository.findByLoanOfferId(loaneeId);
        Optional<Loan> optionalLoan = loanEntity.map(loanMapper::mapToLoan);
        log.info("Loan details returned: {}", optionalLoan);
        return optionalLoan;
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
        Page<LoanProjection> loanProjectionPage = loanRepository.findAllLoanInOrganization(organizationId, PageRequest.of(pageNumber, pageSize));
        if (loanProjectionPage.isEmpty()) {
            log.info("Empty page returned");
            return Page.empty();
        }
        Page<Loan> loanPage = loanProjectionPage.map(loanMapper::mapProjectionToLoan);
        log.info("Mapped loan page: {}", loanPage.getContent());
        return loanPage;
    }

    @Override
    public Page<Loan> searchLoan(String programId, String organizationId, String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId,ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanProjection> loanProjectionPage =
                loanRepository.findAllLoanOfferByLoaneeNameInOrganizationAndProgram(programId,organizationId,name,pageRequest);
        Page<Loan> loans =  loanProjectionPage.map(loanMapper::mapProjectionToLoan);
        return loans;
    }

    @Override
    public Page<Loan> findAllLoan(int pageSize , int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanProjection> loanProjectionPage =
                loanRepository.findAllLoan(pageRequest);
        Page<Loan> loans =  loanProjectionPage.map(loanMapper::mapProjectionToLoan);
        return loans;
    }

    @Override
    public Page<Loan> filterLoanByProgram(String programId, String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId,ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanProjection> loanProjectionPage =
                loanRepository.filterLoanByProgramIdAndOrganization(programId,organizationId,pageRequest);
        Page<Loan> loans =  loanProjectionPage.map(loanMapper::mapProjectionToLoan);
        return loans;
    }

    @Override
    public String findLoanReferal(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loan id cannot be empty");

        log.info("Find loan referrer by loan id {}", id);
        LoanProjection loanProjection = loanRepository.findLoanReferralByLoanId(id);
        log.info("Found loan referrer by loan id {}", loanProjection);

        return loanProjection.getReferredBy();
    }

    @Override
    public Page<Loan> findAllLoanDisburedToLoanee(String id, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(id,UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        Page<LoanProjection> loanProjection =
                loanRepository.findAllLoanDisburestToLoanee(id,pageRequest);
        return loanProjection.map(loanMapper::mapProjectionToLoan);
    }

    @Override
    public Loan findLoaneeLoanByCohortLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Cohort loanee id cannot be empty");
        LoanEntity loanEntity = loanRepository.findByCohortLoaneeId(id);
        log.info("Found loan {}", loanEntity);
        return loanMapper.mapToLoan(loanEntity);
    }
}
