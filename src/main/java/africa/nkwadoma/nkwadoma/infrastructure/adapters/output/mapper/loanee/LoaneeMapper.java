package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanee;


import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeProjection;
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

    @Mapping(source = "firstName", target = "userIdentity.firstName")
    @Mapping(source = "lastName", target = "userIdentity.lastName")
    @Mapping(source = "instituteName", target = "referredBy")
    @Mapping(source = "id", target = "id")
    Loanee mapProjecttionToLoanee(LoaneeProjection loaneeProjection);
}
