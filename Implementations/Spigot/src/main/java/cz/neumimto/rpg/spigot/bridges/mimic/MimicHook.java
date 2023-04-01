package cz.neumimto.rpg.spigot.bridges.mimic;


import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.mimic.Mimic;
import ru.endlesscode.mimic.MimicApiLevel;
import ru.endlesscode.mimic.classes.BukkitClassSystem;
import ru.endlesscode.mimic.level.BukkitLevelSystem;
import ru.endlesscode.mimic.level.ExpLevelConverter;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MimicHook {

    @Inject
    private ClassService classService;

    @Inject
    private SpigotCharacterService characterService;

    public void init(Plugin plugin) {
        Mimic.getInstance().registerClassSystem(NTClassSystem::new, MimicApiLevel.CURRENT, plugin, ServicePriority.Highest);
        Mimic.getInstance().registerLevelSystem(NtLevelSystem::new, MimicApiLevel.CURRENT, plugin, ServicePriority.Highest);
    }

    private class NTClassSystem extends BukkitClassSystem {

        public NTClassSystem(@NotNull Player player) {
            super(player);
        }

        private ActiveCharacter getCharacter() {
            return characterService.getCharacter(getPlayer());
        }

        @NotNull
        @Override
        public List<String> getClasses() {
            return new ArrayList<>(getCharacter().getClasses().keySet());
        }

        @Nullable
        @Override
        public String getPrimaryClass() {
            return getCharacter().getPrimaryClass().getClassDefinition().getName();
        }

    }

    private class NtLevelSystem extends BukkitLevelSystem {

        public NtLevelSystem(@NotNull Player player) {
            super(player);
        }

        private ActiveCharacter getCharacter() {
            return characterService.getCharacter(getPlayer());
        }

        @NotNull
        @Override
        public ExpLevelConverter getConverter() {
            return null;
        }

        @Override
        public int getLevel() {
            ActiveCharacter character = getCharacter();
            if (character.getPrimaryClass() != null) {
                return character.getPrimaryClass().getCharacterClass().getLevel();
            }
            return 0;
        }

        @Override
        public double getExp() {
            return getCharacter().getPrimaryClass().getCharacterClass().getExperiences();
        }

        @Override
        public void setExp(double v) {

        }

        @Override
        public void setLevel(int i) {

        }
    }

}
