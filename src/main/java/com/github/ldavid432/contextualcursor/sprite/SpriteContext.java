package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;

@Singleton
public class SpriteContext
{
	@Inject
	@Getter
	private Client client;

	@Inject
	@Getter
	private SpriteManager spriteManager;

	@Inject
	@Getter
	private ItemManager itemManager;

	@Inject
	private ContextualCursorPlugin plugin;

	public CursorTheme getTheme()
	{
		return plugin.getCursorTheme();
	}

	public double getCursorScale()
	{
		return plugin.getCursorScale();
	}

	public double getItemScale()
	{
		return plugin.getItemScale();
	}

	public boolean isSmoothScalingEnabled()
	{
		return plugin.isCursorSmoothScalingEnabled();
	}
}
