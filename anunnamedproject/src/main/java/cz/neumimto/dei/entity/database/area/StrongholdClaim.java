package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.AreaType;
import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.database.worldobject.Stronghold;

import javax.persistence.*;

/**
 * Created by ja on 5.7.2016.
 */
@Entity
@Table(name = "dei_stronghold_claims")
@PrimaryKeyJoinColumn
public class StrongholdClaim extends ClaimedArea<Stronghold> {

    @ManyToOne(cascade=CascadeType.ALL)
    private Stronghold stronghold;

    public StrongholdClaim() {
        setAreaType(AreaType.STRONGHOLD);
    }

    @Override
    public Stronghold getParent() {
        return stronghold;
    }

    @Override
    public void setParent(Stronghold stronghold) {
        this.stronghold = stronghold;
    }
}
