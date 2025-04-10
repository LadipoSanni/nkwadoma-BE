package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;


import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoaneeMapper {
    @Mapping(target = "userIdentity", source = "userIdentity")
    LoaneeEntity toLoaneeEntity(Loanee loanee);

    @InheritInverseConfiguration
    Loanee toLoanee(LoaneeEntity loaneeEntity);

    List<Loanee> toListOfLoanee(List<LoaneeEntity> loaneeEntities);

    Loanee updateLoanee(Loanee loanee, @MappingTarget Loanee foundLoanee);
}
