package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.LoaneeUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.CohortStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.EnableAsync;
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
@EnableAsync
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
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
//    private final AsynchronousMailingOutputPort asynchronousMailingOutputPort;

    @Override
    public Cohort createCohort(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, CohortMessages.INPUT_CANNOT_BE_NULL.getMessage());
        cohort.validate();
        String cohortName = cohort.getName();
        cohort.validateLoanBreakDowns();
        log.info("Creating cohort with name {} at service level", cohortName);
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
        savedCohort.setProgramName(program.getName());
        organizationIdentityOutputPort.updateNumberOfCohortInOrganization(program.getOrganizationId());
        return savedCohort;
    }

    private void linkCohortToProgram(Program program, ProgramCohort programCohort, Cohort savedCohort) throws MeedlException {
        log.info("Linking cohort to program {}", program.getName());
        program.setNumberOfCohort(program.getNumberOfCohort() + 1);
        log.info("Service offering of organization is : {} ", program.getOrganizationIdentity().getServiceOfferings());
        programOutputPort.saveProgram(program);
        programCohort.setCohort(savedCohort);
        programCohort.setProgramId(program.getId());
        programCohortOutputPort.save(programCohort);
    }

    private Program checkifCohortNameExistInProgram(Cohort cohort, String cohortName) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        log.info("Checking if cohort name exists in program found : {}", program);
        if (ObjectUtils.isEmpty(program)) {
            log.info("Program selected for cohort creation was not found. Cohort name {}, {}",cohortName, ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
            throw new CohortException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohortList = programCohortOutputPort.findAllByProgramId(cohort.getProgramId());
        Optional<ProgramCohort> existingProgramCohort = programCohortList.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equalsIgnoreCase(cohortName))
                .findFirst();
        if (existingProgramCohort.isPresent()) {
            log.info("Cohort with name {} already exists in program. Cohort id {}", cohortName, existingProgramCohort.get().getId());
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
        foundCohort.setProgramName(program.getName());
        return foundCohort;
    }

    private void checkIfCohortNameExist(Cohort cohort, Cohort foundCohort) throws MeedlException {
        Cohort foundCohortByName = null;
        if (StringUtils.isNotEmpty(cohort.getName())) {
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
        } else if (cohort.getStartDate().isBefore(now) && cohort.getExpectedEndDate().isAfter(now)
                || cohort.getStartDate().equals(now) && cohort.getExpectedEndDate().isAfter(now)) {
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
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        Cohort cohort = cohortOutputPort.viewCohortDetails(userId, cohortId);
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        cohort.setProgramName(program.getName());
        return cohort;
    }

    @Override
    public Page<Cohort> viewAllCohortInAProgram(String programId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Program foundProgram = programOutputPort.findProgramById(programId);
        if (ObjectUtils.isEmpty(foundProgram)) {
            log.error("While trying to view all cohort in a program, the program {} was not found.", programId);
            throw new MeedlException(PROGRAM_NOT_FOUND.getMessage());
        }
        return cohortOutputPort.findAllCohortInAProgram(programId, pageSize, pageNumber);
    }

    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<Loanee> loanees = loaneeOutputPort.findAllLoaneesByCohortId(id);
        if (ObjectUtils.isNotEmpty(loanees)) {
            throw new CohortException(CohortMessages.COHORT_WITH_LOANEE_CANNOT_BE_DELETED.getMessage());
        }
        Cohort cohort = cohortOutputPort.findCohort(id);
        cohortOutputPort.deleteCohort(cohort.getId());
        Program program = decreaseNumberOfCohortInProgram(cohort);
        decreaseNumberOfCohortInOrganization(program);
    }

    private void decreaseNumberOfCohortInOrganization(Program program) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId());
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() - 1);
        organizationIdentityOutputPort.save(organizationIdentity);
    }

    private Program decreaseNumberOfCohortInProgram(Cohort cohort) throws MeedlException {
        Program program = programOutputPort.findProgramById(cohort.getProgramId());
        program.setNumberOfCohort(program.getNumberOfCohort() - 1);
        programOutputPort.saveProgram(program);
        return program;
    }

    @Override
    public List<Cohort> searchForCohortInAProgram(String cohortName, String programId) throws MeedlException {
        MeedlValidator.validateDataElement(cohortName, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return cohortOutputPort.searchForCohortInAProgram(cohortName,programId);
    }


    @Override
    public List<Cohort> searchForCohort(String userId, String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
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
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
         return loanBreakdownOutputPort.findAllByCohortId(cohortId);
    }

    @Override
    public String inviteCohort(String userId, String cohortId, List<String> loaneeIds) throws MeedlException {
        Cohort foundCohort = viewCohortDetails(userId,cohortId);
        List<Loanee> cohortLoanees = loaneeOutputPort.findSelectedLoaneesInCohort(foundCohort.getId(), loaneeIds);
        if (cohortLoanees == null || cohortLoanees.isEmpty()){
            log.info("Loanee(s) selected is/are not referable.");
            throw new MeedlException("Loanee(s) selected is/are not referable.");
        }
        if (cohortLoanees.size() == 1){
            inviteTrainee(cohortLoanees.get(0));
//            asynchronousMailOutputPort.notifyLoanReferralActors(cohortLoanees);
            loaneeUseCase.notifyLoanReferralActors(cohortLoanees);
            return LOANEE_HAS_BEEN_REFERED;
        }
        referCohort(cohortLoanees);
        log.info("Number of referable loanees :{} ", cohortLoanees.size());
//            asynchronousMailOutputPort.notifyLoanReferralActors(cohortLoanees);
        loaneeUseCase.notifyLoanReferralActors(cohortLoanees);
        return COHORT_INVITED;
    }

    public void referCohort(List<Loanee> cohortLoanees) {
        Iterator<Loanee> iterator = cohortLoanees.iterator();
        while (iterator.hasNext()) {
            Loanee loanee = iterator.next();
            try {
                inviteTrainee(loanee);
            } catch (MeedlException e) {
                log.error("Failed to invite trainee with id: {}", loanee.getId(), e);
                iterator.remove();
            }
        }
    }

    private void inviteTrainee (Loanee loanee) throws MeedlException {
        log.info("Single loanee is being referred...");
        loaneeUseCase.referLoanee(loanee);
    }

}