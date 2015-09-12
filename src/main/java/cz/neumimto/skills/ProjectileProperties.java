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

package cz.neumimto.skills;

import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Created by NeumimTo on 15.1.2015.
 */
public abstract class ProjectileProperties {
    public static Map<UUID, ProjectileProperties> cache = new HashMap<>();
    protected Projectile t;
    private double damage;
    private Living damager;
    public BiConsumer<Living, IActiveCharacter> consumer;

    public ProjectileProperties(Projectile t, Living damager) {
        this.t = t;
        this.damager = damager;
        cache.put(t.getUniqueId(), this);
    }

    public void onHit(BiConsumer<Living, IActiveCharacter> consumer) {
        this.consumer = consumer;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Projectile getProjectile() {
        return t;
    }

}
