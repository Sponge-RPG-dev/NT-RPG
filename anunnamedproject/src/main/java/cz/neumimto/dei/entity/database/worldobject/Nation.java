package cz.neumimto.dei.entity.database.worldobject;

import cz.neumimto.dei.entity.database.player.Citizen;
import cz.neumimto.dei.entity.database.utils.ItemType2String;
import org.spongepowered.api.item.ItemType;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by NeumimTo on 5.7.2016.
 */
@Entity
@Table(name = "nations")
public class Nation  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nation_id")
    private Long id;

    private String name;

    private long balance;

    private String description;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="leader_id")
    private Citizen leader;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="nation_blockpallete",
            joinColumns=@JoinColumn(name="nation_id")
    )
    @OrderColumn(name = "index_id")
    @Convert(converter = ItemType2String.class)
    private Set<ItemType> blockPalette;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="capital_town_id")
    private Town capital;

    @OneToMany(mappedBy = "nation", cascade = CascadeType.ALL)
    private List<Town> towns;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="nation_permissions",
            joinColumns=@JoinColumn(name="nation_id")
    )
    @OrderColumn(name = "index_id")
    private List<String> permissions;

    @OneToMany(fetch = FetchType.LAZY,mappedBy="nation")
    private List<Stronghold> conqueredstrongholds;

    public long started;

    public boolean isActive;
}
