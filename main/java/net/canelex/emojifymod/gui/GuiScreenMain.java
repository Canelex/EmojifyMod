package net.canelex.emojifymod.gui;

import net.canelex.emojifymod.Values;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiScreenMain extends GuiScreen
{
	private Values values;
	private GuiSlider sliderCombo;
	private GuiSlider sliderTime;
	private int middleX;
	private int middleY;

	public GuiScreenMain(Values values)
	{
		this.values = values;
		values.reloadEmojis();
	}

	@Override public void initGui()
	{
		middleX = width / 2;
		middleY = height / 2;

		buttonList.add(new GuiButton(0, middleX - 55, middleY - 80, 20, 20, "<"));
		buttonList.add(new GuiButton(1, middleX + 35, middleY - 80, 20, 20, ">"));
		buttonList.add(new GuiButton(2, middleX - 55, middleY - 35, 110, 20, getEnabledText()));
		buttonList.add(new GuiButton(3, middleX - 55, middleY - 10, 110, 20, "Size and Position"));
		buttonList.add(sliderCombo = new GuiSlider(4, middleX - 55, middleY + 15, 110, 20, "Combo: ", "", 0, 10, values.comboHits, false, true));
		buttonList.add(sliderTime = new GuiSlider(5, middleX - 55, middleY + 40, 110, 20, "Time: ", "", 1, 10, values.comboTime, false, true));
		buttonList.add(new GuiButton(6, middleX - 55, middleY + 65, 110, 20, "Done"));
	}

	@Override public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();

		// Black background.
		drawRect(middleX - 30, middleY - 100, middleX + 30, middleY - 40, 0x77000000);

		if (values.getCurrent() != null)
		{
			// Emoji Preview.
			GL11.glColor4f(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(values.getCurrent().getResourceLocation());
			drawModalRectWithCustomSizedTexture(middleX - 25, middleY - 95, 0, 0, 50, 50, 50, 50);
		}

		values.comboHits = sliderCombo.getValueInt();
		values.comboTime = sliderTime.getValueInt();

		// Buttons.
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override protected void actionPerformed(GuiButton button) throws IOException
	{
		switch (button.id)
		{
		case 0:
			values.loadImage(-1);
			break;
		case 1:
			values.loadImage(1);
			break;
		case 2:
			values.enabled = !values.enabled;
			button.displayString = getEnabledText();
			break;
		case 3:
			mc.displayGuiScreen(new GuiScreenTransform(values, this));
			break;
		case 6:
			mc.displayGuiScreen(null);
			break;
		}
	}

	@Override public void onGuiClosed()
	{
		values.saveConfig();
	}

	private String getEnabledText()
	{
		if (values.enabled)
		{
			return "Enabled: " + EnumChatFormatting.GREEN + "TRUE";
		}
		else
		{
			return "Enabled: " + EnumChatFormatting.RED + "FALSE";
		}
	}
}
