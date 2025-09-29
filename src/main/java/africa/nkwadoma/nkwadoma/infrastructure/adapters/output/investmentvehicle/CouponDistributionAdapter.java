package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CouponDistributionOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.CouponDistribution;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle.CouponDistributionEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.CouponDistributionMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.investmentvehicle.CouponDistributionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class CouponDistributionAdapter implements CouponDistributionOutputPort {

    private final CouponDistributionRepository couponDistributionRepository;
    private final CouponDistributionMapper couponDistributionMapper;

    @Override
    public CouponDistribution save(CouponDistribution couponDistribution) throws MeedlException {
        MeedlValidator.validateObjectInstance(couponDistribution,"Coupon distribution cannot be empty");
        CouponDistributionEntity couponDistributionEntity =
                couponDistributionMapper.toCouponDistributionEntity(couponDistribution);
        couponDistributionEntity = couponDistributionRepository.save(couponDistributionEntity);
        return couponDistributionMapper.toCouponDistribution(couponDistributionEntity);
    }

    @Override
    public void deleteById(String couponId) throws MeedlException {
        MeedlValidator.validateUUID(couponId,"Coupon distribution id cannot be empty");
        couponDistributionRepository.deleteById(couponId);
    }
}
