package cz.neumimto.rpg.common.configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ClassTypeDefinition implements Comparable<ClassTypeDefinition> {

    @Setting("PrimaryColor")
    private String primaryColor;

    @Setting("SecondaryColor")
    private String secondaryColor;

    @Setting("DyeColor")
    private String dyeColor;

    @Setting("Changeable")
    private boolean changeable;

    @Setting("Order")
    private int order;

    public ClassTypeDefinition(String primaryColor, String secondaryColor, String dyeColor, boolean changeable, int order) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.changeable = changeable;
        this.dyeColor = dyeColor;
        this.order = order;
    }

    public ClassTypeDefinition() {
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public String getDyeColor() {
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
