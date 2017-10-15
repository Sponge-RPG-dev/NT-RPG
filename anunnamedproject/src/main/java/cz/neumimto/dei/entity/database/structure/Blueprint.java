package cz.neumimto.dei.entity.database.structure;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ja on 8.7.16.
 */
@Entity
@Table(name = "dei_blueprints")
public class Blueprint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String schematicPath;

	private int maxHealth;

	private int blockRepairRate;

	private int level;

	@Access(AccessType.PROPERTY)

	private Set<ItemStackResource> buildResources = new HashSet<>();

	private Set<Reward> rewardSet = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSchematicPath(String schematicPath) {
		this.schematicPath = schematicPath;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setBlockRepairRate(int blockRepairRate) {
		this.blockRepairRate = blockRepairRate;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setBuildResources(Set<ItemStackResource> buildResources) {
		this.buildResources = buildResources;
	}

	public void setRewardSet(Set<Reward> rewardSet) {
		this.rewardSet = rewardSet;
	}
}
