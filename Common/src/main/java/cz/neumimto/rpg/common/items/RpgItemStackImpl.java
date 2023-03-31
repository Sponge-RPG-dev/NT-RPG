package cz.neumimto.rpg.common.items;

public class RpgItemStackImpl implements RpgItemStack {

    protected RpgItemType rpgItemType;

    public RpgItemStackImpl(RpgItemType rpgItemType) {
        this.rpgItemType = rpgItemType;
    }

    @Override
    public RpgItemType getItemType() {
        return rpgItemType;
    }


    @Override
    public String toString() {
        return "RpgItemStackImpl{" +
                "rpgItemType=" + rpgItemType +
                '}';
    }
}
