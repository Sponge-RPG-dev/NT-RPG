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

package cz.neumimto.players;

import cz.neumimto.players.groups.NClass;

/**
 * Created by NeumimTo on 28.7.2015.
 */
public class ExtendedNClass {
    public static ExtendedNClass Default = new ExtendedNClass() {{
        setnClass(NClass.Default);
        setPrimary(true);
    }};
    private NClass nClass;
    private double experiences;
    private boolean isPrimary;
    private int level;

    public NClass getnClass() {
        return nClass;
    }

    public void setnClass(NClass nClass) {
        this.nClass = nClass;
    }

    public double getExperiences() {
        return experiences;
    }

    public void setExperiences(double experiences) {
        this.experiences = experiences;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public int getLevel() {
        return level;
    }
}
