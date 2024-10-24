package configs.tests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PalauProperties {

    public static final String PROPERTY_PREFIX = "palau";

    private final TriggerProperties trigger = new TriggerProperties();

    @Getter
    @Setter
    public static class TriggerProperties {
        public static final String PROPERTY_PREFIX = PalauProperties.PROPERTY_PREFIX + ".trigger";

        private String buildInfoPath = "src/test/resources/iroha2_config/stable/5d44d59/smartContracts/build_info.json";

        private String pathToConfig = "iroha2_config/stable/5d44d59/smartContracts/";

        private String registerBondId = "register_bond";
        private String registerBondWasm = pathToConfig + "register_bond_optimized.wasm";
        private String registerBondTriggerKey = "bond";

        private String buyBondId = "buy_bonds_trigger";
        private String buyBondWasm = pathToConfig + "buy_bonds_optimized.wasm";

        private String redeemBondId = "redeem_bonds_trigger";
        private String redeemBondWasm = pathToConfig + "redeem_bonds_optimized.wasm";
    }
}

