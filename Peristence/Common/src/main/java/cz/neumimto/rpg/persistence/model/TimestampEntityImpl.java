

package cz.neumimto.rpg.persistence.model;

import cz.neumimto.rpg.api.persistance.model.TimestampEntity;

import java.util.Date;

/**
 * Created by NeumimTo on 24.7.2015.
 */
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
