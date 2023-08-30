package jp.co.soramitsu.load.objects;

public class Transaction {
    private Boolean send = false;
    private Boolean validate = false;
    public Boolean commit = false;
    private Boolean reject = false;
    private Boolean create = false;
    private String tx;

    public Boolean getSend() {
        return send;
    }

    public Boolean getCommit() {
        return commit;
    }
    public Boolean getValidate() {
        return validate;
    }
    public void setSend(Boolean send) {
        this.send = send;
    }

    public void setValidate(Boolean validate) {
        this.validate = validate;
    }

    public void setCommit(Boolean commit) {
        this.commit = commit;
    }

    public Boolean getReject() {
        return reject;
    }

    public void setReject(Boolean reject) {
        this.reject = reject;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }
}
