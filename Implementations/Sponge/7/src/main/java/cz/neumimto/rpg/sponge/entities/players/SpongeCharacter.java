package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.common.entity.players.ActiveCharacter;
import cz.neumimto.rpg.sponge.entities.players.party.SpongeParty;
import cz.neumimto.rpg.sponge.gui.ArmorAndWeaponMenuHelper;
import cz.neumimto.rpg.sponge.gui.SkillTreeViewModel;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpongeCharacter extends ActiveCharacter<Player, SpongeParty> implements ISpongeCharacter {

    protected Map<String, SkillTreeViewModel> skillTreeViewLocation = new HashMap<>();
    private Map<String, Integer> attrTransaction;

    public SpongeCharacter(UUID uuid, CharacterBase base, int propertyCount) {
        super(uuid, base, propertyCount);
    }



    @Override
    public void sendMessage(int channel, String message) {
        switch (channel){
            case 0:
                sendMessage(message);
                break;
            case 2:
                sendNotification(message);
                break;
        }
    }

    @Override
    public void sendNotification(String message) {
        getPlayer().sendMessage(ChatTypes.ACTION_BAR, TextHelper.parse(message));
    }

    @Override
    public boolean isDetached() {
        return getPlayer() == null;
    }

    @Override
    public void sendMessage(String message) {
        getPlayer().sendMessage(TextHelper.parse(message));
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

    @Override
    public Map<String, Integer> getAttributesTransaction() {
        return attrTransaction;
    }

    @Override
    public void setAttributesTransaction(HashMap<String, Integer> map) {
        attrTransaction = map;
    }

    @Override
    public IActiveCharacter updateItemRestrictions() {
        ArmorAndWeaponMenuHelper.reset(this);
        return super.updateItemRestrictions();
    }
}
