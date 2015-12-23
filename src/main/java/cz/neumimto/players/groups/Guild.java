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

package cz.neumimto.players.groups;

import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.players.properties.DefaultProperties;
import cz.neumimto.skills.ISkill;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by NeumimTo on 27.12.2014.
 */
@Table(name = "Guilds",
        indexes = {@Index(columnList = "id")})
@Entity
//TODO put guilds in a second level cache; configure ehcache in nt core
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Guild {
    public static Guild Default = new Guild() {{
        Default.name = "None";
    }};

    public Guild() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long leaderId;


    private Set<IActiveCharacter> memebers = new HashSet<>();

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

    public Set<IActiveCharacter> getMemebers() {
        return memebers;
    }

    public void setMemebers(Set<IActiveCharacter> memebers) {
        this.memebers = memebers;
    }
}
