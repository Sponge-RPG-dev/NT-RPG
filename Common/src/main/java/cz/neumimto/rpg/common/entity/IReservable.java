

package cz.neumimto.rpg.common.entity;

/**
 * Represents active resource that can be reserved in some conditions
 */
public interface IReservable extends IEntityResource {

    void setReservedAmnout(float f);

    double getReservedAmount();

}
