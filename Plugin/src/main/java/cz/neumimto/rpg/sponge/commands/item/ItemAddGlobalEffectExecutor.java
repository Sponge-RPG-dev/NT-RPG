package cz.neumimto.rpg.sponge.commands.item;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.sponge.NtRpgPlugin;
import cz.neumimto.rpg.api.effects.EffectParams;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.sponge.configuration.Localizations;
import cz.neumimto.rpg.api.effects.model.EffectModelFactory;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemAddGlobalEffectExecutor implements CommandExecutor {
    private static Gson gson = new Gson();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
        if (!(src instanceof Player)) {
            throw new CommandException(Text.of("Only for players"));
        }
        Player player = (Player) src;
        if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
            ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();

            Optional<String> dataOptional = args.getOne("data");
            Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
            EffectParams map = new EffectParams();

            if (!dataOptional.isPresent()) {
                if (modelType != Void.TYPE)
                    throw new CommandException(Text.of("Effect data expected! Use ? as data to list parameters"));
            } else {
                String s = dataOptional.get();
                if (s.equals("?")) {
                    if (modelType == Void.TYPE) {
                        player.sendMessage(Text.of("No data expected"));
                        return CommandResult.empty();
                    } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                        player.sendMessage(Text.of("Expected: " + modelType.getTypeName()));
                        return CommandResult.empty();
                    } else {
                        Map<String, String> q = new HashMap<>();
                        for (Field field : modelType.getFields()) {
                            q.put(field.getName(), field.getType().getName());
                        }
                        player.sendMessage(Text.of("Expected: " + gson.toJson(q)));
                        return CommandResult.empty();
                    }
                }
                if (modelType == Void.TYPE) {
                    //Just do nothing
                } else if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
                    map.put("value", s);
                } else try {
                    //Get rid of unused entries in data string and check for missing
                    EffectParams tempMap = gson.fromJson(s, EffectParams.class);
                    for (Field field : modelType.getFields()) {
                        if (Modifier.isTransient(field.getModifiers())) continue;
                        if (!tempMap.containsKey(field.getName())) {
                            throw new CommandException(Text.of("Missing parameter: " + field.getName()));
                        }
                        map.put(field.getName(), tempMap.get(field.getName()));
                    }
                } catch (JsonSyntaxException e) {
                    Map<String, String> q = new HashMap<>();
                    for (Field field : modelType.getFields()) {
                        q.put(field.getName(), field.getType().getName());
                    }
                    throw new CommandException(Text.of("Expected: " + gson.toJson(q)));
                }
            }

            itemStack = NtRpgPlugin.GlobalScope.inventorySerivce.addEffectsToItemStack(itemStack, effect.getName(), map);
            itemStack = NtRpgPlugin.GlobalScope.inventorySerivce.updateLore(itemStack);
            player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
            player.sendMessage(TextHelper.parse("Enchantment " + effect.getName() + " added"));
            return CommandResult.success();
        } else {
            player.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
        }
        return CommandResult.empty();
    }
}
