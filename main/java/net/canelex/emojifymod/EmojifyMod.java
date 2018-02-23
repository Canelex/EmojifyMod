package net.canelex.emojifymod;

import io.netty.channel.ChannelPipeline;
import net.canelex.emojifymod.command.CommandEmojify;
import net.canelex.emojifymod.packet.EntityStatusHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Mod(modid = "emojifymod", name = "Emojify Mod", version = "1.0")
public class EmojifyMod
{
	// Managers
	private Minecraft mc;
	private TextureManager tm;
	private RenderManager rm;
	private Values values;

	// Combo
	private UUID targetPlayer;
	private int currentCombo;
	private long timeExpireAttack;
	private long timeExpireRender;

	@Mod.EventHandler public void init(FMLInitializationEvent event)
	{
		mc = Minecraft.getMinecraft();
		tm = mc.getTextureManager();
		rm = mc.getRenderManager();

		File imagesDir = new File(mc.mcDataDir, "emojis");

		if (!imagesDir.exists())
		{
			imagesDir.mkdir();
		}

		List defaultPacks = ObfuscationReflectionHelper
				.getPrivateValue(Minecraft.class, mc, "defaultResourcePacks", "field_110449_ao");
		defaultPacks.add(new EmojiLoader(imagesDir)); // Register resource loader (fake resource pack)

		values = new Values(imagesDir);
		values.loadConfig();

		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new CommandEmojify(values));
	}

	@SubscribeEvent public void onPlayerAttack(AttackEntityEvent event)
	{
		if (event.target instanceof EntityPlayer)
		{
			UUID uuid = event.target.getUniqueID();

			if (!uuid.equals(targetPlayer))
			{
				currentCombo = 0; // Player switched targets, reset the combo.
			}

			targetPlayer = uuid;
			timeExpireAttack = System.currentTimeMillis() + 2000; //  Listen for hits in next 2 seconds.
		}
	}

	@SubscribeEvent public void onClientTick(TickEvent.ClientTickEvent event)
	{
		// Inject an adapter to handle S19PacketEntityStatus. Credits to Erouax.

		if (mc.getNetHandler() != null)
		{
			ChannelPipeline pipeline = mc.getNetHandler().getNetworkManager().channel().pipeline();

			if (pipeline.get("entity_status_handler") == null && pipeline.get("packet_handler") != null)
			{
				pipeline.addBefore("packet_handler", "entity_status_handler", new EntityStatusHandler(this));
			}
		}
	}

	@SubscribeEvent public void onRenderPlayer(RenderPlayerEvent.Post event)
	{
		EntityPlayer tPlayer = mc.thePlayer;
		EntityPlayer rPlayer = event.entityPlayer;

		if (values.enabled && values.getCurrent() != null) // Has image to render.
		{
			if (mc.gameSettings.thirdPersonView == 0 && !rPlayer.equals(tPlayer) && !rPlayer.isInvisibleToPlayer(tPlayer)) // Can see player?
			{
				if (values.comboHits == 0 || (rPlayer.getUniqueID() == targetPlayer && System.currentTimeMillis() <= timeExpireRender)) // Should render?
				{
					Emoji currentImage = values.getCurrent();
					double dist = Math.sqrt(event.x * event.x + event.z * event.z);

					if (dist <= 1) return;

					// OpenGL Positioning
					GL11.glPushMatrix();
					double yaw = Math.atan2(event.z, event.x);
					double pitch = Math.atan2(event.y, dist);
					double distance = 0.8D;
					double factorX = -Math.cos(yaw) * Math.cos(pitch);
					double factorZ = -Math.sin(yaw) * Math.cos(pitch);
					double factorY = -Math.sin(pitch);

					GL11.glTranslated(event.x, event.y + rPlayer.getEyeHeight() - 0.2, event.z); // getEyeHeight
					GL11.glTranslated(factorX * distance, factorY * distance, factorZ * distance); // My code
					//GL11.glTranslated(-Math.cos(yaw) * 0.8, -Math.sin(pitch), -Math.sin(yaw) * 0.8);
					GL11.glRotated(-rm.playerViewY, 0, 1, 0);
					GL11.glRotated(rm.playerViewX, 1, 0, 0);
					GL11.glRotated(180, 0, 0, 0); // My code
					GL11.glScaled(-0.02666667D, -0.02666667D, 0.02666667D);
					GL11.glTranslated(0, 9.374999D, 0);
					GL11.glTranslated(-currentImage.offX, -currentImage.offY, 0); // My code

					if (rPlayer.isSneaking())
					{
						GL11.glTranslated(0, -8D, 0);
					}

					// Rendering properties
					GlStateManager.disableLighting();
					GlStateManager.depthMask(true);
					GlStateManager.enableBlend();
					GlStateManager.enableTexture2D();
					GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

					// Tessellate
					ResourceLocation resourceLocation = currentImage.getResourceLocation();
					tm.bindTexture(resourceLocation);
					Tessellator tess = Tessellator.getInstance();
					WorldRenderer wr = tess.getWorldRenderer();
					wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
					int size = currentImage.size;
					wr.pos(-size, -size, 0).tex(1, 1).color(255, 255, 255, 255).endVertex();
					wr.pos(-size, size, 0).tex(1, 0).color(255, 255, 255, 255).endVertex();
					wr.pos(size, size, 0).tex(0, 0).color(255, 255, 255, 255).endVertex();
					wr.pos(size, -size, 0).tex(0, 1).color(255, 255, 255, 255).endVertex();
					tess.draw();

					// Rendering properties
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.popMatrix();
				}
			}
		}
	}

	public void onEntityDamage(Entity entity)
	{
		if (entity == null) 
		{
			return;
		}
		
		if (!entity.equals(mc.thePlayer))
		{
			UUID uuid = entity.getUniqueID();
			long currentTime = System.currentTimeMillis();

			if (uuid == targetPlayer && currentTime < timeExpireAttack) // Damage source was the player.
			{
				currentCombo++;

				if (currentCombo >= values.comboHits)
				{
					timeExpireRender = currentTime + values.comboTime * 1000L; // Render the emoji for X seconds.
				}
			}
		}
		else
		{
			currentCombo = 0; // Player took damage, reset combo.
		}
	}
}
