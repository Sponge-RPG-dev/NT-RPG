/*
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.neumimto.rpg.players.groups;

import cz.neumimto.rpg.players.CharacterBase;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Created by NeumimTo on 27.12.2014.
 *//*
@Table(name = "Guilds",
        indexes = {@Index(columnList = "id")})
@Entity
//TODO put guilds in a second level cache; configure ehcache in nt core
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)*/
public class Guild {

	public static Guild Default = new Guild();

	static {
		Default.name = "None";
	}

	@Id
	@GeneratedValue(generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long guildId;

	private String name;

	private Long leaderId;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "characterId")
	private List<CharacterBase> memebers;


	public Guild() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}

	public List<CharacterBase> getMemebers() {
		return memebers;
	}

	public void setMemebers(List<CharacterBase> memebers) {
		this.memebers = memebers;
	}
}
