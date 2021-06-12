package cz.neumimto.rpg.spigot.gui.elements;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.util.GeometryUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MaskPane extends StaticPane {

    private ItemMask mask;
    private Map<Character, GuiItem> binds = new HashMap<>();

    private char dynamicContentMask;
    private Iterator<GuiItem> dynamicContent;

    public MaskPane(int x, int y, ItemMask mask) {
        super(x, y);
        this.mask = mask;
        this.length = mask.mask[0].length();
        this.height = mask.mask.length;
    }

    public void bindItem(char mask, GuiItem guiItem) {
        binds.put(mask, guiItem);
    }

    @Override
    public void display(@NotNull InventoryComponent inventoryComponent, int paneOffsetX, int paneOffsetY, int maxLength,
                        int maxHeight) {
        int length = Math.min(this.length, maxLength);
        int height = Math.min(this.height, maxHeight);

        for (int row = 0; row < mask.mask.length; row++) {
            for (int element = 0; element < mask.mask[row].length(); element++) {
                int x = row, y = element;

                if (isFlippedHorizontally())
                    x = length - x - 1;

                if (isFlippedVertically())
                    y = height - y - 1;

                Map.Entry<Integer, Integer> coordinates = GeometryUtil.processClockwiseRotation(x, y, length, height,
                        getRotation());

                x = coordinates.getKey();
                y = coordinates.getValue();

                if (x < 0 || x >= length || y < 0 || y >= height) {
                    return;
                }
                char slot = mask.mask[row].charAt(element);
                GuiItem item;
                if (slot == dynamicContentMask && dynamicContent != null && dynamicContent.hasNext()) {
                    item = dynamicContent.next();
                } else {
                    item = binds.get(slot);
                }

                int finalRow = getY() + y + paneOffsetY;
                int finalColumn = getX() + x + paneOffsetX;

                inventoryComponent.setItem(item, finalColumn, finalRow);
            }
        }
    }


    public void setDynamicContentMask(char dynamicContentMask) {
        this.dynamicContentMask = dynamicContentMask;
    }

    public void setDynamicContent(List<GuiItem> dynamicContent) {
        this.dynamicContent = dynamicContent.iterator();
    }


    public static class ItemMask {
        private String[] mask;

        public ItemMask(String... mask) {
            this.mask = mask;
        }
    }
}
