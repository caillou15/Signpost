package gollorum.signpost.minecraft.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import gollorum.signpost.minecraft.rendering.FlippableModel;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModelButton extends ImageButton {

    private final List<GuiModelRenderer> modelRenderers;

    public ModelButton(
        TextureResource background,
        Point point,
        float scale,
        Rect.XAlignment xAlignment,
        Rect.YAlignment yAlignment,
        Function<Rect, Rect> rectBuilder,
        Runnable onPress,
        ModelData... modelData
    ) {
        this(
            background,
            new Rect(point, background.size.scale(scale), xAlignment, yAlignment),
            scale, rectBuilder, b -> onPress.run(), modelData
        );
    }

    private ModelButton(
        TextureResource background,
        Rect rect,
        float scale,
        Function<Rect, Rect> rectBuilder,
        Button.IPressable onPress,
        ModelData... modelData
    ){
        super(
            rect.point.x, rect.point.y,
            rect.width, rect.height,
            0, 0, (int) (background.size.height * scale),
            background.location,
            (int) (background.fileSize.width * scale), (int) (background.fileSize.height * scale),
            onPress
        );
        modelRenderers = new ArrayList<>();
        for(ModelData model: modelData) {
            modelRenderers.add(new GuiModelRenderer(
                rectBuilder.apply(rect),
                model.model,
                model.modelSpaceXOffset,
                model.modelSpaceYOffset,
                model.itemStack
            ));
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        for(GuiModelRenderer model : modelRenderers) {
            model.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public static class ModelData {

        public final FlippableModel model;
        public final float modelSpaceXOffset;
        public final float modelSpaceYOffset;
        public final ItemStack itemStack;

        public ModelData(FlippableModel model, float modelSpaceXOffset, float modelSpaceYOffset, ItemStack itemStack) {
            this.model = model;
            this.modelSpaceXOffset = modelSpaceXOffset;
            this.modelSpaceYOffset = modelSpaceYOffset;
            this.itemStack = itemStack;
        }
    }

}
