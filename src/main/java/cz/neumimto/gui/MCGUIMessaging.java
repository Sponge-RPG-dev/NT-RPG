package cz.neumimto.gui;

import cz.neumimto.effects.EffectStatusType;
import cz.neumimto.effects.IEffect;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.players.CharacterBase;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.SkillInfo;
import cz.neumimto.skills.SkillTree;
import djxy.api.MinecraftGuiService;
import djxy.models.component.Component;
import djxy.models.component.ComponentAttribute;
import djxy.models.component.ComponentType;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;
import java.util.Map;

/**
 * Created by NeumimTo on 6.8.2015.
 */
@Singleton
public class MCGUIMessaging implements IPlayerMessage {

    @Inject
    private MinecraftGuiService mcGuiService;

    @Inject
    private NComponentManager componentManager;

    @Inject
    private GuiService guiService;

    @Override
    public boolean isClientSideGui() {
        return true;
    }

    @Override
    public void sendMessage(IActiveCharacter player, String message) {

    }

    @Override
    public void sendCooldownMessage(IActiveCharacter player, String message, long cooldown) {

    }

    @Override
    public void openSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkills) {


    }

    @Override
    public void moveSkillTreeMenu(IActiveCharacter player, SkillTree skillTree, Map<String, Integer> learnedSkill, SkillInfo center) {
        Component panel = new Component(componentManager.root);
        panel.setId("SkilltreeMenu");
        panel.setType(ComponentType.PANEL);
        String centericonurl = guiService.getIconURI(center.getSkillName());

        final double centerX = ((double) panel.getAttributes().getAttribute(ComponentAttribute.WIDTH.name())) / 2;
        final double centerY = ((double) panel.getAttributes().getAttribute(ComponentAttribute.HEIGHT.name())) / 2;
        double angle = 0;
        for (SkillInfo info : center.getDepending()) {

            int radius = 20;
            double x = Math.cos(angle) * radius + centerX;
            double y = Math.sin(angle) * radius + centerY;

            Component buttoncenter = new Component(ComponentType.BUTTON, panel);
            buttoncenter.getAttributes().setAttribute(ComponentAttribute.X_RELATIVE.name(), x);
            buttoncenter.getAttributes().setAttribute(ComponentAttribute.Y_RELATIVE.name(), y);
            Component image = new Component(ComponentType.IMAGE, buttoncenter);
            image.getAttributes().setAttribute(ComponentAttribute.IMAGE_NAME.name(), info.getSkillName());
            image.getAttributes().setAttribute(ComponentAttribute.IMAGE_TYPE.name(), "png");
            Component level = new Component(ComponentType.PARAGRAPH, image);
            level.getAttributes().setAttribute(ComponentAttribute.POSITION.name(), "");
            angle = +360 / center.getDepending().size();
        }

    }

    @Override
    public void sendEffectStatus(IActiveCharacter player, EffectStatusType type, IEffect effect) {

    }

    @Override
    public void invokeCharacterMenu(Player player, List<CharacterBase> characterBases) {

    }

    @Override
    public void sendManaStatus(IActiveCharacter character, float currentMana, float maxMana, float reserved) {

    }

    @Override
    public void sendPlayerInfo(IActiveCharacter character, List<CharacterBase> target) {

    }
}
