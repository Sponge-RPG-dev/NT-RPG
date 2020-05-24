package cz.neumimto.rpg.sponge.entities.players;

import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.entity.PropertyServiceImpl;
import cz.neumimto.rpg.common.entity.players.AbstractCharacterService;
import cz.neumimto.rpg.common.entity.players.CharacterMana;
import cz.neumimto.rpg.sponge.SpongeRpgPlugin;
import cz.neumimto.rpg.sponge.entities.SpongeEntityService;
import cz.neumimto.rpg.sponge.entities.players.party.SpongePartyService;
import cz.neumimto.rpg.sponge.inventory.SpongeInventoryService;
import cz.neumimto.rpg.sponge.utils.PermissionUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static cz.neumimto.rpg.api.logging.Log.error;
import static cz.neumimto.rpg.api.logging.Log.info;


@Singleton
public class SpongeCharacterService extends AbstractCharacterService<ISpongeCharacter> {

    @Inject
    private SpongeRpgPlugin plugin;

    @Inject
    private SpongeEntityService spongeEntityService;

    @Inject
    private SpongePartyService partyService;

    @Inject
    private SpongeInventoryService inventoryService;

    @Override
    public ISpongeCharacter createCharacter(UUID player, CharacterBase characterBase) {
        SpongeCharacter spongeCharacter = new SpongeCharacter(player, characterBase, PropertyServiceImpl.LAST_ID);
        spongeCharacter.setMana(new CharacterMana(spongeCharacter));
        spongeCharacter.setHealth(new SpongeCharacterHealth(spongeCharacter));
        return spongeCharacter;
    }

    @Override
    protected void initSpellbook(ISpongeCharacter activeCharacter, String[][] spellbookPages) {

    }

    @Override
    protected void initSpellbook(ISpongeCharacter activeCharacter, int i, int j, PlayerSkillContext skill) {

    }

    @Override
    public void registerDummyChar(ISpongeCharacter dummy) {
        characters.put(dummy.getUUID(), dummy);
    }

    @Override
    public ISpongeCharacter buildDummyChar(UUID uuid) {
        info("Creating a dummy character for " + uuid);
        return new SpongePreloadCharacter(uuid);
    }

    public ISpongeCharacter getCharacter(Player player) {
        return getCharacter(player.getUniqueId());
    }


    @Override
    public boolean assignPlayerToCharacter(UUID uniqueId) {
        info("Assigning player to character " + uniqueId);
        if (!hasCharacter(uniqueId)) {
            error("Could not find any character for player " + uniqueId + " Auth event not fired?");
            return false;
        }

        ISpongeCharacter character = getCharacter(uniqueId);
        if (character.isStub()) {
            return false;
        }

        Player pl = character.getPlayer();
        if (character.getCharacterBase().getHealthScale() != null) {
            pl.offer(Keys.HEALTH_SCALE, character.getCharacterBase().getHealthScale());
        }
        return true;
    }

    public void setHeathscale(ISpongeCharacter character, double i) {
        character.getCharacterBase().setHealthScale(i);
        character.getPlayer().offer(Keys.HEALTH_SCALE, i);
        putInSaveQueue(character.getCharacterBase());
    }

    @Override
    public void respawnCharacter(ISpongeCharacter character) {
        super.respawnCharacter(character);
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            invalidateCaches(character);
            Double d = character.getHealth().getMaxValue();
            character.getPlayer().offer(Keys.HEALTH, d);
        }).delay(1, TimeUnit.MILLISECONDS).submit(plugin);
    }

    @Override
    protected void scheduleNextTick(Runnable r) {
        Sponge.getScheduler().createTaskBuilder().delay(1, TimeUnit.MILLISECONDS)
                .execute(r).submit(plugin);
    }

    @Override
    public void removePersistantSkill(CharacterSkill characterSkill) {
        playerDao.removePersitantSkill(characterSkill);
    }

    @Override
    public int canCreateNewCharacter(UUID uniqueId, String name) {
        List<CharacterBase> list = getPlayersCharacters(uniqueId);
        if (list.size() >= PermissionUtils.getMaximalCharacterLimit(uniqueId)) {
            return 1;
        }
        if (list.stream().anyMatch(c -> c.getName().equalsIgnoreCase(name))) {
            return 2;
        }
        return 0;
    }

    @Override
    public void notifyCooldown(IActiveCharacter caster, PlayerSkillContext skillInfo, long cd) {
        if (cd > 0) {

            if (caster instanceof ISpongeCharacter) {
                ISpongeCharacter character = (ISpongeCharacter) caster;
                Player player = character.getPlayer();

                String icon = skillInfo.getSkillData().getIcon();

                if (icon != null) {
                    cd /= 50;
                    Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, icon);
                    if (type.isPresent()) {
                        ItemType itemType = type.get();
                        player.getCooldownTracker().setCooldown(itemType, (int) cd);
                    }
                }
            }
        }
    }

    @Override
    public void addExperiences(ISpongeCharacter character, double exp, String source) {
        if ("VANILLA".equals(source)) {
            Player player = character.getPlayer();
           //fix your shit sponge Integer integer = player.get(Keys.EXPERIENCE_FROM_START_OF_LEVEL).orElse(0);
           //fix your shit sponge player.offer(Keys.EXPERIENCE_FROM_START_OF_LEVEL, integer + (int) exp);
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "xp " + (int)exp + " " + player.getName());
        } else {
            super.addExperiences(character, exp, source);
        }
    }

    @Override
    public void updateSpellbook(ISpongeCharacter character, int page, int slot, ISkill o) {
        if (o != null) {
            ItemStack is = inventoryService.createSkillbind(character, o);
            character.getSpellbook()[page - 1][slot - 1] = is;
            character.getCharacterBase().getSpellbookPages()[page - 1][slot - 1] = is.get(Keys.DISPLAY_NAME).get().toPlain();
        } else {
            character.getSpellbook()[page - 1][slot - 1] = null;
            character.getCharacterBase().getSpellbookPages()[page - 1][slot - 1] = null;;
        }
        putInSaveQueue(character.getCharacterBase());
    }
}
