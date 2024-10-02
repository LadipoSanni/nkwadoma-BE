package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

    ProgramEntity toProgramEntity(Program program);

    @InheritInverseConfiguration
    Program toProgram(ProgramEntity programEntity);
}
