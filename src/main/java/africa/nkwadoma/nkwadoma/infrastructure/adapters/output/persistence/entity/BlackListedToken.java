package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class BlackListedToken {

    @Id
    @Column(length = 2000)
    private String access_token;
}
