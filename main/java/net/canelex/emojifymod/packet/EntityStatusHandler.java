package net.canelex.emojifymod.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.canelex.emojifymod.EmojifyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S19PacketEntityStatus;

public class EntityStatusHandler extends ChannelInboundHandlerAdapter
{
	private EmojifyMod mod;
	private Minecraft mc;

	public EntityStatusHandler(EmojifyMod mod)
	{
		this.mod = mod;
		this.mc = Minecraft.getMinecraft();
	}

	@Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		if (msg instanceof S19PacketEntityStatus)
		{
			S19PacketEntityStatus packet = (S19PacketEntityStatus) msg;

			if (packet.getOpCode() == 2) // Entity's state is damaged.
			{
				mod.onEntityDamage(packet.getEntity(mc.theWorld));
			}
		}

		super.channelRead(ctx, msg);
	}
}
