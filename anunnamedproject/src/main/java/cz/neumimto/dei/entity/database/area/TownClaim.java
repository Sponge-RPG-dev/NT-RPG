package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.database.worldobject.Town;

import javax.persistence.*;

/**
 * Created by ja on 5.7.2016.
 */

@Entity
@Table(name = "dei_claims")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TownClaim extends ClaimedArea<Town> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tclaim_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Town town;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
