package vazkii.patchouli.client.book.page;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;

public class PageMultiblock extends PageWithText {

	String name;
	@SerializedName("multiblock_id")
	String multiblockId;
	
	@SerializedName("multiblock")
	SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize")
	boolean showVisualizeButton = true;
	
	private transient AbstractMultiblock multiblockObj;
	private transient ButtonWidget visualizeButton;
	private final transient Random random = new Random();

	@Override
	public void build(BookEntry entry, int pageNum) {
		if(multiblockId != null && !multiblockId.isEmpty()) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(new Identifier(multiblockId));
			
			if(mb instanceof AbstractMultiblock)
				multiblockObj = (AbstractMultiblock) mb;
		}
		
		if(multiblockObj == null && serializedMultiblock != null)
			multiblockObj = serializedMultiblock.toMultiblock();
		
		if(multiblockObj == null)
			throw new IllegalArgumentException("No multiblock located for " + multiblockId);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if(showVisualizeButton)
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97, this::handleButtonVisualize));
	}

	@Override
	public int getTextHeight() {
		return 115;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GlStateManager.enableBlend();
		GlStateManager.color4f(1F, 1F, 1F,1f);
		GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);
		
		parent.drawCenteredStringNoShadow(name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if(multiblockObj != null)
			renderMultiblock();
		
		super.render(mouseX, mouseY, pticks);
	}
	
	public void handleButtonVisualize(ButtonWidget button) {
		String entryKey = parent.getEntry().getResource().toString();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.setMultiblock(multiblockObj, name, bookmark, true);
		parent.addBookmarkButtons();
		
		if(!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	private void renderMultiblock() {
		Vec3i size = multiblockObj.getSize();
		int sizeX = size.getX();
		int sizeY = size.getY();
		int sizeZ = size.getZ();
		float maxX = 90;
		float maxY = 90;
		float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
		float scaleX = maxX / diag;
		float scaleY = maxY / sizeY;
		float scale = -Math.min(scaleX, scaleY);
		
		int xPos = GuiBook.PAGE_WIDTH / 2;
		int yPos = 60;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(xPos, yPos, 100);
		GlStateManager.scalef(scale, scale, scale);
		GlStateManager.translatef(-(float) sizeX / 2, -(float) sizeY / 2, 0);

		// Initial eye pos somewhere off in the distance in the -Z direction
		Vector4f eye = new Vector4f(0, 0, -100, 1);
		Matrix4f rotMat = new Matrix4f();
		rotMat.loadIdentity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		GlStateManager.rotatef(-30F, 1F, 0F, 0F);
		rotMat.rotX((float) Math.toRadians(30F));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if(!Screen.hasShiftDown())
			time += ClientTicker.partialTicks;
		GlStateManager.translatef(-offX, 0, -offZ);
		GlStateManager.rotatef(time, 0F, 1F, 0F);
		rotMat.rotY((float) Math.toRadians(-time));
		GlStateManager.rotatef(45F, 0F, 1F, 0F);
		rotMat.rotY((float) Math.toRadians(-45F));
		GlStateManager.translatef(offX, 0, offZ);
		
		// Finally apply the rotations
		rotMat.transform(eye);
		renderElements(multiblockObj, BlockPos.iterate(BlockPos.ORIGIN, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		GlStateManager.popMatrix();
	}
	
	private void renderElements(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		GlStateManager.pushMatrix();
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GlStateManager.translatef(0, 0, -1);

		BlockEntityRendererDispatcher.staticPlayerX = eye.x;
		BlockEntityRendererDispatcher.staticPlayerY = eye.y;
		BlockEntityRendererDispatcher.staticPlayerZ = eye.z;

		RenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
		for (RenderLayer layer : RenderLayer.getBlockLayers()) {
			if (layer == RenderLayer.getTranslucent()) {
				doBlockEntityRenderPass(mb, blocks, 0);
			}
			doWorldRenderPass(mb, blocks, layer, eye);
			if (layer == RenderLayer.getTranslucent()) {
				doBlockEntityRenderPass(mb, blocks, 1);
			}
		}
		ForgeHooksClient.setRenderLayer(oldRenderLayer);

		setGlStateForPass(0);
		mc.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		GlStateManager.popMatrix();
	}

	private void doWorldRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull RenderLayer layer, Vector4f eye) {
		mc.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		mc.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
		
		ForgeHooksClient.setRenderLayer(layer);
		setGlStateForPass(layer);
		
		BufferBuilder wr = Tessellator.getInstance().getBuffer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		for (BlockPos pos : blocks) {
			BlockState bs = mb.getBlockState(pos);
			Block block = bs.getBlock();
			if (block.canRenderInLayer(bs, layer)) {
				renderBlock(bs, pos, mb, Tessellator.getInstance().getBuffer());
			}
		}

		if (layer == RenderLayer.getTranslucent()) {
			wr.sortVertexData(eye.x, eye.y, eye.z);
		}
		Tessellator.getInstance().draw();
	}

	public void renderBlock(@Nonnull BlockState state, @Nonnull BlockPos pos, @Nonnull AbstractMultiblock mb, @Nonnull BufferBuilder bufferBuilder) {

		try {
			BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
			BlockRenderType type = state.getRenderType();
			if (type != BlockRenderType.MODEL) {
				return;
			}

			// We only want to change one param here, the check sides
			BakedModel bakedmodel = blockRenderManager.getModel(state);
			blockRenderManager.getModelRenderer().render(mb, bakedmodel, state, pos, bufferBuilder, false, random,  state.getRenderingSeed(pos), EmptyModelData.INSTANCE);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<BlockEntity> erroredBEs = Collections.newSetFromMap(new WeakHashMap<>());

	private void doBlockEntityRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final int pass) {
		mb.setWorld(mc.world);
		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		
		setGlStateForPass(1);
		
		for (BlockPos pos : blocks) {
			BlockEntity be = mb.getBlockEntity(pos);
			BlockPos relPos = new BlockPos(mc.player);
			if (be != null && !erroredBEs.contains(be)) {
				be.setWorld(mc.world.getWorld(), pos);
				be.setPos(relPos.add(pos));

				try {
					BlockEntityRenderDispatcher.INSTANCE.render(be, pos.getX(), pos.getY(), pos.getZ(), ClientTicker.partialTicks);
				} catch (Exception e) {
					erroredBEs.add(be);
					Patchouli.LOGGER.error("An exception occured rendering block entity", e);
				}
			}
		}
		
		RenderHelper.disableStandardItemLighting();
	}

	private void setGlStateForPass(@Nonnull RenderLayer layer) {
		int pass = layer == RenderLayer.getTranslucent() ? 1 : 0;
		setGlStateForPass(pass);
	}

	private void setGlStateForPass(int layer) {
		GlStateManager.color4f(1, 1, 1,1);

		if (layer == 0) {
			GlStateManager.enableDepthTest();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		} else {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
		}
	}
}
