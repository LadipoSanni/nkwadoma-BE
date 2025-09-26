package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education;


import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.ProgramLoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProgramLoanDetailMapper {
    ProgramLoanDetailEntity toProgramLoanEntity(ProgramLoanDetail programLoanDetail);

    ProgramLoanDetail toProgramLoanDetail(ProgramLoanDetailEntity programLoanDetailEntity);
}
