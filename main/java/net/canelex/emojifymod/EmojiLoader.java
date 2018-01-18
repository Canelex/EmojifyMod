package net.canelex.emojifymod;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public class EmojiLoader implements IResourcePack
{
	private File emojiDir;

	public EmojiLoader(File emojiDir)
	{
		this.emojiDir = emojiDir;
	}

	@Override public InputStream getInputStream(ResourceLocation location) throws IOException
	{
		File emojiFile = new File(emojiDir, location.getResourcePath());
		return new FileInputStream(emojiFile);
	}

	@Override public boolean resourceExists(ResourceLocation location)
	{
		try
		{
			return getInputStream(location) != null;
		}
		catch (IOException ex)
		{
			return false;
		}
	}

	@Override public Set<String> getResourceDomains()
	{
		return Collections.singleton("emojis");
	}

	@Override public String getPackName()
	{
		return "emojify";
	}

	@Override public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer p1, String p2) throws IOException
	{
		return null;
	}

	@Override public BufferedImage getPackImage() throws IOException
	{
		return null;
	}
}
