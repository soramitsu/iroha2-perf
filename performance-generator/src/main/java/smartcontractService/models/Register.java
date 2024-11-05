package smartcontractService.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Register {
    @JsonProperty("NewDomain")
    private Domain domain;
}
