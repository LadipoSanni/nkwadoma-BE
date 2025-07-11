package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlnotification;


import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.identity.UserEntity;
import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name = "meedl_user", nullable = false)
    private UserEntity user;
    private boolean read;
    private LocalDateTime timestamp;
    private boolean callToAction;
    private String senderMail;
    private String senderFullName;
    @Lob
    @Column(name = "content_detail", columnDefinition = "TEXT")
    private String contentDetail;
    @Enumerated(EnumType.STRING)
    private NotificationFlag notificationFlag;

}
