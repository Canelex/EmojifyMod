package net.canelex.emojifymod.gui;

import net.canelex.emojifymod.Emoji;
import net.canelex.emojifymod.Values;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiScreenTransform extends GuiScreen
{
	private Values values;
	private Emoji emoji;
	private GuiScreen parent;
	private GuiSlider sliderSize;
	private GuiSlider sliderX;
	private GuiSlider sliderY;
	private int middleX;
	private int middleY;

	public GuiScreenTransform(Values values, GuiScreen parent)
	{
		this.values = values;
		this.emoji = values.getCurrent();
		this.parent = parent;
	}

	@Override public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();

		emoji.size = sliderSize.getValueInt();
		emoji.offX = sliderX.getValueInt();
		emoji.offY = sliderY.getValueInt();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override public void initGui()
	{
		middleX = width / 2;
		middleY = height / 2;

		buttonList.add(new GuiButton(0, middleX - 55, middleY - 60, 110, 20, "Reset"));
		buttonList.add(sliderX = new GuiSlider(1, middleX - 55, middleY - 35, 110, 20, "PosX: ", "px", -16, 16, emoji.offX, false, true));
		buttonList.add(sliderY = new GuiSlider(2, middleX - 55, middleY - 10, 110, 20, "PosY: ", "px", -16, 16, emoji.offY, false, true));
		buttonList.add(sliderSize = new GuiSlider(3, middleX - 55, middleY + 15, 110, 20, "Size: ", "px", 0, 32, emoji.size, false, true));
		buttonList.add(new GuiButton(4, middleX - 55, middleY + 40, 110, 20, "Done"));
	}

	@Override protected void actionPerformed(GuiButton button) throws IOException
	{
		switch (button.id)
		{
		case 0:
			emoji.size = 16;
			sliderSize.setValue(16);
			sliderSize.updateSlider();
			emoji.offX = 0;
			sliderX.setValue(0);
			sliderX.updateSlider();
			emoji.offY = 0;
			sliderY.setValue(0);
			sliderY.updateSlider();
			break;
		case 4:
			mc.displayGuiScreen(parent);
			break;
		}
	}

	@Override public void onGuiClosed()
	{
		values.saveConfig();
	}
}
