package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "programStartDate", expression = "java(java.time.LocalDate.now())")
    ProgramEntity toProgramEntity(Program program);

    @InheritInverseConfiguration
    Program toProgram(ProgramEntity programEntity);
}
