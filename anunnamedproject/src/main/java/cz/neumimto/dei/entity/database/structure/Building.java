package cz.neumimto.dei.entity.database.structure;

import javax.persistence.*;

/**
 * Created by ja on 8.7.16.
 */
@Entity
@Table(name = "dei_buildings")
public class Building {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "blueprint_id")
	private Blueprint blueprint;

	private boolean life;

	private boolean isFinished;

	private long timeLeftToBuild;

	private boolean maxDamageTreshhold;

	private String upKeepCron;

	private long lastTimeCronRun;


}
