package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mappers;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.ProgramCreateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ProgramResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProgramControllerMapper {
    @Mapping(source = "instituteId", target = "organizationId")
    @Mapping(source = "programName", target = "name")
    @Mapping(source = "creatorId", target = "createdBy")
    @Mapping(source = "programDuration", target = "duration")
    @Mapping(source = "programMode", target = "mode")
    @ValueMappings({
            @ValueMapping(target = "MONTHS", source = "Months"),
            @ValueMapping(target = "WEEKS", source = "Weeks"),
            @ValueMapping(target = "YEARS", source = "Years")
    })
    Program toProgram(ProgramCreateRequest programCreateRequest);
    @InheritInverseConfiguration
    ProgramResponse toProgramResponse(Program program);
}
