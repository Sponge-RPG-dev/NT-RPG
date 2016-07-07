package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.database.worldobject.Stronghold;

import javax.persistence.*;

/**
 * Created by ja on 5.7.2016.
 */
@Entity
@Table(name = "dei_stronghold_claims")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StrongholdClaim extends ClaimedArea<Stronghold> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sclaim_id")
    private Long id;

    @ManyToOne(cascade=CascadeType.ALL)
    private Stronghold stronghold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
