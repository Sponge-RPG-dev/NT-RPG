package cz.neumimto.dei.entity.database.structure;

import cz.neumimto.dei.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;

import javax.persistence.*;

/**
 * Created by ja on 8.7.16.
 */
@Entity
@Table(name = "dei_table")
public class ItemStackResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ItemStack toItemStack() {
        return Utils.toItemStack(this);
    }
}
