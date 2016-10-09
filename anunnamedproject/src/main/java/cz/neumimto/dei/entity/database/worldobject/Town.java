package cz.neumimto.dei.entity.database.worldobject;

import cz.neumimto.dei.entity.IHasClaims;
import cz.neumimto.dei.entity.database.area.TownClaim;
import cz.neumimto.dei.entity.database.player.Citizen;
import cz.neumimto.dei.entity.database.area.ClaimedArea;
import cz.neumimto.dei.entity.database.utils.AreaPermissions;
import cz.neumimto.dei.entity.database.utils.BlockType2String;
import cz.neumimto.dei.entity.database.utils.TownType;
import cz.neumimto.dei.serivce.ClaimedAreaType;
import org.spongepowered.api.block.BlockType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 5.7.2016.
 */
@Entity
@Table(name = "dei_towns")
public class Town implements IHasClaims<TownClaim> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Town_id")
    private Long id;

    @Column(unique = true)
    private String name;

    private String welcomeMessage;

    private String boardCrossMessage;

    private String title;

    @Enumerated(EnumType.STRING)
    private TownType townType;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="citizenId")
    private Citizen leader;

    @ElementCollection
    @CollectionTable( name = "user_team",
            joinColumns = @JoinColumn( name = "nation_id" ) )
    @Column( name = "team_name" )
    @Enumerated(EnumType.STRING)
    @Convert( converter = BlockType2String.class )
    private Set<BlockType> blockPalette;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy = "town")
    private List<Citizen> citizens;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "town")
    private Set<TownClaim> claimedAreas = new HashSet<>();

    @OneToOne
    @JoinColumn(name="home_claim_id",nullable = false)
    private TownClaim homeChunk;

    @ManyToOne
    @JoinColumn(name = "nation_id")
    private Nation nation;

    private long balance;

    private long lastTimeAttacked;

    private long lastTimeConquered;

    private boolean isDestroyed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_perms_id")
    private AreaPermissions areaPermissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getBoardCrossMessage() {
        return boardCrossMessage;
    }

    public void setBoardCrossMessage(String boardCrossMessage) {
        this.boardCrossMessage = boardCrossMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Citizen> getCitizens() {
        return citizens;
    }

    public void setCitizens(List<Citizen> citizens) {
        this.citizens = citizens;
    }

    public Set<TownClaim> getClaimedAreas() {
        return claimedAreas;
    }

    public void setClaimedAreas(Set<TownClaim> claimedAreas) {
        this.claimedAreas = claimedAreas;
    }

    @Override
    public ClaimedAreaType getType() {
        return ClaimedAreaType.TOWN;
    }

    public ClaimedArea getHomeChunk() {
        return homeChunk;
    }

    public void setHomeChunk(TownClaim homeChunk) {
        this.homeChunk = homeChunk;
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getLastTimeAttacked() {
        return lastTimeAttacked;
    }

    public void setLastTimeAttacked(long lastTimeAttacked) {
        this.lastTimeAttacked = lastTimeAttacked;
    }

    public long getLastTimeConquered() {
        return lastTimeConquered;
    }

    public void setLastTimeConquered(long lastTimeConquered) {
        this.lastTimeConquered = lastTimeConquered;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }


    public Set<BlockType> getBlockPalette() {
        return blockPalette;
    }

    public void setBlockPalette(Set<BlockType> blockPalette) {
        this.blockPalette = blockPalette;
    }

    public AreaPermissions getAreaPermissions() {
        return areaPermissions;
    }

    public void setAreaPermissions(AreaPermissions areaPermissions) {
        this.areaPermissions = areaPermissions;
    }

    public Citizen getLeader() {
        return leader;
    }

    public void setLeader(Citizen leader) {
        this.leader = leader;
    }
}
