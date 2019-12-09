package cz.neumimto.rpg.sponge.gui.vanilla;

import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.configuration.PluginConfig;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.localization.LocalizationKeys;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.TextHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterAttributesGui extends VanillaGui {

    private ISpongeCharacter character;

    public CharacterAttributesGui(ISpongeCharacter character) {
        super(character.getPlayer().getUniqueId());

        this.character = character;
        this.inventory = characterEmptyInventory(character).build(plugin);
        makeBorder(DyeColors.ORANGE);

        createAttributeRow();
        createCommitAttributeTxButton();

        setItem(SlotPos.of(1, 1), itemAction(ItemTypes.PAPER, translate(LocalizationKeys.BACK), Gui::displayCharacterMenu));

        PluginConfig pluginConfig = Rpg.get().getPluginConfig();
        if (pluginConfig.RESPEC_ATTRIBUTES) {
            ItemStack btnRespec = itemCommand(ItemTypes.BARRIER, translate(LocalizationKeys.RESPEC_ATTRIBUTES), "char attributes-respec");
            setItem(SlotPos.of(7, 5), btnRespec);
        }
    }

    private void createAttributeRow() {
        int attributePoints = character.getAttributePoints();
        for (Integer value : character.getAttributesTransaction().values()) {
            attributePoints -= value;
        }

        Collection<AttributeConfig> attribs = Rpg.get().getPropertyService().getAttributes().values();
        List<AttributeConfig> allOf = attribs.stream().filter(a -> !a.isHidden()).collect(Collectors.toList());
        int q = 1;
        for (AttributeConfig attribute : allOf) {

            List<Text> lore = TextHelper.splitStringByDelimiter(attribute.getDescription());

            int totalAmount = 0;

            Integer amount = character.getCharacterBase().getAttributes().get(attribute.getId());
            amount = amount == null ? 0 : amount;
            totalAmount += amount;

            Integer transientAmount = character.getTransientAttributes().get(attribute.getId());
            transientAmount = transientAmount == null ? 0 : totalAmount;
            totalAmount += transientAmount;

            Integer transacted = character.getAttributesTransaction().get(attribute.getId());
            transacted = transacted == null ? 0 : transacted;

            String attrString = attribute.getName() + " (" + totalAmount + ")";
            String transactedString = transacted != 0 ? " (+" + transacted + ")" : "";
            lore.add(Text.builder(amount + transactedString + " / " + attribute.getMaxValue()).build());

            ItemStack itemStack = getItem(SlotPos.of(q, 3));
            if (!itemStack.isEmpty()) {
                itemStack.offer(Keys.DISPLAY_NAME, Text.of(attrString));
                itemStack.offer(Keys.ITEM_LORE, lore);
            } else {
                itemStack = itemStack(itemType(attribute.getItemType()), Text.of(attrString), lore);
                setItem(SlotPos.of(q, 3), itemStack);
            }

            if (attributePoints > 0) {
                String command = "char attribute " + attribute.getId() + " 1";
                ItemStack btn = getItem(SlotPos.of(q, 2));
                if (btn.isEmpty()) {
                    btn = itemAction(ItemTypes.SUGAR, Text.of(TextColors.GREEN, "+"), (character) -> {
                        Sponge.getCommandManager().process(character.getPlayer(), command);
                        createAttributeRow();
                        createCommitAttributeTxButton();
                    });
                    setItem(SlotPos.of(q, 2), btn);
                }
            } else {
                setItem(SlotPos.of(q, 2), ItemStack.empty());
            }

            q++;
        }
    }

    private void createCommitAttributeTxButton() {
        int attributePoints = character.getAttributePoints();
        for (Integer value : character.getAttributesTransaction().values()) {
            attributePoints -= value;
        }

        Text name = Text.builder("Commit").color(TextColors.GREEN).build();
        List<Text> lore = new ArrayList<>();
        lore.add(Text.builder("Remaining Attribute Points: " + attributePoints).color(attributePoints == 0 ? TextColors.RED : TextColors.GREEN).build());

        ItemStack commit = getItem(SlotPos.of(7, 1));
        if (commit.isEmpty()) {
            commit = itemAction(ItemTypes.DIAMOND, name, c -> {
                Sponge.getCommandManager().process(character.getPlayer(), "char tx-attribute-commit");
                Gui.displayCharacterMenu(c);
            });
            setItem(SlotPos.of(7, 1), commit);
        }
        commit.offer(Keys.ITEM_LORE, lore);
    }

    protected Text translate(String id) {
        return TextHelper.parse(Rpg.get().getLocalizationService().translate(id));
    }
}
