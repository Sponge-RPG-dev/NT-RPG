package cz.neumimto.rpg.common.model;

import java.util.Date;

public interface TimestampEntity {

    void onCreate();

    void onUpdate();

    Date getUpdated();

    void setUpdated(Date updated);

    Date getCreated();

    void setCreated(Date created);
}
