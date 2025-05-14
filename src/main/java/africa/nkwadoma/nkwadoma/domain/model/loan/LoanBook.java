package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.*;

import java.io.File;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LoanBook {
    private String absoluteFilePath;
    private String name;
    private File file;
}
