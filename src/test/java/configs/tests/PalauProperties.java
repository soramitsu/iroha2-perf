package configs.tests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = PalauProperties.PROPERTY_PREFIX)
public class PalauProperties {

    public static final String PROPERTY_PREFIX = "palau";

    private final TriggerProperties trigger = new TriggerProperties();

    @Getter
    @Setter
    public static class TriggerProperties {
        public static final String PROPERTY_PREFIX = PalauProperties.PROPERTY_PREFIX + ".trigger";

        private String buildInfoPath = "src/test/resources/iroha2_config/stable/5d44d59/smartContracts/build_info.json";

        private String registerBondId = "register_bond";
        private String registerBondWasm = "src/test/resources/iroha2_config/stable/5d44d59/smartContracts/register_bond_optimized.wasm";
        private String registerBondTriggerKey = "bond";

        private String buyBondId = "buy_bonds_trigger";
        private String buyBondWasm = "src/test/resources/iroha2_config/stable/5d44d59/smartContracts/buy_bonds_optimized.wasm";

        private String redeemBondId = "redeem_bonds_trigger";
        private String redeemBondWasm = "src/test/resources/iroha2_config/stable/5d44d59/smartContracts/redeem_bonds_optimized.wasm";
    }
}

