package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.text.format.TextColor;

@ConfigSerializable
public class ClassTypeDefinition {

    @Setting("PrimaryColor")
    private TextColor primaryColor;

    @Setting("SecondaryColor")
    private TextColor secondaryColor;

    @Setting("DyeColor")
    private DyeColor dyeColor;

    @Setting("Changeable")
    private boolean changeable;


    public ClassTypeDefinition(TextColor primaryColor, TextColor secondaryColor, DyeColor dyeColor, boolean changeable) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.changeable = changeable;
        this.dyeColor = dyeColor;
    }

    public ClassTypeDefinition() {
    }

    public TextColor getPrimaryColor() {
        return primaryColor;
    }

    public TextColor getSecondaryColor() {
        return secondaryColor;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }
}
