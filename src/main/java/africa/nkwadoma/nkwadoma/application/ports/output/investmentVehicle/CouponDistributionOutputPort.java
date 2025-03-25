package africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.CouponDistribution;

public interface CouponDistributionOutputPort {
    CouponDistribution save(CouponDistribution couponDistribution) throws MeedlException;

    void deleteById(String couponId) throws MeedlException;

}