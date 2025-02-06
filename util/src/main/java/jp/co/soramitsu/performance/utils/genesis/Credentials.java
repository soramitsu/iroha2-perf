package jp.co.soramitsu.performance.utils.genesis;

public enum Credentials {

    WONDERLAND("wonderland"),

    GARDEN_OF_LIVE_FLOWERS("garden_of_live_flowers"),

    ALICE("ed0120CE7FA46C9DCE7EA4B125E2E36BDB63EA33073E7590AC92816AE1E861B7048B03"),

    BOB("ed012004FF5B81046DDCCF19E2E451C45DFB6F53759D4EB30FA2EFA807284D1CC33016"),

    CARPENTER("ed0120E9F632D3034BAB6BB26D92AC8FD93EF878D9C5E69E01B61B4C47101884EE2F99"),

    ASSET_DEFINITION_ROSE("rose"),

    ASSET_DEFINITION_CABBAGE("cabbage"),

    CAN_SET_PARAMETERS("CanSetParameters"),

    CAN_REGISTER_DOMAIN("CanRegisterDomain");

    private final String value;

    Credentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
