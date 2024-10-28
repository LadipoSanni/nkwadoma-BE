package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper;

import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.ProgramResponse;
import org.mapstruct.*;
import org.springframework.cglib.core.*;

@Mapper(componentModel = "spring", uses = DurationTypeMapper.class)
public interface ProgramRestMapper {
    @Mapping(source = "programCreateRequest.instituteId", target = "organizationId")
    @Mapping(source = "programCreateRequest.programName", target = "name")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "programCreateRequest.programDuration", target = "duration")
    @Mapping(source = "programCreateRequest.programMode", target = "mode")
    @Mapping(source = "programCreateRequest.durationStatus", target = "durationType")
    @Mapping(source = "meedlUserId", target = "createdBy")
    Program toProgram(ProgramCreateRequest programCreateRequest, String meedlUserId);

    @Mapping(target = "pageNumber", source = "pageNumber", defaultValue = "0")
    @Mapping(target = "pageSize", source = "pageSize", defaultValue = "0")
    Program toProgram(ProgramsRequest programsRequest);

    @Mapping(target = "totalAmountRepaid", source = "totalAmountRepaid", defaultValue = "0")
    @Mapping(target = "totalAmountDisbursed", source = "totalAmountDisbursed", defaultValue = "0")
    @Mapping(target = "totalAmountOutstanding", source = "totalAmountOutstanding", defaultValue = "0")
    ProgramResponse toProgramResponse(Program program);
}
