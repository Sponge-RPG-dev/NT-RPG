package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.ISkill;

import java.util.UUID;

public class ACFBootstrap {


    public static void initializeACF(CommandManager manager, BaseCommand... commandClasses) {

        manager.getCommandCompletions().registerAsyncCompletion("effect", c ->
                Rpg.get().getEffectService().getGlobalEffects().keySet()
        );

        manager.getCommandContexts().registerContext(IGlobalEffect.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getEffectService().getGlobalEffect(s.toLowerCase());
        });


        manager.getCommandCompletions().registerAsyncCompletion("skill", c ->
                Rpg.get().getSkillService().getSkills().keySet()
        );

        manager.getCommandCompletions().registerAsyncCompletion("learnedSkill", c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            return character.getSkills().keySet();
        });

        manager.getCommandContexts().registerContext(ISkill.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getSkillService().getById(s.toLowerCase());
        });


        manager.getCommandCompletions().registerAsyncCompletion("class", c ->
                Rpg.get().getClassService().getClasses().keySet());

        manager.getCommandContexts().registerContext(ClassDefinition.class, c -> {
            String firstArg = c.getFirstArg();
            c.popFirstArg();
            return Rpg.get().getClassService().getClassDefinitionByName(firstArg);
        });

        manager.getCommandCompletions().registerAsyncCompletion("attribute", c ->
                Rpg.get().getPropertyService().getAttributes().keySet());

        manager.getCommandContexts().registerContext(AttributeConfig.class, c -> {
            String firstArg = c.getFirstArg();
            c.popFirstArg();
            return Rpg.get().getPropertyService().getAttributeById(firstArg);
        });
                
        for (BaseCommand o : commandClasses) {
            manager.registerCommand(o);
        }
    }
}
