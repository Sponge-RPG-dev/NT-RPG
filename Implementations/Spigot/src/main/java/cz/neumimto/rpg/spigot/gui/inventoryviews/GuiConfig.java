package cz.neumimto.rpg.spigot.gui.inventoryviews;

import com.electronwill.nightconfig.core.conversion.Path;

import java.util.List;

public class GuiConfig {

    @Path("TranslationKey")
    public String translationkey;

    @Path("Name")
    public String name;

    @Path("Inventory")
    public List<String> inventory;

    @Path("Mask")
    public List<MaskConfig> mask;

    @Path("Skip")
    public String content;

    public static class MaskConfig {

        @Path("C")
        public String C;
        @Path("TranslationKey")
        public String translationKey;
        @Path("Id")
        public String id;
        @Path("Model")
        public Integer model;
        @Path("Supplier")
        public String supplier;
        @Path("OnClick")
        public OnClick onClick;
        @Path("Tags")
        public List<String> tags;
    }

    public static class OnClick {
        @Path("Command")
        public String command;
    }
}
