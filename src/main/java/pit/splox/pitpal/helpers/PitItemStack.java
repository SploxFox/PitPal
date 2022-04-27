package pit.splox.pitpal.helpers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PitItemStack {
    public final ItemStack itemStack;
    protected PitItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        itemStacks.put(itemStack.getTagCompound(), this);
    }

    // Some simple code to avoid unnecessary creation of new objects all the time
    private static Map<NBTTagCompound, PitItemStack> itemStacks = new HashMap<NBTTagCompound, PitItemStack>();
    public static PitItemStack from(ItemStack itemStack) {
        if (itemStacks.containsKey(itemStack.getTagCompound())) {
            return itemStacks.get(itemStack.getTagCompound());
        } else {
            return new PitItemStack(itemStack);
        }
    }

    private NBTTagCompound getTag() {
        return this.itemStack.getTagCompound();
    }

    public boolean hasPitAttributes() {
        return getTag() != null && getTag().getCompoundTag("ExtraAttributes") != null;
    }

    public NBTTagCompound getPitAttributes() {
        return getTag().getCompoundTag("ExtraAttributes");
    }

    public boolean hasNonce() {
        return hasPitAttributes()
            && getPitAttributes().hasKey("Nonce");
    }

    public int getNonce() {
        return getPitAttributes().getInteger("Nonce");
    }

    public int getUpgradeTier() {
        return getPitAttributes().hasKey("UpgradeTier") ? getPitAttributes().getInteger("UpgradeTier") : -1;
    }

    public int getItemId() {
        return Item.getIdFromItem(itemStack.getItem());
    }

    public boolean isFresh() {
        return hasNonce() && getUpgradeTier() == 0;
    }

    public boolean isPants() {
        return getItemId() == 300;
    }

    public boolean isBow() {
        return getItemId() == 261;
    }

    public boolean isSword() {
        return getItemId() == 283;
    }

    public boolean isMystic() {
        return isPants() || isBow() || isSword();
    }
}
