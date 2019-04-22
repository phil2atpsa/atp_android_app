package za.co.atpsa.atp.entities;

import java.io.Serializable;

public class EvsReport implements Serializable {
    String name;
    String  type;
    String created_at;
    String origin_ip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getOrigin_ip() {
        return origin_ip;
    }

    public void setOrigin_ip(String origin_ip) {
        this.origin_ip = origin_ip;
    }
}
