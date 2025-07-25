package africa.nkwadoma.nkwadoma.domain.model.investmentvehicle;

import africa.nkwadoma.nkwadoma.domain.enums.BankPartner;
import africa.nkwadoma.nkwadoma.domain.enums.Custodian;
import africa.nkwadoma.nkwadoma.domain.enums.FundManager;
import africa.nkwadoma.nkwadoma.domain.enums.Trustee;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FundStakeHolder{
    private List<BankPartner> bankPartners;
    private List<FundManager> fundManagers;
    private List<Trustee> trustees;
    private List<Custodian> custodians;

}