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

package cz.neumimto.rpg.utils;


import org.spongepowered.api.scheduler.Task;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by NeumimTo on 17.7.2015.
 */
public class SASTask<T, U> {
    Function<T, U> function;
    Consumer<U> consumer;

    public SASTask async(Function<T, U> funct) {
        this.function = funct;
        return this;
    }

    public SASTask sync(Consumer<U> consumer) {
        this.consumer = consumer;
        return this;
    }

    public void start(T t, Object plugin) {

        Task.Builder taskBuilder = Task.builder();
        taskBuilder.async().execute(() -> {
            U u1 = function.apply(t);
            Task.builder().execute(() -> consumer.accept(u1)).submit(plugin);
        }).submit(plugin);

    }

}
