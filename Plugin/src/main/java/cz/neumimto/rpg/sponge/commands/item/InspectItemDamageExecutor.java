package cz.neumimto.rpg.sponge.commands.item;

import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.items.ClassItem;
import cz.neumimto.rpg.api.items.ItemClass;
import cz.neumimto.rpg.api.items.RpgItemType;
import cz.neumimto.rpg.sponge.damage.SpongeDamageService;
import cz.neumimto.rpg.entities.EntityService;
import cz.neumimto.rpg.inventory.SpongeItemService;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.PlayerClassData;
import cz.neumimto.rpg.sponge.properties.SpongePropertyService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;
import java.util.function.Function;

public class InspectItemDamageExecutor implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = args.<Player>getOne("player").get();
        SpongeItemService is = NtRpgPlugin.GlobalScope.itemService;
        Optional<ItemStack> itemInHand = player.getItemInHand(HandTypes.MAIN_HAND);
        if (!itemInHand.isPresent()) {
            src.sendMessage(Text.of(player.getName() + " has no item in main hand"));
            return CommandResult.empty();
        }
        ItemStack itemStack = itemInHand.get();
        Optional<RpgItemType> rpgItemType = NtRpgPlugin.GlobalScope.itemService.getRpgItemType(itemStack);
        if (!rpgItemType.isPresent()) {
            src.sendMessage(Text.of(player.getName() + " has no Managed item in main hand"));
            return CommandResult.empty();
        }
        RpgItemType fromItemStack = rpgItemType.get();
        ItemClass itemClass = fromItemStack.getItemClass();
        List<ItemClass> parents = new LinkedList<>();
        ItemClass parent = itemClass.getParent();
        List<Integer> o = new ArrayList<>();
        o.addAll(itemClass.getProperties());
        o.addAll(itemClass.getPropertiesMults());
        while (parent != null) {
            parents.add(parent);
            o.addAll(parent.getPropertiesMults());
            o.addAll(parent.getProperties());
            parent = parent.getParent();
        }
        parents.add(itemClass);
        Collections.reverse(parents);

        List<Text> a = new ArrayList<>();
        for (ItemClass wc : parents) {
            a.addAll(TO_TEXT.apply(wc));
        }
        for (Text text : a) {
            src.sendMessage(text);
        }
        src.sendMessage(Text.of(TextColors.GOLD, "=================="));
        SpongeDamageService ds = NtRpgPlugin.GlobalScope.damageService;
        CharacterService cs = NtRpgPlugin.GlobalScope.characterService;
        EntityService es = NtRpgPlugin.GlobalScope.entityService;
        SpongePropertyService ps = NtRpgPlugin.GlobalScope.spongePropertyService;
        IActiveCharacter character = cs.getCharacter(player);
        src.sendMessage(Text.of(TextColors.RED, "Damage: ", ds.getCharacterItemDamage(character, fromItemStack)));
        src.sendMessage(Text.of(TextColors.RED, "Details: "));
        src.sendMessage(Text.of(TextColors.GRAY, " - From Item: ", character.getBaseWeaponDamage(fromItemStack)));

        Collection<PlayerClassData> values = character.getClasses().values();
        for (PlayerClassData value : values) {
            Set<ClassItem> weapons = value.getClassDefinition().getWeapons();
            for (ClassItem weapon : weapons) {
                if (weapon.getType() == fromItemStack) {
                    src.sendMessage(Text.of(TextColors.GRAY, "  - From Class: " + weapon.getDamage()));
                }
            }
        }


        src.sendMessage(Text.of(TextColors.GRAY, " - From ItemClass: "));
        Iterator<Integer> iterator = o.iterator();
        while (iterator.hasNext()) {
            int integer = iterator.next();
            String nameById = ps.getNameById(integer);

            if (nameById != null && !nameById.endsWith("_mult")) {
                iterator.remove();
            } else continue;

            src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", es.getEntityProperty(character, integer)));
        }
        src.sendMessage(Text.of(TextColors.GRAY, "   - Mult: "));
        iterator = o.iterator();
        while (iterator.hasNext()) {
            int integer = iterator.next();
            String nameById = ps.getNameById(integer);
            src.sendMessage(Text.of(TextColors.GRAY, "   - ", nameById, ":", es.getEntityProperty(character, integer)));
        }

        return CommandResult.success();
    }

    private Function<ItemClass, List<Text>> TO_TEXT = weaponClass -> {
        List<Text> list = new ArrayList<>();
        SpongePropertyService ps = NtRpgPlugin.GlobalScope.spongePropertyService;
        list.add(Text.of(TextColors.GOLD, weaponClass.getName()));
        for (Integer property : weaponClass.getProperties()) {
            list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
        }
        for (Integer property : weaponClass.getPropertiesMults()) {
            list.add(Text.of(TextColors.GRAY, " -> ", ps.getNameById(property)));
        }
        return list;
    };
}
