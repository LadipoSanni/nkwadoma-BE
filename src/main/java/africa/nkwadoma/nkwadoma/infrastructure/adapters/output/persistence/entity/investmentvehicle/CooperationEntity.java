package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.investmentvehicle;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String email;
//    @OneToOne
//    private UserEntity userIdentity;
}
