package net.canelex.emojifymod;

import net.minecraft.util.ResourceLocation;

public class Emoji
{
	public String name;
	public boolean internal;
	public int offX;
	public int offY;
	public int size;

	public Emoji(String name, boolean internal)
	{
		this.name = name;
		this.internal = internal;
		this.offX = 0;
		this.offY = 0;
		this.size = 16;
	}

	public ResourceLocation getResourceLocation()
	{
		return new ResourceLocation(internal ? "emojifymod" : "emojis", name);
	}

	@Override public String toString()
	{
		return offX + "," + offY + "," + size;
	}
}
