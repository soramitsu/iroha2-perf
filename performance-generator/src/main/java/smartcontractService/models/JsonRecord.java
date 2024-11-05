package smartcontractService.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonRecord {
    @JsonProperty("Register")
    private Register register;
}
