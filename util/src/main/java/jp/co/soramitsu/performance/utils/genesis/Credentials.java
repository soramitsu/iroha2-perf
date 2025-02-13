package jp.co.soramitsu.performance.utils.genesis;

public enum Credentials {

    WONDERLAND("wonderland"),

    GARDEN_OF_LIVE_FLOWERS("garden_of_live_flowers"),

    ALICE("ed0120FC126A7272403803751DFFC2A40B7E1A6860D6FDE5637E36E783E0032488ACCF"),

    BOB("ed012004FF5B81046DDCCF19E2E451C45DFB6F53759D4EB30FA2EFA807284D1CC33016"),

    CARPENTER("ed0120E9F632D3034BAB6BB26D92AC8FD93EF878D9C5E69E01B61B4C47101884EE2F99"),

    ASSET_DEFINITION_ROSE("rose"),

    ASSET_DEFINITION_CABBAGE("cabbage"),

    CAN_SET_PARAMETERS("CanSetParameters"),

    CAN_REGISTER_DOMAIN("CanRegisterDomain"),

    CAN_MINT_ASSET_WITH_DEFINITION("CanMintAssetWithDefinition"),

    CAN_MANAGE_PEERS("CanManagePeers"),

    CAN_MANAGE_ROLES("CanManageRoles"),

    CAN_UNREGISTER_DOMAIN("CanUnregisterDomain"),

    CAN_UPGRADE_EXECUTOR("CanUpgradeExecutor");

    private final String value;

    Credentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
