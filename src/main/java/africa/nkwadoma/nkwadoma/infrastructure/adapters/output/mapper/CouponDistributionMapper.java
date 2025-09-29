package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CouponDistributionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponDistributionMapper {
    CouponDistributionEntity toCouponDistributionEntity(CouponDistribution couponDistribution);

    CouponDistribution toCouponDistribution(CouponDistributionEntity couponDistributionEntity);
}
