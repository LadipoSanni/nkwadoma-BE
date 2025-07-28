package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramProjection;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProgramMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "programStartDate", expression = "java(java.time.LocalDate.now())")
//    @Mapping(target = "organizationEntity.id", source = "organizationId")
    ProgramEntity toProgramEntity(Program program);

    @InheritInverseConfiguration
    Program toProgram(ProgramEntity programEntity);

    void updateProgram(@MappingTarget Program foundProgram,Program program);


    @Mapping(target = "programStartDate", source = "programStartDate")
    @Mapping(target = "programDescription", source = "programDescription")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "mode", source = "mode")
    @Mapping(target = "numberOfLoanees", source = "numberOfLoanees")
    @Mapping(target = "numberOfCohort", source = "numberOfCohort")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "programStatus", source = "programStatus")
    Program mapFromProgramProjectionToProgram(ProgramProjection programProjection);


    @Mapping(target = "totalAmountRequested", source = "amountRequested")
    @Mapping(target = "totalAmountOutstanding", source = "outstandingAmount")
    @Mapping(target = "totalAmountDisbursed", source = "amountReceived")
    @Mapping(target = "totalAmountRepaid", source = "amountRepaid")
    @Mapping(target = "id", ignore = true)
    void mapProgramLoanDetailsToProgram(@MappingTarget Program program, ProgramLoanDetail programLoanDetail);
}
