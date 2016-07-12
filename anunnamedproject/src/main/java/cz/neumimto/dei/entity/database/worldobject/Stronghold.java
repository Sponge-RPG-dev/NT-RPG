package cz.neumimto.dei.entity.database.worldobject;

import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.database.structure.ConquestPoint;
import cz.neumimto.dei.entity.database.area.StrongholdClaim;
import cz.neumimto.dei.entity.database.utils.UpKeep2String;
import cz.neumimto.dei.serivce.ClaimedAreaType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by NeumimTo on 5.7.2016.
 */
@Entity
@Table(name = "dei_strongholds")
public class Stronghold implements IHasClaims<StrongholdClaim> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stronghold_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Nation nation;

    private long lastTimeAttacked;

    private long lastTimeConquered;

    private int minimumSizeRaidParty = -1;

    private int maximumSizeRaidParty = -1;

    private int minimumPlayersInNationRequiredForAttacking = -1;

    private int maximumSizeDefenders = -1;

    @Convert(converter = UpKeep2String.class)
    private String upkeep;

    private String upkeepCron;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="stronghold",fetch = FetchType.EAGER)
    private Set<StrongholdClaim> claimedAreas;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="stronghold",fetch = FetchType.EAGER)
    private List<ConquestPoint> conquestPoints;

    @Transient
    private Set<UUID> defenders = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Nation getClaimedBy() {
        return nation;
    }

    public void setClaimedBy(Nation claimedBy) {
        this.nation = claimedBy;
    }

    public long getLastTimeAttacked() {
        return lastTimeAttacked;
    }

    public void setLastTimeAttacked(long lastTimeAttacked) {
        this.lastTimeAttacked = lastTimeAttacked;
    }

    public String getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(String upkeep) {
        this.upkeep = upkeep;
    }

    @Override
    public Set<StrongholdClaim> getClaimedAreas() {
        return claimedAreas;
    }

    @Override
    public void setClaimedAreas(Set<StrongholdClaim> claimedAreas) {
        this.claimedAreas = claimedAreas;
    }

    @Override
    public ClaimedAreaType getType() {
        return ClaimedAreaType.STRONGHOLD;
    }

    public List<ConquestPoint> getConquestPoints() {
        return conquestPoints;
    }

    public void setConquestPoints(List<ConquestPoint> conquestPoints) {
        this.conquestPoints = conquestPoints;
    }

    public String getUpkeepCron() {
        return upkeepCron;
    }

    public void setUpkeepCron(String upkeepCron) {
        this.upkeepCron = upkeepCron;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public long getLastTimeConquered() {
        return lastTimeConquered;
    }

    public void setLastTimeConquered(long lastTimeConquered) {
        this.lastTimeConquered = lastTimeConquered;
    }

    public int getMinimumSizeRaidParty() {
        return minimumSizeRaidParty;
    }

    public void setMinimumSizeRaidParty(int minimumSizeRaidParty) {
        this.minimumSizeRaidParty = minimumSizeRaidParty;
    }

    public int getMaximumSizeRaidParty() {
        return maximumSizeRaidParty;
    }

    public void setMaximumSizeRaidParty(int maximumSizeRaidParty) {
        this.maximumSizeRaidParty = maximumSizeRaidParty;
    }

    public int getMinimumPlayersInNationRequiredForAttacking() {
        return minimumPlayersInNationRequiredForAttacking;
    }

    public void setMinimumPlayersInNationRequiredForAttacking(int minimumPlayersInNationRequiredForAttacking) {
        this.minimumPlayersInNationRequiredForAttacking = minimumPlayersInNationRequiredForAttacking;
    }

    public int getMaximumSizeDefenders() {
        return maximumSizeDefenders;
    }

    public void setMaximumSizeDefenders(int maximumSizeDefenders) {
        this.maximumSizeDefenders = maximumSizeDefenders;
    }

    public Set<UUID> getDefenders() {
        return defenders;
    }

    public void setDefenders(Set<UUID> defenders) {
        this.defenders = defenders;
    }
}
