package smartcontractService.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Domain {
    @JsonProperty("id")
    private String id;
}
