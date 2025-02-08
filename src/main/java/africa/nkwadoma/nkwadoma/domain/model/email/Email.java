package africa.nkwadoma.nkwadoma.domain.model.email;

import lombok.*;
import org.thymeleaf.context.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class Email {
    private String from;
    private String template;
    private String subject;
    private String to;
    private Context context;
    private String firstName;
}
