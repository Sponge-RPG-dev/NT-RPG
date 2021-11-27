package cz.neumimto.rpg.model;

import cz.neumimto.rpg.common.model.TimestampEntity;

import java.util.Date;

/**
 * Created by NeumimTo on 24.7.2015.
 */
public abstract class TimestampEntityTest implements TimestampEntity {

    private Date updated;
    private Date created;

    public void onCreate() {
        updated = created = new Date();
    }

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
