package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper;

import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle.CouponDistributionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponDistributionMapper {
    CouponDistributionEntity toCouponDistributionEntity(CouponDistribution couponDistribution);

    CouponDistribution toCouponDistribution(CouponDistributionEntity couponDistributionEntity);
}
