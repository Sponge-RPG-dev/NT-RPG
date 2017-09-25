package cz.neumimto.dei.entity.database.utils;

import javax.persistence.*;

/**
 * Created by NeumimTo on 5.7.2016.
 */
@Entity
@Table(name = "dei_areapermissions")
public class AreaPermissions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "area_perms_id")
	public Long id;

	public boolean pvp;

	public boolean allyCanBuild;
	public boolean allyCanSwitch;

	public boolean citizenCanBuild;
	public boolean citizenCanInteract;

	public boolean enemyCanInteract;
	public boolean enemyCanBuild;

	public boolean mobsCanSpawn;


}
