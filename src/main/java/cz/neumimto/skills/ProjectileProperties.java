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

import cz.neumimto.IEntity;
import cz.neumimto.entities.IMob;
import cz.neumimto.players.IActiveCharacter;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Projectile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by NeumimTo on 15.1.2015.
 */
public class ProjectileProperties {
    public static Map<UUID, ProjectileProperties> cache = new LinkedHashMap<UUID, ProjectileProperties>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, ProjectileProperties> entry) {
            return entry.getValue().lifetime > System.currentTimeMillis();
        }
    };
    protected Projectile t;
    private double damage;
    private long lifetime;
    private IEntity caster;
    public BiConsumer<IEntity,IEntity> consumer;

    public ProjectileProperties(Projectile t, IEntity caster) {
        this.t = t;
        cache.put(t.getUniqueId(), this);
        lifetime = System.currentTimeMillis()+5000;
        this.caster = caster;
    }

    public void onHit(BiConsumer<IEntity,IEntity> consumer) {
        this.consumer = consumer;
    }

    public void process(IEntity target) {
        consumer.accept(caster,target);
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
