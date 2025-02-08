package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CustomerDetail {
    @JsonProperty("Relevance")
    private int relevance;
    @JsonProperty("BVN")
    private String bvn;
    @JsonProperty("RegistryID")
    private String registryID;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("DOBI")
    private String dobI;
    @JsonProperty("PhoneNumbers")
    private List<String> phoneNumbers;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("EmailAddresses")
    private List<String> emailAddresses;
    @JsonProperty("IDs")
    private List<String> ids;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("CustomerType")
    private String customerType;
    @JsonProperty("Picture")
    private String picture;

}
