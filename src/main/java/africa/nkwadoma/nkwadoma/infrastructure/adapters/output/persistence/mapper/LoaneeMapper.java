package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeMapper {
    LoaneeEntity toLoaneeEntity(Loanee loanee);

    Loanee toLoanee(LoaneeEntity loaneeEntity);
}
