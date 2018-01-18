package net.canelex.emojifymod;

import com.google.common.collect.Maps;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Map;

public class Values
{
	private File emojiDir;
	private Configuration config;
	private Map<String, Emoji> emojis;
	private String current;

	public boolean enabled;
	public int comboHits;
	public int comboTime;

	public Values(File emojiDir)
	{
		this.emojiDir = emojiDir;
		this.config = new Configuration(new File(emojiDir, "config.cfg"));
		this.emojis = Maps.newLinkedHashMap();
		reloadEmojis();
	}

	public void loadImage(int slots)
	{
		if (emojis.size() == 0) return;

		String[] keys = emojis.keySet().toArray(new String[emojis.size()]);

		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i].equals(current))
			{
				int index = (i + slots) % keys.length;

				if (index < 0)
				{
					index += keys.length;
				}

				current = keys[index];
				break;
			}
		}
		}

	public void reloadEmojis()
	{
		emojis.clear();
		emojis.put("default1.png", new Emoji("default1.png", true));
		emojis.put("default2.png", new Emoji("default2.png", true));
		emojis.put("default3.png", new Emoji("default3.png", true));

		if (!emojiDir.exists())
		{
			emojiDir.mkdir(); // Create directory if it doesn't exist.
		}

		for (String file : emojiDir.list())
		{
			if (file.endsWith(".png") || file.endsWith(".jpg"))
			{
				emojis.put(file, new Emoji(file, false));
			}
		}

		loadConfig();

		if (!emojis.containsKey(current))
		{
			current = "default1.png";
		}
	}

	public Emoji getCurrent()
	{
		if (current == null)
		{
			return null;
		}

		return emojis.get(current);
	}

	public void loadConfig()
	{
		config.load();
		updateConfig(true);
	}

	public void saveConfig()
	{
		updateConfig(false);
		config.save();
	}

	private void updateConfig(boolean load)
	{
		Property prop;

		prop = config.get("General", "enabled", true);
		if (load) enabled = prop.getBoolean(); else prop.set(enabled);

		prop = config.get("General", "current", "default1.png");
		if (load) current = prop.getString(); else prop.set(current);

		prop = config.get("General", "comboHits", 0);
		if (load) comboHits = prop.getInt(); else prop.set(comboHits);

		prop = config.get("General", "comboTime", 5);
		if (load) comboTime = prop.getInt(); else prop.set(comboTime);

		for (Emoji emoji : emojis.values())
		{
			prop = config.get("Emoji", emoji.name, emoji.toString());

			if (load)
			{
				try
				{
					String[] args = prop.getString().split(",");
					emoji.offX = Integer.parseInt(args[0]);
					emoji.offY = Integer.parseInt(args[1]);
					emoji.size = Integer.parseInt(args[2]);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				prop.set(emoji.toString());
			}
		}
	}
}
