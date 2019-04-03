package com.blockchain.larisa.domain;

public class AccountIdInfo {

    private Integer id;

    private String type;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return "working".equals(status);
    }

    @Override
    public String toString() {
        return "AccountIdInfo{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
