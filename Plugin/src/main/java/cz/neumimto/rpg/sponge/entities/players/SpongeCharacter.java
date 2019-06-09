package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.sponge.entities.players.party.SpongeParty;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpongeCharacter extends ActiveCharacter<Player, SpongeParty> implements ISpongeCharacter {

    protected Map<String, SkillTreeViewModel> skillTreeViewLocation = new HashMap<>();

    public SpongeCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }

    @Override
    public UUID getUUID() {
        return pl;
    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public boolean isDetached() {
        return getPlayer() == null;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public Map<String, SkillTreeViewModel> getSkillTreeViewLocation() {
        return skillTreeViewLocation;
    }

    @Override
    public SkillTreeViewModel getLastTimeInvokedSkillTreeView() {
        for (SkillTreeViewModel skillTreeViewModel : skillTreeViewLocation.values()) {
            if (skillTreeViewModel.isCurrent()) {
                return skillTreeViewModel;
            }
        }
        return null;
    }
}
