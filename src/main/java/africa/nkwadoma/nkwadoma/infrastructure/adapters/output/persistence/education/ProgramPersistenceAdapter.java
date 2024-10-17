package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.stream.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final ServiceOfferEntityRepository serviceOfferEntityRepository;

    @Override
    public Program findProgramByName(String programName) throws MeedlException {
        validateDataElement(programName);
        ProgramEntity programEntity = programRepository.findByName(programName).
                orElseThrow(()-> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));

        return programMapper.toProgram(programEntity);
    }

    @Override
    public Program saveProgram(Program program) throws MeedlException {
        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId());
        ProgramEntity programEntity = programMapper.toProgramEntity(program);

        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);
        if (organizationIdentity.getServiceOffering() != null &&
                organizationIdentity.getServiceOffering().getIndustry() != Industry.EDUCATION
        ) {
            throw new EducationException(ProgramMessages.WRONG_INDUSTRY.getMessage());
        }

        serviceOfferEntityRepository.save(organizationEntity.getServiceOfferingEntity());
        organizationEntity.setNumberOfPrograms(organizationEntity.getNumberOfPrograms() + 1);
        organizationEntity = organizationEntityRepository.save(organizationEntity);

        programEntity.setOrganizationEntity(organizationEntity);
        programEntity = programRepository.save(programEntity);

        return programMapper.toProgram(programEntity);
    }

    @Override
    public boolean programExists(String programName) throws MeedlException {
        validateDataElement(programName);
        return programRepository.existsByName(programName);
    }

    @Override
    public void deleteProgram(String programId) throws MeedlException {
        validateDataElement(programId);
        ProgramEntity program = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        programRepository.delete(program);
    }

    @Override
    public Program findProgramById(String programId) throws MeedlException {
        validateDataElement(programId);
        ProgramEntity programEntity = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        return programMapper.toProgram(programEntity);
    }

    @Override
    public Page<Program> findAllPrograms(String organizationId, int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProgramEntity> programEntities = programRepository.findAllByOrganizationEntityId(organizationId, pageRequest);
        return programEntities.map(programMapper::toProgram);
    }
}
