package cz.neumimto.rpg.api.persistance.model;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

public interface TimestampEntity {
    @PrePersist
    void onCreate();

    @PreUpdate
    void onUpdate();

    Date getUpdated();

    void setUpdated(Date updated);

    Date getCreated();

    void setCreated(Date created);
}
