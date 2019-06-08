package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.common.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpongeCharacter extends ActiveCharacter implements ISpongeCharacter {

    protected Map<String, SkillTreeViewModel> skillTreeViewLocation = new HashMap<>();

    public SpongeCharacter(UUID uuid, JPACharacterBase base, int propertyCount) {
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
    public void sendMessage(MessageChannel channel, String message) {
        Player player = getEntity();
        switch (channel) {

        }
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
