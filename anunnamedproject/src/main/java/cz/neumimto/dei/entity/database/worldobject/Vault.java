package cz.neumimto.dei.entity.database.worldobject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Created by ja on 5.7.2016.
 */
@MappedSuperclass
public abstract class Vault {

    private int x;

    private int z;

    private int y;
}
