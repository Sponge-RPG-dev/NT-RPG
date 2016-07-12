package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.AreaType;
import cz.neumimto.dei.entity.database.worldobject.Town;

import javax.persistence.*;

/**
 * Created by ja on 5.7.2016.
 */

@Entity
@Table(name = "dei_claims")
@PrimaryKeyJoinColumn
public class TownClaim extends ClaimedArea<Town> {

    @ManyToOne(cascade = CascadeType.ALL)
    private Town town;

    public TownClaim() {
        setAreaType(AreaType.TOWN);
    }

    @Override
    public Town getParent() {
        return town;
    }

    @Override
    public void setParent(Town town) {
        this.town = town;
    }
}
