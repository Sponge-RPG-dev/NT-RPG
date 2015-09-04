package cz.neumimto;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by NeumimTo on 24.7.2015.
 */
@MappedSuperclass
public abstract class TimestampEntity {

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updated;

    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }


    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
