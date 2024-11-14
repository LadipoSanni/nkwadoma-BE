package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProgramMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "programStartDate", expression = "java(java.time.LocalDate.now())")
//    @Mapping(target = "organizationEntity.id", source = "organizationId")
    ProgramEntity toProgramEntity(Program program);

    @InheritInverseConfiguration
    Program toProgram(ProgramEntity programEntity);

    Program updateProgram(Program program, @MappingTarget Program foundProgram);

}
