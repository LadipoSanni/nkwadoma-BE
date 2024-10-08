package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.ProgramCreateRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.ProgramResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = DurationTypeMapper.class)
public interface ProgramRestMapper {
    @Mapping(source = "programCreateRequest.instituteId", target = "organizationId")
    @Mapping(source = "programCreateRequest.programName", target = "name")
    @Mapping(source = "programCreateRequest.createdAt", target = "createdAt")
    @Mapping(source = "programCreateRequest.programDuration", target = "duration")
    @Mapping(source = "programCreateRequest.programMode", target = "mode")
    @Mapping(source = "programCreateRequest.durationStatus", target = "durationType")
    @Mapping(source = "meedlUserId", target = "createdBy")
    Program toProgram(ProgramCreateRequest programCreateRequest, String meedlUserId);

    @InheritInverseConfiguration
    ProgramResponse toProgramResponse(Program program);
}
