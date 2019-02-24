package cz.neumimto.rpg.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.text.format.TextColor;

@ConfigSerializable
public class ClassTypeDefinition implements Comparable<ClassTypeDefinition> {

    @Setting("PrimaryColor")
    private TextColor primaryColor;

    @Setting("SecondaryColor")
    private TextColor secondaryColor;

    @Setting("DyeColor")
    private DyeColor dyeColor;

    @Setting("Changeable")
    private boolean changeable;

    @Setting("Order")
    private int order;

    public ClassTypeDefinition(TextColor primaryColor, TextColor secondaryColor, DyeColor dyeColor, boolean changeable, int order) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.changeable = changeable;
        this.dyeColor = dyeColor;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(ClassTypeDefinition o) {
        return order - o.order;
    }
}
