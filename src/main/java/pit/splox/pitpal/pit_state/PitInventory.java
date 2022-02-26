package pit.splox.pitpal.pit_state;

import java.util.function.BooleanSupplier;

import akka.japi.Predicate;
import net.minecraft.item.ItemStack;
import pit.splox.pitpal.Module;

public class PitInventory extends Module {
    private final ItemStack[] items = new ItemStack[40];
    public final int size = 40;
    private final boolean hasMystics;
    public PitInventory(ItemStack[] items) {
        boolean hasMystics = false;
        for (int i = 0; i < items.length; i++) {
            this.items[i] = items[i];

            if (!hasMystics && items[i] != null 
                    && items[i].getTagCompound() != null
                    && items[i].getTagCompound().getCompoundTag("ExtraAttributes").hasKey("MaxLives")
                    && items[i].getTagCompound().getCompoundTag("ExtraAttributes").getInteger("MaxLives") != 1) {
                hasMystics = true;
            }
        }
        this.hasMystics = hasMystics;
    }
    public PitInventory() {
        this.hasMystics = false;
    }

    public ItemStack getItem(int i) {
        return items[i];
    }

    public boolean has(Predicate<ItemStack> criteria) {
        for (int i = 0; i < items.length; i++) {
            if (criteria.test(items[i])) {
                return true;
            }
        }

        return false;
    }

    public boolean hasPerishableMystics() {
        return this.hasMystics;
    }
}
