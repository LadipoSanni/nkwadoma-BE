package africa.nkwadoma.nkwadoma.infrastructure.enums;

import lombok.Getter;

@Getter
public enum CreditRegistryConstant {
    EMAIL("EmailAddress"),
    SUBSCRIBER_ID("SubscriberID"),
    PASSWORD("Password"),
    SESSION_CODE("SessionCode"),
    CUSTOMER_QUERY("CustomerQuery"),
    GET_NO_MATCH_REPORT("GetNoMatchReport"),
    MIN_RELEVANCE("MinRelevance"),
    MAX_RECORDS("MaxRecords"),
    ENQUIRY_REASON("EnquiryReason");

    private final String value;
    CreditRegistryConstant(String value) {
        this.value = value;
    }
}
