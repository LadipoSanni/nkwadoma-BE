package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanBreakdownEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_INVITED;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.loan.SuccessMessages.LOANEE_HAS_BEEN_REFERED;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CohortService implements CohortUseCase {

    private final CohortOutputPort cohortOutputPort;
    private final ProgramOutputPort programOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanDetailsOutputPort loanDetailsOutputPort;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeUseCase loaneeUseCase;


    @Override
    public Cohort createCohort(Cohort cohort) throws MeedlException {
        String cohortName = cohort.getName();
        MeedlValidator.validateObjectInstance(cohort);
        cohort.validate();
        cohort.validateLoanBreakDowns();
        Program program = checkifCohortNameExistInProgram(cohort, cohortName);
        cohort.setCreatedAt(LocalDateTime.now());
        cohort.setNumberOfLoanees(0);
        ProgramCohort programCohort = new ProgramCohort();
        cohort.setExpectedEndDate(cohort.getStartDate().plusMonths(program.getDuration()));
        activateStatus(cohort);
        Cohort savedCohort = cohortOutputPort.save(cohort);
        List<LoanBreakdown> savedLoanBreakdowns = saveLoanBreakdown(cohort.getLoanBreakdowns(), savedCohort);
        linkCohortToProgram(program, programCohort, savedCohort);
        BigDecimal totalFee = calculateTotalLoanBreakdownAmount(savedLoanBreakdowns, cohort.getTuitionAmount());
        savedCohort.setTotalCohortFee(totalFee);
        savedCohort.setOrganizationId(program.getOrganizationId());
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
        if (existingProgramCohort.isPresent()) {
            throw new CohortException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
        }
        return program;
    }

    @Override
    public Cohort editCohort(Cohort cohort) throws MeedlException {
        cohort.updateValidation();
        Cohort foundCohort = cohortOutputPort.findCohort(cohort.getId());
        Program program = programOutputPort.findProgramById(foundCohort.getProgramId());
        checkIfCohortNameExist(cohort, foundCohort);
        if (!ObjectUtils.isEmpty(foundCohort.getLoanDetail())) {
            throw new CohortException(CohortMessages.COHORT_WITH_LOAN_DETAILS_CANNOT_BE_EDITED.getMessage());
        }
        cohortMapper.updateCohort(foundCohort, cohort);
        foundCohort.setUpdatedAt(LocalDateTime.now());
        foundCohort.setExpectedEndDate(foundCohort.getStartDate().plusMonths(program.getDuration()));
        activateStatus(foundCohort);
        List<LoanBreakdown> foundLoanBreakDown = loanBreakdownOutputPort.findAllByCohortId(cohort.getId());
        cohortOutputPort.save(foundCohort);
        foundCohort.setLoanBreakdowns(foundLoanBreakDown);
        return foundCohort;
    }

    private void checkIfCohortNameExist(Cohort cohort, Cohort foundCohort) throws MeedlException {
        Cohort foundCohortByName = null;
        if (! StringUtils.isEmpty(cohort.getName())) {
            foundCohortByName = cohortOutputPort.checkIfCohortExistWithName(cohort.getName());
        }
        if (ObjectUtils.isNotEmpty(foundCohortByName)) {
            if (!StringUtils.equals(foundCohort.getId(), foundCohortByName.getId())) {
                throw new CohortException(CohortMessages.COHORT_WITH_NAME_EXIST.getMessage());
            }
        }
    }

    private List<LoanBreakdown> saveLoanBreakdown(List<LoanBreakdown> breakdowns, Cohort savedCohort) {
        List<LoanBreakdown> loanBreakdowns = breakdowns.stream()
                .peek(loanBreakdownObject -> loanBreakdownObject.setCohort(savedCohort))
                .toList();
        return loanBreakdownOutputPort.saveAllLoanBreakDown(loanBreakdowns);
    }

    private static void activateStatus(Cohort cohort) {
        LocalDate now = LocalDate.now();
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
    public Cohort viewCohortDetails(String userId,  String cohortId) throws MeedlException {
        return cohortOutputPort.viewCohortDetails(userId, cohortId);
    }

    @Override
    public Page<Cohort> viewAllCohortInAProgram(String programId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Program foundProgram = programOutputPort.findProgramById(programId);
        if (ObjectUtils.isEmpty(foundProgram)) {
            throw new MeedlException(PROGRAM_NOT_FOUND.getMessage());
        }
        return cohortOutputPort.findAllCohortInAProgram(programId, pageSize, pageNumber);
    }

    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        cohortOutputPort.deleteCohort(id);
    }

    @Override
    public List<Cohort> searchForCohortInAProgram(String cohortName, String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId);
        MeedlValidator.validateDataElement(cohortName);
        return cohortOutputPort.searchForCohortInAProgram(cohortName,programId);
    }


    @Override
    public List<Cohort> searchForCohort(String userId, String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        MeedlValidator.validateUUID(userId);
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)){
            OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(userId);
            return cohortOutputPort.searchCohortInOrganization(organizationIdentity.getId(),name);
        }
        return cohortOutputPort.findCohortByName(name);
    }

    @Override
    public Page<Cohort> viewAllCohortInOrganization(String actorId,
                                                    int pageNumber,int pageSize) throws MeedlException {
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(actorId);
        return cohortOutputPort.findAllCohortByOrganizationId(organizationIdentity.getId(),pageSize,pageNumber);
    }

    @Override
    public List<LoanBreakdown> getCohortLoanBreakDown(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId);
         return loanBreakdownOutputPort.findAllByCohortId(cohortId);
    }

    @Override
    public String inviteCohort(String userId, String cohortId, List<String> loaneeIds) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId,cohortId);
        List<Loanee> cohortLoanees = loaneeOutputPort.findSelectedLoaneesInCohort(foundCohort.getId(), loaneeIds);
        if (cohortLoanees == null || cohortLoanees.isEmpty()){
            log.info("No loanee in the cohort is assigned to be referred");
            throw new MeedlException("No loanee in the cohort is assigned to be referred");
        }
        if (cohortLoanees.size() == 1){
            inviteTrainee(cohortLoanees.get(0));
            return LOANEE_HAS_BEEN_REFERED ;
        }

        for (Loanee loanee : cohortLoanees) {
            try {
                inviteTrainee(loanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee: {}", loanee.getId(), e);
            }
        }
        return COHORT_INVITED;
    }

        private void inviteTrainee (Loanee loanee) throws MeedlException {
            loaneeUseCase.referLoanee(loanee.getId());
        }

}