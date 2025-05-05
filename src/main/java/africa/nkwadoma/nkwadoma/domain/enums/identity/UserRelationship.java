package africa.nkwadoma.nkwadoma.domain.enums.identity;

public enum UserRelationship {
    // Self-reference
    SELF,

    // Immediate Family
    FATHER,
    MOTHER,
    SON,
    DAUGHTER,
    BROTHER,
    SISTER,
    HUSBAND,
    WIFE,

    // Extended Family
    GRANDFATHER,
    GRANDMOTHER,
    GRANDSON,
    GRANDDAUGHTER,
    UNCLE,
    AUNT,
    NEPHEW,
    NIECE,
    COUSIN,

    // In-laws
    FATHER_IN_LAW,
    MOTHER_IN_LAW,
    SON_IN_LAW,
    DAUGHTER_IN_LAW,
    BROTHER_IN_LAW,
    SISTER_IN_LAW,

    // Step Relationships
    STEPFATHER,
    STEPMOTHER,
    STEPSON,
    STEPDAUGHTER,
    STEPBROTHER,
    STEPSISTER,

    // Other relationships
    GODFATHER,
    GODMOTHER,
    GODSON,
    GODDAUGHTER,
    FRIEND,
    COLLEAGUE,
    PARTNER,
    FIANCÉ,
    FIANCÉE
}
