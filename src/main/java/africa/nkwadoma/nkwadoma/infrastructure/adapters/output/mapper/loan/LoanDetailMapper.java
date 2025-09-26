package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.LoanDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoanDetailMapper {
    LoanDetailEntity toLoanDetailEntity(LoanDetail loanDetail);

    LoanDetail toLoanDetail(LoanDetailEntity loanDetailEntity);
}
