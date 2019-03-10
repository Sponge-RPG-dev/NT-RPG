package cz.neumimto.rpg.commands.admin;

import static cz.neumimto.rpg.Log.info;
import static cz.neumimto.rpg.Log.warn;
import cz.neumimto.core.ioc.IoC;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.Log;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.persistance.ClassDefinitionDao;
import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PreloadCharacter;
import cz.neumimto.rpg.scripting.JSLoader;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.PlayerSkillContext;
import cz.neumimto.rpg.skills.SkillService;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ReloadExecutor implements CommandExecutor {
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String[] a = args.<String>getOne("args").get().split(" ");
		if (a[0].equalsIgnoreCase("js")) {
			JSLoader jsLoader = IoC.get().build(JSLoader.class);
			jsLoader.initEngine();

			int i = 1;
			String q = null;
			while (i < a.length) {
				q = a[i];
				if (q.equalsIgnoreCase("skills") || q.equalsIgnoreCase("s")) {
					jsLoader.reloadSkills();
					CharacterService build = IoC.get().build(CharacterService.class);
					SkillService skillService = IoC.get().build(SkillService.class);
					build.getCharacters()
							.stream()
							.forEach(qw -> {
								Map<String, PlayerSkillContext> skills = qw.getSkills();
								for (Map.Entry<String, PlayerSkillContext> entry : skills.entrySet()) {
									if (entry.getValue() == PlayerSkillContext.Empty) {
										continue;
									}
									PlayerSkillContext value = entry.getValue();
									Optional<ISkill> byId = skillService.getById(value.getSkill().getId());
									if (!byId.isPresent()) {
										throw new RuntimeException("Unabled to reload the skill " + value.getSkill().getId() + ". "
												+ "Restart the server");
									}
									ISkill skill = byId.get();
									value.setSkill(skill);
									value.getSkillData().setSkill(skill);
								}
							});
				}
				if (q.equalsIgnoreCase("attributes") || q.equalsIgnoreCase("a")) {
					jsLoader.reloadAttributes();
				}
				if (q.equalsIgnoreCase("globaleffects") || q.equalsIgnoreCase("g")) {
					jsLoader.reloadGlobalEffects();
				}
				i++;
			}
		} else if (a[0].equalsIgnoreCase("skilltree")) {
			IoC.get().build(SkillService.class).reloadSkillTrees();
		} else if (a[0].equalsIgnoreCase("settings")) {
			NtRpgPlugin.GlobalScope.plugin.reloadMainPluginConfig();
		} else if (a[0].equalsIgnoreCase("mobs")) {
			IoC.get().build(EntityService.class).reloadMobConfiguration();
		} else if (a[0].equalsIgnoreCase("classes")) {
			//Check if configs are ok
			warn("[RELOAD] Attempting to reload classes from config files...");
			info("[RELOAD] Checking class files: ");
			ClassDefinitionDao build = IoC.get().build(ClassDefinitionDao.class);
			try {
				build.parseClassFiles();
				info("[RELOAD] Class files ok");

				//Get all objects we need to save
				Set<CharacterBase> characterBases = new HashSet<>();
				for (Player player : Sponge.getServer().getOnlinePlayers()) {
					IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
					if (character.isStub()) {
						continue;
					}
					characterBases.add(character.getCharacterBase());
				}

				//Set Char stubs
				for (Player player : Sponge.getServer().getOnlinePlayers()) {
					IActiveCharacter character = NtRpgPlugin.GlobalScope.characterService.getCharacter(player);
					if (character.isStub()) {
						continue;
					}
					PreloadCharacter preloadCharacter = NtRpgPlugin.GlobalScope.characterService.buildDummyChar(player.getUniqueId());
					NtRpgPlugin.GlobalScope.characterService.registerDummyChar(preloadCharacter);
				}
				Log.info("[RELOAD] Purging effect caches");
				NtRpgPlugin.GlobalScope.effectService.purgeEffectCache();
				NtRpgPlugin.GlobalScope.effectService.stop();
				//todo purge all skills
				//todo purge all skilltrees

				for (CharacterBase characterBase : characterBases) {
					Log.info("[RELOAD] saving character " + characterBase.getLastKnownPlayerName());
					NtRpgPlugin.GlobalScope.characterService.save(characterBase);
				}


				//we should be ready to start loading stuff back
				Sponge.getScheduler().createTaskBuilder().execute(() -> {
					//System.gc(); - for reloading skills its required

					NtRpgPlugin.GlobalScope.effectService.start();
					//todo load skills
					//todo load skilltrees

					NtRpgPlugin.GlobalScope.classService.loadClasses();
					Comparator<CharacterBase> cmp = Comparator.comparing(CharacterBase::getUpdated);
					for (Player player : Sponge.getServer().getOnlinePlayers()) {
						List<CharacterBase> playersCharacters =
								NtRpgPlugin.GlobalScope.characterService.getPlayersCharacters(player.getUniqueId());
						if (playersCharacters.isEmpty()) {
							continue;
						}
						CharacterBase max = playersCharacters.stream().max(cmp).get();
						ActiveCharacter activeCharacter = NtRpgPlugin.GlobalScope.characterService.createActiveCharacter(player.getUniqueId(), max);
						NtRpgPlugin.GlobalScope.characterService.setActiveCharacter(player.getUniqueId(), activeCharacter);
						NtRpgPlugin.GlobalScope.characterService.invalidateCaches(activeCharacter);
						NtRpgPlugin.GlobalScope.characterService.assignPlayerToCharacter(player);
					}
				}).submit(this);


			} catch (ObjectMappingException e) {
				src.sendMessage(Text.of("Errors occured during class reload, check server console for more informations"));
			}
		} else {
			src.sendMessage(TextHelper.parse("js[s/a/g] skilltree [r,a] classes"));
			return CommandResult.empty();
		}
		return CommandResult.success();
	}
}
