package cz.neumimto.rpg.spigot.scripting.mechanics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cz.neumimto.rpg.common.skills.scripting.Handler;
import cz.neumimto.rpg.common.skills.scripting.Target;
import cz.neumimto.rpg.spigot.SpigotRpg;
import cz.neumimto.rpg.spigot.SpigotRpgPlugin;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

//https://minecraft.fandom.com/wiki/Advancement/JSON_format
@Singleton
public class ToastMechanic {
    private static final Gson gson = new Gson();

    public static final String challenge = "challenge";
    public static final String goal = "goal";
    public static final String frame = "frame";

    @Inject
    private SpigotRpg rpgApi;

    @Handler
    public void sendToPlayer(@Target ISpigotCharacter character,
                             String message,
                             String icon,
                             String nbt,
                             String frame,
                             String background,
                             String desc) {
        NamespacedKey id = new NamespacedKey(SpigotRpgPlugin.getInstance(), "ntrpg-fake-" + UUID.randomUUID());

        Bukkit.getUnsafe().loadAdvancement(id, createToastData(
                message,
                frame,
                icon,
                nbt,
                background,
                desc
        ));
        Player player = character.getPlayer();
        Advancement advancement = Bukkit.getAdvancement(id);
        if (advancement == null) {
            throw new CouldNotCreateToastMessage(message);
        }
        AdvancementProgress advancementProgress = player.getAdvancementProgress(advancement);
        if (!advancementProgress.isDone()) {
            advancementProgress.getRemainingCriteria().forEach(advancementProgress::awardCriteria);
        }
        rpgApi.scheduleSyncLater(() -> {
            if (advancementProgress.isDone()) {
                advancementProgress.getRemainingCriteria().forEach(advancementProgress::revokeCriteria);
            }
            Bukkit.getUnsafe().removeAdvancement(id);
        });
    }

    private String createToastData(String message, String frame, String itemId, String iconNbt, String background, String description) {
        JsonObject root = new JsonObject();

        JsonObject icon = new JsonObject();
        icon.addProperty("item", itemId);
        if (iconNbt != null) {
            icon.addProperty("nbt", iconNbt);
        }
        root.add("icon", icon);

        JsonObject display = new JsonObject();


        display.addProperty("title", message);
        display.addProperty("frame", frame);
        if (background == null) {
            background = "minecraft:textures/gui/advancements/backgrounds/adventure.png";
        }
        display.addProperty("background", background);
        display.addProperty("description", description);
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", false);
        display.addProperty("hidden", true);
        root.add("display", display);

        JsonObject criteria = new JsonObject();
        JsonObject trigger = new JsonObject();
        trigger.addProperty("trigger", "minecraft:impossible");
        criteria.add("impossible", trigger); //command advancement
        root.add("criteria", criteria);


        return gson.toJson(root);
    }

    private static class CouldNotCreateToastMessage extends RuntimeException {
        public CouldNotCreateToastMessage(String message) {
            super(message);
        }
    }

}
