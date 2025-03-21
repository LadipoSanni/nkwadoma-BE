package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentVehicle;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Getter
@Setter
@ToString
public class CooperationEntity {
    @Id
    @UuidGenerator
    private String id;
    private String name;
    @OneToOne
    private UserEntity userEntity;
}
