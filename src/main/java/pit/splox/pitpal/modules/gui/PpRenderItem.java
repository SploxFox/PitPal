package pit.splox.pitpal.modules.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import pit.splox.pitpal.Colors;
import pit.splox.pitpal.PitPal;

import static pit.splox.pitpal.PitPal.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.lwjgl.opengl.GL11;

/**
 * PitPal RenderItem class to draw the word "fresh" on fresh pants/mystics
 */
public class PpRenderItem extends RenderItem {
    public PpRenderItem(TextureManager textureManager, ModelManager modelManager) {
        super(textureManager, modelManager);
    }
    
    // Alternatively could move this to renderItem to always show the extra stuff
    @Override
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        super.renderItemIntoGUI(stack, x, y);

        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            return;
        }

        NBTTagCompound pitAttributes = tag.getCompoundTag("ExtraAttributes");

        if (pitAttributes != null && pitAttributes.hasKey("UpgradeTier")) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, 301);

            int size = 2;
            int padding = 1;
            int margin = 1;
            int outerSize = size + 2 * padding;
            int blockSize = margin + outerSize - 1;

            y += 16 - outerSize - margin + 1;
            x += margin;

            int length = 1;
            int color = 0xffffffff;

            int upgradeTier = pitAttributes.getInteger("UpgradeTier");
            if (upgradeTier > 0) {
                length = upgradeTier;
                color = 0xff4287f5;
            } else {
                color = 0xff1bf22d;
            }
            
            for (int i = 0; i < length; i++) {
                int dx = x + blockSize * i;
                Gui.drawRect(dx, y, dx + size + 1, y + size + 1, Colors.getShadow(color));
                Gui.drawRect(dx + 1, y + 1, dx + size, y + size, color);

            }
            GlStateManager.enableBlend();
            
            GlStateManager.translate(0, 0, -301);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Uses reflection to insert our own item renderer.
     * Used for drawing on top of items w/o messing with
     * resource packs, textures or models.
     * 
     * This also gives us easy access to the item's NBT
     * data and allows us to easily implement other things
     * in the future, for example, different textures
     * depending on enchant. All of this comes at the cost
     * of compatibility with other mods.
     * 
     * Someone more well-versed in vanilla item rendering than
     * me is welcome to fix this up
     * 
     * @return instance of the PpRenderItem used
     */
    public static PpRenderItem init() {
        try {
            
            ItemRenderer itemRenderer = mc.getItemRenderer();
            Class<?> mcClass = mc.getClass();

            Field modelManagerField = ReflectionHelper.findField(mcClass, "field_175617_aL", "modelManager");
            modelManagerField.setAccessible(true);
            ModelManager modelManager = (ModelManager) modelManagerField.get(mc);
            
            // Field renderManagerField = ReflectionHelper.findField(mcClass, "field_175616_W", "renderManager");
            // renderManagerField.setAccessible(true);

            Field itemRendererField = ReflectionHelper.findField(mcClass, "field_175620_Y", "itemRenderer");
            itemRendererField.setAccessible(true);
            
            Field renderItemField = ReflectionHelper.findField(mcClass, "field_175621_X", "renderItem");
            renderItemField.setAccessible(true);

            PpRenderItem renderItem = new PpRenderItem(mc.renderEngine, modelManager);
            RenderManager renderManager = new RenderManager(mc.renderEngine, renderItem);
            renderItemField.set(mc, renderItem);
            //renderManagerField.set(mc, renderManager);
            itemRendererField.set(mc, new ItemRenderer(mc));
            ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(renderItem);

            return renderItem;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("idk", "rip");
            return null;
        }
    }
}
