package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.ISkill;

import java.util.UUID;

public class ACFBootstrap {


    public static void initializeACF(CommandManager manager, BaseCommand... commandClasses) {
        manager.getCommandContexts().registerIssuerAwareContext(IGlobalEffect.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getEffectService().getGlobalEffect(s.toLowerCase());
        });
        manager.getCommandCompletions().registerAsyncCompletion("effect", c ->
                Rpg.get().getEffectService().getGlobalEffects().keySet()
        );


        manager.getCommandCompletions().registerAsyncCompletion("skill", c ->
                Rpg.get().getSkillService().getSkills().keySet()
        );

        manager.getCommandContexts().registerIssuerAwareContext(ISkill.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getSkillService().getById(s.toLowerCase());
        });

        manager.getCommandCompletions().registerAsyncCompletion("learnedSkill", c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            return character.getSkills().keySet();
        });

        manager.getCommandCompletions().registerAsyncCompletion("class", c -> Rpg.get().getClassService().getClasses().keySet());

        for (BaseCommand o : commandClasses) {
            manager.registerCommand(o);
        }
    }
}
