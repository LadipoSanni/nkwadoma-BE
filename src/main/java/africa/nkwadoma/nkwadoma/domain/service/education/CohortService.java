package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;

import java.util.List;

@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;
    private final ProgramOutputPort programOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanDetailsOutputPort loanDetailsOutputPort;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;
    private final CohortMapper cohortMapper;


    @Override
    public Cohort createCohort(Cohort cohort)  throws MeedlException {
        String cohortName = cohort.getName();
        MeedlValidator.validateObjectInstance(cohort);
        cohort.validate();
        Program program = checkifCohortNameExistInProgram(cohort, cohortName);
        cohort.setCreatedAt(LocalDateTime.now());
        cohort.setNumberOfLoanees(0);
        activateStatus(cohort);
        ProgramCohort programCohort = new ProgramCohort();
        if (cohort.getLoanDetail() != null) {
            LoanDetail loanDetail = loanDetailsOutputPort.saveLoanDetails(cohort.getLoanDetail());
            cohort.setLoanDetail(loanDetail);
        }
        Cohort savedCohort = cohortOutputPort.save(cohort);
        List<LoanBreakdown> savedLoanBreakdowns = saveLoanBreakdown(cohort.getLoanBreakdowns(), savedCohort);
        linkCohortToProgram(program, programCohort, savedCohort);
        BigDecimal totalFee = calculateTotalLoanBreakdownAmount(savedLoanBreakdowns,cohort.getTuitionAmount());
        savedCohort.setTotalCohortFee(totalFee);
        savedCohort = cohortOutputPort.save(savedCohort);
        savedCohort.setLoanBreakdowns(savedLoanBreakdowns);
        return savedCohort;
    }

    private void linkCohortToProgram(Program program, ProgramCohort programCohort, Cohort savedCohort) throws MeedlException {
        program.setNumberOfCohort(program.getNumberOfCohort() + 1);
        programOutputPort.saveProgram(program);
        programCohort.setCohort(savedCohort);
        programCohort.setProgramId(program.getId());
        programCohortOutputPort.save(programCohort);
    }

    private Program checkifCohortNameExistInProgram(Cohort cohort, String cohortName) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        if (program == null) {
            throw new CohortException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohortList = programCohortOutputPort.findAllByProgramId(cohort.getProgramId());
        Optional<ProgramCohort> existingProgramCohort = programCohortList.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equals(cohortName))
                .findFirst();
        if (existingProgramCohort.isPresent()){
            throw new CohortException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
        }
        return program;
    }

    @Override
    public Cohort editCohort(Cohort cohort) throws MeedlException {
        cohort.updateValidation();
        Cohort foundCohort = cohortOutputPort.findCohort(cohort.getId());
        checkIfCohortNameExist(cohort, foundCohort);
        if (foundCohort.getLoanDetail() != null) {
            throw new CohortException(CohortMessages.COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED.getMessage());
        }
        cohortMapper.updateCohort(foundCohort,cohort);
        foundCohort.setUpdatedAt(LocalDateTime.now());
        activateStatus(foundCohort);
        List<LoanBreakdown> foundLoanBreakDown = loanBreakdownOutputPort.findAllByCohortId(cohort.getId());
        cohortOutputPort.save(foundCohort);
        foundCohort.setLoanBreakdowns(foundLoanBreakDown);
        return foundCohort;
    }

    private void checkIfCohortNameExist(Cohort cohort, Cohort foundCohort) throws MeedlException {
        Cohort foundCohortByName = null;
        if (cohort.getName() != null) {
            foundCohortByName = cohortOutputPort.findCohortByName(cohort.getName());
        }
        if (foundCohortByName != null) {
            if (!StringUtils.equals(foundCohort.getId(), foundCohortByName.getId())) {
                throw new CohortException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
            }
        }
    }

    private List<LoanBreakdown> saveLoanBreakdown(List<LoanBreakdown> breakdowns, Cohort savedCohort) {
        List<LoanBreakdown> loanBreakdowns = breakdowns.stream()
                .peek(loanBreakdownObject -> loanBreakdownObject.setCohort(savedCohort))
                .toList();
        return loanBreakdownOutputPort.saveAll(loanBreakdowns);
    }

    private static void activateStatus(Cohort cohort) {
        LocalDateTime now = LocalDateTime.now();
        if (cohort.getStartDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.INCOMING);
        } else if (cohort.getStartDate().isBefore(now) && cohort.getExpectedEndDate().isAfter(now)) {
            cohort.setActivationStatus(ActivationStatus.ACTIVE);
            cohort.setCohortStatus(CohortStatus.CURRENT);
        } else if (cohort.getExpectedEndDate().isBefore(now) || cohort.getExpectedEndDate().isEqual(now)) {
            cohort.setActivationStatus(ActivationStatus.INACTIVE);
            cohort.setCohortStatus(CohortStatus.GRADUATED);
        }
    }
    private BigDecimal calculateTotalLoanBreakdownAmount(List<LoanBreakdown> loanBreakdowns, BigDecimal tutionFee) {
        return loanBreakdowns.stream()
                .map(LoanBreakdown::getItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).add(tutionFee);
    }

    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId,programId,cohortId);
    }

    @Override
    public Page<Cohort> viewAllCohortInAProgram(String programId,int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Program foundProgram = programOutputPort.findProgramById(programId);
        if (ObjectUtils.isEmpty(foundProgram)) {
            throw new MeedlException(PROGRAM_NOT_FOUND.getMessage());
        }
        List<Cohort> cohorts = cohortOutputPort.findAllCohortInAProgram(programId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), cohorts.size());
        if (start >= cohorts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, cohorts.size());
        }
        return new PageImpl<>(cohorts.subList(start, end), pageRequest, cohorts.size());
    }

    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        cohortOutputPort.deleteCohort(id);
    }

    @Override
    public Cohort searchForCohortInAProgram(String cohortName, String programId) throws MeedlException {
        return cohortOutputPort.searchForCohortInAProgram(cohortName,programId);
    }

    @Override
    public void inviteCohort(String userId, String programId, String cohortId) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId,programId,cohortId);
        List<Loanee> cohortLoanees = loaneeOutputPort.findAllLoaneesByCohortId(foundCohort);
        cohortLoanees
                .forEach(this::inviteTrainee);

    }
    private void inviteTrainee(Loanee loanee){}
}
