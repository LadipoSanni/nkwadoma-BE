package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.education.CreateCohortRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanBook.LoanBookResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.education.CohortRestMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.io.File;

@Mapper(componentModel = "spring", uses = {CohortRestMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanBookRestMapper {
    @Mapping(target = "file", source = "file")
    @Mapping(target = "cohort.id", source = "cohortId")
    @Mapping(target = "cohort.createdBy", source = "createdBy")
    @Mapping(target = "createdBy", source = "createdBy")
    LoanBook map(String cohortId, File file, String createdBy);

    LoanBookResponse map(LoanBook loanBook);
}
