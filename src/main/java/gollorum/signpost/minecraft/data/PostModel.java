package gollorum.signpost.minecraft.data;

import gollorum.signpost.Signpost;
import gollorum.signpost.minecraft.block.Post;
import gollorum.signpost.minecraft.block.Waystone;
import gollorum.signpost.utils.math.geometry.Vector3;
import gollorum.signpost.utils.modelGeneration.FaceRotation;
import gollorum.signpost.utils.modelGeneration.SignModelFactory;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PostModel extends BlockModelProvider {

    private static final String texturePost = "post";
    public static final String textureSign = "texture";
    public static final String secondaryTexture = "secondary_texture";

    private static final ResourceLocation previewLocation = new ResourceLocation(Signpost.MOD_ID, "post_preview");
    private static final ResourceLocation wideLocation = new ResourceLocation(Signpost.MOD_ID, "small_wide_sign");
    private static final ResourceLocation shortLocation = new ResourceLocation(Signpost.MOD_ID, "small_short_sign");
    private static final ResourceLocation largeLocation = new ResourceLocation(Signpost.MOD_ID, "large_sign");
    private static final ResourceLocation wideOverlayLocation = new ResourceLocation(Signpost.MOD_ID, "small_wide_sign_overlay");
    private static final ResourceLocation shortOverlayLocation = new ResourceLocation(Signpost.MOD_ID, "small_short_sign_overlay");
    private static final ResourceLocation largeOverlayLocation = new ResourceLocation(Signpost.MOD_ID, "large_sign_overlay");

    public final Map<Post.Info, BlockModelBuilder> allModels;
    public final BlockModelBuilder waystoneModel;

    private static final ModelBuilder.FaceRotation mainTextureRotation = ModelBuilder.FaceRotation.CLOCKWISE_90;
    private static final ModelBuilder.FaceRotation secondaryTextureRotation = ModelBuilder.FaceRotation.CLOCKWISE_90;

    private final BlockModelBuilder previewModel;

    private static final int textureCenterY = 8;

    public PostModel(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(generator, Signpost.MOD_ID, fileHelper);
        previewModel = new BlockModelBuilder(new ResourceLocation(Signpost.MOD_ID, "block/" + previewLocation.getPath()), fileHelper);
        allModels = Arrays.stream(Post.All_INFOS).collect(Collectors.<Post.Info, Post.Info, BlockModelBuilder>toMap(
            i -> i,
            i -> new BlockModelBuilder(new ResourceLocation(Signpost.MOD_ID, "block/" + i.registryName), fileHelper)
        ));
        waystoneModel = new BlockModelBuilder(new ResourceLocation(Signpost.MOD_ID, "block/" + Waystone.REGISTRY_NAME), fileHelper);
        generator.addProvider(new Item(generator, fileHelper));
    }

    private class Item extends ItemModelProvider {

        public Item(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, Signpost.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            for (Map.Entry<Post.Info, BlockModelBuilder> entry : allModels.entrySet()) {
                getBuilder(entry.getKey().registryName).parent(entry.getValue());
            }
            getBuilder(Waystone.REGISTRY_NAME).parent(waystoneModel);
        }
    }

    @Override
    protected void registerModels() {
        BlockModelBuilder previewBuilder = getBuilder(previewLocation.toString())
            .parent(new ModelFile.ExistingModelFile(new ResourceLocation("block/block"), existingFileHelper))
            .transforms()
                .transform(ModelBuilder.Perspective.GUI)
                    .rotation(30, 315, 0)
                    .scale(0.625f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
                    .rotation(0, 315, 0)
                    .scale(0.4f)
                .end()
                .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT)
                    .rotation(0, 315, 0)
                    .scale(0.4f)
                .end()
            .end();
        makePostAt(new Vector3(8, 8, 8), previewBuilder);
        new SignModelFactory<String>().makeWideSign(new Vector3(8, 12, 8), "#" + textureSign, "#" + secondaryTexture)
            .build(previewBuilder, SignModelFactory.Builder.BlockModel);

        makePostAt(new Vector3(0, 8, 0), getBuilder("post_only"))
            .texture(texturePost, Post.ModelType.Oak.postTexture);
        new SignModelFactory<String>().makeWideSign("#" + textureSign, "#" + secondaryTexture)
            .build(getBuilder(wideLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.ModelType.Oak.mainTexture)
            .texture(secondaryTexture, Post.ModelType.Oak.secondaryTexture);
        new SignModelFactory<String>().makeShortSign("#" + textureSign, "#" + secondaryTexture)
            .build(getBuilder(shortLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.ModelType.Oak.mainTexture)
            .texture(secondaryTexture, Post.ModelType.Oak.secondaryTexture);
        new SignModelFactory<String>().makeLargeSign("#" + textureSign, "#" + secondaryTexture)
            .build(getBuilder(largeLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.ModelType.Oak.mainTexture)
            .texture(secondaryTexture, Post.ModelType.Oak.secondaryTexture);

        new SignModelFactory<String>().makeWideSignOverlay("#" + textureSign).build(getBuilder(wideOverlayLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.OverlayTextures.Wide.Gras);
        new SignModelFactory<String>().makeShortSignOverlay("#" + textureSign).build(getBuilder(shortOverlayLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.OverlayTextures.Short.Gras);
        new SignModelFactory<String>().makeLargeSignOverlay("#" + textureSign).build(getBuilder(largeOverlayLocation.toString()), SignModelFactory.Builder.BlockModel)
            .texture(textureSign, Post.OverlayTextures.Large.Gras);

        for(Post.Info info : Post.All_INFOS) {
            getBuilder(info.registryName)
                .parent(previewModel)
                .texture("particle", info.type.postTexture)
                .texture(texturePost, info.type.postTexture)
                .texture(textureSign, info.type.mainTexture)
                .texture(secondaryTexture, info.type.secondaryTexture);
        }
        cubeAll(Waystone.REGISTRY_NAME, new ResourceLocation(Signpost.MOD_ID, "block/waystone"));
    }

    private static BlockModelBuilder makePostAt(Vector3 center, BlockModelBuilder builder) {
        builder
            .element()
                .from(center.x - 2, center.y - 8, center.z - 2)
                .to(center.x + 2, center.y + 8, center.z + 2)
            .face(Direction.SOUTH)
                .texture("#" + texturePost)
                .uvs(0, 0, 4, 16)
            .end()
            .face(Direction.EAST)
                .texture("#" + texturePost)
                .uvs(4, 0, 8, 16)
            .end()
            .face(Direction.NORTH)
                .texture("#" + texturePost)
                .uvs(8, 0, 12, 16)
            .end()
            .face(Direction.WEST)
                .texture("#" + texturePost)
                .uvs(12, 0, 16, 16)
            .end()
            .face(Direction.DOWN)
                .texture("#" + texturePost)
                .uvs(0, 4, 4, 0)
            .end()
            .face(Direction.UP)
                .texture("#" + texturePost)
                .uvs(0, 16, 4, 12)
            .end();
        return builder;
    }

}