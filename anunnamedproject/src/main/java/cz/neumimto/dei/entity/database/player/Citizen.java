package cz.neumimto.dei.entity.database.player;

import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.utils.UUID2String;
import cz.neumimto.dei.entity.database.worldobject.Nation;
import cz.neumimto.dei.entity.database.worldobject.Town;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by NeumimTO on 5.7.2016.
 */
@Entity
@Table(name = "dei_citizen")
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Citizen_id")
    private Long id;

    @Convert(converter = UUID2String.class)
    private UUID uuid;

    @ManyToOne(cascade = CascadeType.ALL)
    private Town town;

    private String lastKnownName;

    private String townPrefix;

    private String townSuffix;

    private String nationPrefix;

    private String nationSuffix;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nation_id")
    private Nation lastNation;

    private long lastNationChangeTime;

    private boolean npc;

    @Transient
    private ClaimedArea currentChunk;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public String getTownPrefix() {
        return townPrefix;
    }

    public void setTownPrefix(String townPrefix) {
        this.townPrefix = townPrefix;
    }

    public String getTownSuffix() {
        return townSuffix;
    }

    public void setTownSuffix(String townSuffix) {
        this.townSuffix = townSuffix;
    }

    public Nation getLastNation() {
        return lastNation;
    }

    public void setLastNation(Nation lastNation) {
        this.lastNation = lastNation;
    }

    public long getLastNationChangeTime() {
        return lastNationChangeTime;
    }

    public void setLastNationChangeTime(long lastNationChangeTime) {
        this.lastNationChangeTime = lastNationChangeTime;
    }

    public boolean isNpc() {
        return npc;
    }

    public void setNpc(boolean npc) {
        this.npc = npc;
    }

    public ClaimedArea getCurrentChunk() {
        return currentChunk;
    }

    public void setCurrentChunk(ClaimedArea currentChunk) {
        this.currentChunk = currentChunk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Citizen citizen = (Citizen) o;

        return uuid.equals(citizen.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }


    public String getNationPrefix() {
        return nationPrefix;
    }

    public void setNationPrefix(String nationPrefix) {
        this.nationPrefix = nationPrefix;
    }

    public String getNationSuffix() {
        return nationSuffix;
    }

    public void setNationSuffix(String nationSuffix) {
        this.nationSuffix = nationSuffix;
    }
}
