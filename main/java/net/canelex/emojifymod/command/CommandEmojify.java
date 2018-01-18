package net.canelex.emojifymod.command;

import net.canelex.emojifymod.Values;
import net.canelex.emojifymod.gui.GuiScreenMain;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CommandEmojify extends CommandBase
{
	private Values values;

	public CommandEmojify(Values values)
	{
		this.values = values;
	}

	@Override public String getCommandName()
	{
		return "emojify";
	}

	@Override public String getCommandUsage(ICommandSender sender)
	{
		return "/emojify";
	}

	@Override public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}

	@Override public void processCommand(ICommandSender sender, String[] args) throws CommandException
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		MinecraftForge.EVENT_BUS.unregister(this);
		Minecraft.getMinecraft().displayGuiScreen(new GuiScreenMain(values));
	}
}
