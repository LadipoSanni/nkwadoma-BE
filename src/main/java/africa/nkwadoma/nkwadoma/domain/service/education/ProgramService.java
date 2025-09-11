package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final ProgramMapper programMapper;
    private final ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private final LoanBreakdownOutputPort loanBreakdownOutputPort;
    private final CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private final InstituteMetricsOutputPort instituteMetricsOutputPort;

    @Override
    public Program createProgram(Program program) throws MeedlException {
        log.info("Creating program {}", program);
        program.validate();
        OrganizationIdentity organizationIdentity = findProgramOrganization(program);
        program.setOrganizationIdentity(organizationIdentity);
        checkIfProgramExistByNameInOrganization(program);

        program.setProgramStatus(ActivationStatus.ACTIVE);

        program = programOutputPort.saveProgram(program);

        ProgramLoanDetail programLoanDetail = buildProgramLoanDetail(program);
        programLoanDetailOutputPort.save(programLoanDetail);

        updateInstituteMetrics(organizationIdentity);
        return program;
    }

    private void updateInstituteMetrics(OrganizationIdentity organizationIdentity) throws MeedlException {
        InstituteMetrics instituteMetrics = instituteMetricsOutputPort.findByOrganizationId(organizationIdentity.getId());
        instituteMetrics.setNumberOfPrograms(instituteMetrics.getNumberOfPrograms() + BigInteger.ONE.intValue());
        log.info("Updating total number of programs in institute metrics to {}",instituteMetrics.getNumberOfPrograms());
        instituteMetricsOutputPort.save(instituteMetrics);
    }

    private static ProgramLoanDetail buildProgramLoanDetail(Program program) {
        return ProgramLoanDetail.builder()
                .program(program)
                .interestIncurred(BigDecimal.ZERO)
                .amountRequested(BigDecimal.valueOf(0)).outstandingAmount(BigDecimal.valueOf(0))
                .amountReceived(BigDecimal.valueOf(0)).amountRepaid(BigDecimal.valueOf(0)).build();
    }

    @Override
    public Program updateProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        program.validateUpdateProgram(foundProgram);
        log.info("Program duration after validation {}", program.getDuration());
        if (ObjectUtils.isNotEmpty(foundProgram)) {
            if (foundProgram.getNumberOfLoanees() > 0){
                log.warn("A programing with {} loanees can not be edited.", foundProgram.getNumberOfLoanees());
                throw new EducationException(ProgramMessages.PROGRAM_WITH_LOANEE_CANNOT_BE_EDITED.getMessage());
            }
            log.info("Program at service layer update program: ========>{}", foundProgram);
             programMapper.updateProgram(foundProgram, program);
//             OrganizationIdentity organizationIdentity = findProgramOrganization(foundProgram);
//            program.setOrganizationIdentity(organizationIdentity);
//            checkIfProgramExistByNameInOrganization(foundProgram);
            program.setOrganizationId(foundProgram.getOrganizationId());
            program.setId(foundProgram.getId());
            boolean existInOrganization =
                    programOutputPort.programExistsInOrganization(program);
                if (existInOrganization){
                  throw new EducationException(PROGRAM_ALREADY_EXISTS.getMessage());
                }

        }
        return programOutputPort.saveProgram(foundProgram);
    }
    private OrganizationIdentity findProgramOrganization(Program program) throws MeedlException {
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(program.getCreatedBy());
        log.info("The organization identity found when saving program is: {}", organizationIdentity);
        program.setOrganizationId(organizationIdentity.getId());
        return organizationIdentity;
    }
    private void checkIfProgramExistByNameInOrganization(Program program) throws MeedlException {
        boolean programExists = programOutputPort.programExistsInOrganization(program);
        log.info("Program exists {}. name {}, organization {}", programExists, program.getName(), program.getOrganizationId());
        if (programExists) {
            log.error("Program with name {} already exists in organization with id :{}", program.getName(), program.getOrganizationId());
            throw new EducationException(PROGRAM_ALREADY_EXISTS.getMessage());
        }
        log.info("Program with name {} does not exists in organization with id :{}, therefore program can be created/updated.", program.getName(), program.getOrganizationId());
    }
    @Override
    public Page<Program> viewAllPrograms(Program program) throws MeedlException {
        log.info("organization id {}", program.getOrganizationId());
        UserIdentity userIdentity = userIdentityOutputPort.findById(program.getCreatedBy());
        if (IdentityRole.isMeedlStaff(userIdentity.getRole())){
            log.info("organization id {}", program.getOrganizationId());
            MeedlValidator.validateUUID(program.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
            return programOutputPort.findAllProgramByOrganizationId(program.getOrganizationId(),program.getPageSize(),
                    program.getPageNumber());
        }
        return programOutputPort.findAllPrograms(program.getCreatedBy(), program.getPageSize(), program.getPageNumber());
    }

    @Override
    public Page<Program> searchProgramByName(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        program.validateViewProgramByNameInput();
        MeedlValidator.validatePageSize(program.getPageSize());
        MeedlValidator.validatePageNumber(program.getPageNumber());
        return getPrograms(program);
    }

    private Page<Program> getPrograms(Program program) throws MeedlException {
        UserIdentity foundCreator = userIdentityOutputPort.findById(program.getCreatedBy());
        log.info("Found User identity: {}", foundCreator);
        if (ObjectUtils.isNotEmpty(foundCreator) && foundCreator.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)) {
            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(foundCreator.getId());
            log.info("Found Organization Employee: {}", employeeIdentity);
            return programOutputPort.findProgramByNameWithinOrganization(program, employeeIdentity.getOrganization());
        }
        return programOutputPort.findProgramByName(program.getName(),program.getPageNumber(),program.getPageSize());
    }

    @Transactional
    @Override
    public void deleteProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());

        boolean loaneeExistInProgram = programOutputPort.checkIfLoaneeExistInProgram(foundProgram.getId());
                if (loaneeExistInProgram) {
                    throw new EducationException(ProgramMessages.PROGRAM_WITH_LOANEE_CANNOT_BE_DELETED.getMessage());
                }
                else {
                    loanBreakdownOutputPort.deleteAllBreakDownAssociateWithProgram(foundProgram.getId());
                    cohortLoanDetailOutputPort.deleteAllCohortLoanDetailAssociateWithProgram(foundProgram.getId());
                    int numberOfDeletedCohort = cohortOutputPort.deleteAllCohortAssociateWithProgram(foundProgram.getId());
                    programLoanDetailOutputPort.deleteByProgramId(foundProgram.getId());
                    programOutputPort.deleteProgram(foundProgram.getId());
                    decreaseNumberOfProgramInOrganization(foundProgram,numberOfDeletedCohort);
                }
    }

    private void decreaseNumberOfProgramInOrganization(Program foundProgram,int numberOfDeletedCohort) throws MeedlException {
        OrganizationIdentity  organizationIdentity =
                organizationIdentityOutputPort.findById(foundProgram.getOrganizationId());
        organizationIdentity.setNumberOfPrograms(organizationIdentity.getNumberOfPrograms() - 1);
        organizationIdentity.setNumberOfCohort(organizationIdentity.getNumberOfCohort() - numberOfDeletedCohort);
        organizationIdentityOutputPort.save(organizationIdentity);
    }

    @Override
    public Program viewProgramById(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        program = programOutputPort.findProgramById(program.getId());
        ProgramLoanDetail programLoanDetail = programLoanDetailOutputPort.findByProgramId(program.getId());
        programMapper.mapProgramLoanDetailsToProgram(program,programLoanDetail);
        getLoanPercentage(program,programLoanDetail);
        return program;
    }


    private static void getLoanPercentage(Program program, ProgramLoanDetail programLoanDetail) {
        BigDecimal totalAmountReceived = programLoanDetail.getAmountReceived();
        if (totalAmountReceived != null && totalAmountReceived.compareTo(BigDecimal.ZERO) > 0 &&
                programLoanDetail.getOutstandingAmount() != null &&
                programLoanDetail.getAmountRepaid() != null) {
            program.setDebtPercentage(
                    programLoanDetail.getOutstandingAmount()
                            .divide(totalAmountReceived, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
            );
            program.setRepaymentRate(
                    programLoanDetail.getAmountRepaid()
                            .divide(totalAmountReceived, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue()
            );
        } else {
            program.setDebtPercentage(0.0);
            program.setRepaymentRate(0.0);
        }
    }
}
