package com.github.ldavid432.contextualcursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.mouseInsideBounds;
import com.github.ldavid432.contextualcursor.cache.ImageCache;
import com.github.ldavid432.contextualcursor.config.CursorBackgroundMode;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.PluginManager;

/**
 * Caches config values as well as sprite images
 */
@Setter
@Getter
@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ContextualCursorCache implements ProviderCallbacks
{
	private final Client client;
	private final ContextualCursorConfig config;
	private final PluginManager pluginManager;
	private final ConfigManager configManager;
	private final ItemManager itemManager;
	private final SpriteManager spriteManager;

	public void init()
	{
		cursorScale = (double) config.getCursorScale() / 100;
		itemScale = (double) config.getItemScale() / 100;
		isCursorSmoothScalingEnabled = config.isCursorSmoothScalingEnabled();
		isItemSmoothScalingEnabled = config.isItemSmoothScalingEnabled();
		isCustomDefaultCursorEnabled = config.isCustomDefaultCursorEnabled();
		isDefaultCursorOverlayEnabled = config.isDefaultCursorOverlayEnabled();
		isPersistSpells = config.shouldPersistSpells();
		isPersistItems = config.shouldPersistItems();
		isShowUseItemCursorEnabled = config.isShowUseItemCursorEnabled();
		isDebugTooltipEnabled = config.isDebugTooltipEnabled();
		cursorTheme = config.getCursorTheme();
		cursorBackgroundMode = config.getCursorBackgroundMode();
		isCustomCursorPluginEnabled = configManager.getConfiguration("runelite", "customcursorplugin", Boolean.class) == Boolean.TRUE;
		isLoggedOut = client.getGameState() != GameState.LOGGED_IN;
		isCursorInBounds = mouseInsideBounds(client.getMouseCanvasPosition(), client);
		altPressed = false;
		if (spriteCache == null)
		{
			spriteCache = new ImageCache(this, client, itemManager, spriteManager);
		}
	}

	// config values
	private double cursorScale;
	private double itemScale;
	private boolean isCursorSmoothScalingEnabled;
	private boolean isItemSmoothScalingEnabled;
	private boolean isCustomDefaultCursorEnabled;
	private boolean isDefaultCursorOverlayEnabled;
	private boolean isPersistSpells;
	private boolean isPersistItems;
	private boolean isShowUseItemCursorEnabled;
	private boolean isDebugTooltipEnabled;
	private CursorTheme cursorTheme;
	private CursorBackgroundMode cursorBackgroundMode;

	// non-config values
	private boolean isCustomCursorPluginEnabled;
	private boolean isLoggedOut;
	private boolean isCursorInBounds;
	private boolean altPressed;

	private ImageCache spriteCache;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks()
	{
		@Override
		public void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing)
		{
			clear();
		}

		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			clear();
		}

		@Override
		public void onThemeChange(CursorTheme theme)
		{
			clear();
		}

		@Override
		public void onShutdown()
		{
			clear();
		}
	};

	@Nullable
	public BufferedImage getImage(Sprite sprite)
	{
		try
		{
			return spriteCache.get(sprite);
		}
		catch (ExecutionException e)
		{
			return null;
		}
	}

	public void clear()
	{
		spriteCache.invalidateAll();
	}

	public boolean canOverrideDefaultCursor()
	{
		return !isCustomCursorPluginEnabled && isCustomDefaultCursorEnabled;
	}

	public boolean canDefaultCursorOverrideWithOverlay()
	{
		return canOverrideDefaultCursor() && isDefaultCursorOverlayEnabled && !isLoggedOut && isCursorInBounds;
	}
}
