package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.LoanBookResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.CohortRestMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {CohortRestMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanBookRestMapper {
    @Mapping(target = "cohort", source = "request")
    @Mapping(target = "file", expression = "java(new java.io.File(absoluteFilePath))")
    @Mapping(target = "absoluteFilePath", source = "absoluteFilePath")
    @Mapping(target = "createdBy", source = "createdBy")
    LoanBook map(CreateCohortRequest request, String absoluteFilePath, String createdBy);

    LoanBookResponse map(LoanBook loanBook);
}
