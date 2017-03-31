package gollorum.signpost.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gollorum.signpost.blocks.PostPostTile;
import gollorum.signpost.util.DoubleBaseInfo;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelPost extends ModelBase {
	public Board board1;
	public Board board2;
	public ModelRenderer post;
	private static final String __OBFID = "CL_00000854";

	public ModelPost() {

		textureWidth = 24;
		textureHeight = 22;

		board1 = new Board(this, 8);
		board2 = new Board(this, 0);

		post = new ModelRenderer(this, 0, 0);
		post.addBox(-2F, 0F, -2F, 4, 16, 4, 0.0F);
	}

	public void render(PostRenderer postRenderer, float f1, float f5, DoubleBaseInfo tilebases, PostPostTile tile, double rotation1, double rotation2) {
		super.render(null, 0, f1, 0, 0, 0, f5);
		post.render(f5);
		if ((tilebases.base1 != null&&!tilebases.base1.name.equals("null")&&!tilebases.base1.name.equals("")) || tile.isItem) {
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 1);
			GL11.glTranslated(0, -1.5, 0);
			GL11.glRotated(-Math.toDegrees(rotation1), 0, 1, 0);
			board1.render(f5, tilebases.flip1);
			GL11.glPopMatrix();
		}
		if ((tilebases.base2 != null&&!tilebases.base2.name.equals("null")&&!tilebases.base2.name.equals("")) || tile.isItem) {
			GL11.glPushMatrix();
			GL11.glRotated(180, 0, 0, 1);
			GL11.glTranslated(0, -0.5, 0);
			GL11.glRotated(-Math.toDegrees(rotation2), 0, 1, 0);
			board2.render(f5, tilebases.flip2);
			GL11.glPopMatrix();
		}
	}

	public void renderOverlay1(DoubleBaseInfo tilebases, float f5, double rotation) {
		GL11.glPushMatrix();
		GL11.glRotated(Math.toDegrees(rotation), 0, 1, 0);
		GL11.glTranslated(0, 0.75, 2.5/16.0);
		GL11.glScaled(1.01, 1.01, 1.1);
		GL11.glTranslated(0, -0.75, -2.5/16.0);
		GL11.glRotated(180, 0, 0, 1);
		GL11.glTranslated(0, -1.5, 0);
		board1.render(f5, tilebases.flip1);
		GL11.glPopMatrix();
	}
	
	public void renderOverlay2(DoubleBaseInfo tilebases, float f5, double rotation) {
		GL11.glPushMatrix();
		GL11.glRotated(Math.toDegrees(rotation), 0, 1, 0);
		GL11.glTranslated(0, 0.25, 2.5/16.0);
		GL11.glScaled(1.01, 1.01, 1.1);
		GL11.glTranslated(0, -0.25, -2.5/16.0);
		GL11.glRotated(180, 0, 0, 1);
		GL11.glTranslated(0, -0.5, 0);
		board2.render(f5, tilebases.flip2);
		GL11.glPopMatrix();
	}

}