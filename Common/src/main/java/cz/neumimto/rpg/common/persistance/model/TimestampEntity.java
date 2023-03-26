package cz.neumimto.rpg.common.persistance.model;

import java.util.Date;

public abstract class TimestampEntity {

    private Date created;
    private Date updated;

    public void onCreate() {
        updated = created = new Date();
    }

    public void onUpdate() {
        updated = new Date();
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
