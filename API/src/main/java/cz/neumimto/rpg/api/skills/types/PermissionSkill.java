/*  Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
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
 */

package cz.neumimto.rpg.api.skills.types;

import com.typesafe.config.Config;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.permissions.PermissionService;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.SkillData;
import cz.neumimto.rpg.api.skills.SkillExecutionType;
import cz.neumimto.rpg.api.skills.SkillResult;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.api.skills.utils.SkillLoadingErrors;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionSkill extends AbstractSkill<IActiveCharacter> {

    @Inject
    private PermissionService permissionService;

    @Override
    public SkillResult onPreUse(IActiveCharacter character, PlayerSkillContext esi) {
        return SkillResult.CANCELLED;
    }

    @Override
    public SkillExecutionType getSkillExecutionType() {
        return SkillExecutionType.PASSIVE;
    }

    @Override
    public void skillLearn(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillLearn(IActiveCharacter, context);
        PermissionData permissionData = (PermissionData) context.getSkillData();
        if (permissionData.permissions != null) {
            permissionService.addPermissions(IActiveCharacter, permissionData.permissions);
        }
    }

    @Override
    public void skillRefund(IActiveCharacter IActiveCharacter, PlayerSkillContext context) {
        super.skillRefund(IActiveCharacter, context);
        PermissionData permissionData = (PermissionData) context.getSkillData();
        if (permissionData.permissions != null) {
            permissionService.removePermissions(IActiveCharacter, permissionData.permissions);
        }
    }

    @Override
    public <T extends SkillData> void loadSkillData(T skillData, SkillTree context, SkillLoadingErrors errors, Config c) {
        PermissionData permissionData = (PermissionData) skillData;
        List<String> permissions = c.getStringList("Permissions");
        if (permissions != null) {
            permissionData.permissions = new HashSet<>(permissions);
        }
    }

    @Override
    public SkillData constructSkillData() {
        return new PermissionData(getId());
    }

    public class PermissionData extends SkillData {
        private Set<String> permissions;

        public PermissionData(String skill) {
            super(skill);
        }

        public Set<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<String> permissions) {
            this.permissions = permissions;
        }
    }
}
