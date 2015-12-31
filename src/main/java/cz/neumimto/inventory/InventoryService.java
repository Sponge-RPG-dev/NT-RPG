/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.inventory;

import cz.neumimto.configuration.Localization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.damage.DamageService;
import cz.neumimto.effects.EffectBase;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.SkillService;
import cz.neumimto.utils.ItemStackUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class InventoryService {

    @Inject
    private SkillService skillService;

    @Inject
    private Game game;

    @Inject
    private CharacterService characterService;

    @Inject
    private EffectService effectService;

    @Inject
    private DamageService damageService;

    private Map<UUID, InventoryMenu> inventoryMenus = new HashMap<>();

    public static final ItemType ITEM_SKILL_BIND = ItemTypes.BLAZE_POWDER;

    public ItemStack getHelpItem(List<String> lore, ItemType type) {
        ItemStack.Builder builder = ItemStack.builder();
        builder.quantity(1).itemType(type);
        return builder.build();
    }

    public Map<UUID, InventoryMenu> getInventoryMenus() {
        return inventoryMenus;
    }

    public void addInventoryMenu(UUID uuid, InventoryMenu menu) {
        if (!inventoryMenus.containsKey(uuid))
            inventoryMenus.put(uuid, menu);
    }

    public InventoryMenu getInventoryMenu(UUID uniqueId) {
        return inventoryMenus.get(uniqueId);
    }

    public GridInventory getInventory(UUID uniqueId) {
        return null;
    }


    public void initializeHotbar(IActiveCharacter character) {
        for (int i = 0; i < 10; i++) {
            initializeHotbar(character, i);
        }
    }

    public void initializeHotbar(IActiveCharacter character, int slot) {
        Player player = character.getPlayer();
        CarriedInventory<? extends Carrier> inventory = player.getInventory();
        Iterable<Inventory> slots = inventory.slots();

    }

    protected HotbarObject getHotbarObject(IActiveCharacter character, ItemStack is) {
        if (is == null)
            return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        if (is.getItem() == ITEM_SKILL_BIND) {
            return buildHotbarSkill(character, is);
        }
        if (ItemStackUtils.isWeapon(is.getItem())) {
            return buildHotbarWeapon(character, is);
        }
        return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
    }

    private Weapon buildHotbarWeapon(IActiveCharacter character, ItemStack is) {
        Map<IGlobalEffect, Integer> itemEffects = ItemStackUtils.getItemEffects(is);
        Weapon w = new Weapon(is.getItem());
        w.setEffects(itemEffects);

        return w;
    }

    private HotbarObject buildHotbarConsumable(IActiveCharacter character, ItemStack is) {
        return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
    }

    protected HotbarSkill buildHotbarSkill(IActiveCharacter character, ItemStack is) {
        HotbarSkill skill = null;
        Optional<Text> text = is.get(Keys.DISPLAY_NAME);
        if (text.isPresent()) {
            Text text1 = text.get();
            if (text1.getColor() == TextColors.GOLD && text1.getStyle() == TextStyles.ITALIC) {
                skill = new HotbarSkill();
                String s = Texts.toPlain(text1);
                String[] split = s.split(" ");
                for (String s1 : split) {
                    if (s1.endsWith("«")) {
                        skill.left_skill = skillService.getSkill(s1.substring(0,s1.length()-2));
                    } else if (s1.startsWith("»")) {
                        skill.right_skill = skillService.getSkill(s1.substring(0,s1.length()-2));
                    }
                }
            }
        }
        return skill;
    }

    private static Text ItemBindFirstLoreLineLine = Texts.of(TextColors.RED, "▒▒▒▒▒▒▒▒▒");

    //todo is it possible to attach persistent custom data to an itemstacks except lore?
    public void createHotbarSkill(ItemStack is, ISkill right, ISkill left) {
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        List<Text> lore;
        if (texts.isPresent()) {
            lore = texts.get();
        } else {
            lore = new ArrayList<>();
        }
        is.offer(Keys.DISPLAY_NAME, Texts.of(TextColors.GOLD, TextStyles.ITALIC, left != null ? left.getName() + " «" : "", right != null ? "» " +right.getName() : ""));
        lore.add(ItemBindFirstLoreLineLine);
        if (right != null) {
            lore.add(Texts.of(Localization.CAST_SKILL_ON_RIGHTLICK.replaceAll("%1", right.getName())));
            if (right.getDescription() != null)
                lore.add(Texts.of("* " + right.getDescription()));
            if (right.getLore() != null)
                lore.add(Texts.of(TextStyles.ITALIC,"* " + right.getLore()));
        }
        if (left != null) {
            lore.add(Texts.of(Localization.CAST_SKILL_ON_RIGHTLICK.replaceAll("%1", left.getName())));
            if (left.getDescription() != null)
                lore.add(Texts.of("* " + left.getDescription()));
            if (left.getLore() != null)
                lore.add(Texts.of(TextStyles.ITALIC,"* " + left.getLore()));
        }
        lore.add(Texts.of(TextColors.GRAY, Localization.ITEM_SKILLBIND_FOOTER));
        is.offer(Keys.ITEM_LORE, lore);
    }

    public void onRightClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
            hotbarObject.onRightClick(character);
        }
    }

    public void onLeftClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
            hotbarObject.onLeftClick(character);
        }
    }

    public void changeEquipedWeapon(IActiveCharacter character, ItemStack weapon) {
        //old
        Weapon mainHand = character.getMainHand();
        effectService.removeGlobalEffectsAsEnchantments(mainHand.getEffects(),character);

        //new
        Weapon weapon1 = buildHotbarWeapon(character, weapon);
        effectService.applyGlobalEffectsAsEnchantments(weapon1.getEffects(),character);

        int slot = mainHand.getSlot();
        character.setHotbarSlot(slot, weapon1);

        damageService.recalculateCharacterWeaponDamage(character);
    }
}
