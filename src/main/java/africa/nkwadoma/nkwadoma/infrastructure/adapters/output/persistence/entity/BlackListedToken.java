package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class BlackListedToken {

    @Id
    @Column(length = 2000)
    private String access_token;
    private LocalDateTime expirationDate;
}
