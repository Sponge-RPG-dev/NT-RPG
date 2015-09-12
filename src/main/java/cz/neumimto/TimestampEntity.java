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

package cz.neumimto;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by NeumimTo on 24.7.2015.
 */
@MappedSuperclass
public abstract class TimestampEntity {

    @Column(name = "updated")
    @Temporal(TemporalType.TIMESTAMP)
    public Date updated;

    @PrePersist
    protected void onCreate() {
        updated = created = new Date();
    }


    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
}
