package africa.nkwadoma.nkwadoma.domain.model.education;


import lombok.*;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgramCohort {

    private String id;
    private Cohort cohort;
    private Program program;
}
