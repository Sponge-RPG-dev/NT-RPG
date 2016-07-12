package cz.neumimto.dei.entity.database.area;

import cz.neumimto.dei.entity.AreaType;
import cz.neumimto.dei.entity.database.area.StrongholdClaim;
import cz.neumimto.dei.entity.database.structure.Blueprint;
import cz.neumimto.dei.entity.database.structure.Building;
import cz.neumimto.dei.entity.database.utils.UpgradeState;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 8.7.16.
 */
@Entity
@Table(name = "dei_stronghold_area_upgrades")
@PrimaryKeyJoinColumn
public class StrongholdUpgradeArea extends StrongholdClaim {

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "dei_upgrade_structure", joinColumns = {
            @JoinColumn(name = "structure_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "stronghold_upgrade_id",
                    nullable = false, updatable = false) })
    private Set<Blueprint> possibleupgrades = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "building_id")
    private Building building;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpgradeState upgradeState = UpgradeState.EMPTY;

    public StrongholdUpgradeArea() {
        setAreaType(AreaType.STRONGHOLD_UPGRADE);
    }
}
