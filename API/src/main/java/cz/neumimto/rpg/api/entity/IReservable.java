

package cz.neumimto.rpg.api.entity;

/**
 * Represents active resource that can be reserved in some conditions
 */
public interface IReservable extends IEntityResource {

    void setReservedAmnout(float f);

    double getReservedAmount();

}
