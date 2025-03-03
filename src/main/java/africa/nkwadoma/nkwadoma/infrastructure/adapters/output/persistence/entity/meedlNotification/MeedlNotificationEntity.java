package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlNotification;


import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class MeedlNotificationEntity {

    @Id
    @UuidGenerator
    public String id;
    private String title;
    private String contentId;
    private String name;
    @ManyToOne
    @JoinColumn(name = "meedl_user", nullable = false)
    private UserEntity user;
    private boolean isRead;
    private LocalDateTime timestamp;
}
