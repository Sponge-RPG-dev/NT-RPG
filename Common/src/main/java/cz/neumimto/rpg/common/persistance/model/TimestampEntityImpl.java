

package cz.neumimto.rpg.common.persistance.model;

import cz.neumimto.rpg.common.model.TimestampEntity;

import java.util.Date;

public abstract class TimestampEntityImpl implements TimestampEntity {

    private Date created;
    private Date updated;

    @Override
    public void onCreate() {
        updated = created = new Date();
    }

    @Override
    public void onUpdate() {
        updated = new Date();
    }

    @Override
    public Date getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }
}
