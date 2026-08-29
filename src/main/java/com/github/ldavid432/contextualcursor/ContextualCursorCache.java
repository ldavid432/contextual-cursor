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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.customcursor.CustomCursorPlugin;

/**
 * Caches config values as well as sprite images
 */
@Setter
@Getter
@Singleton
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextualCursorCache implements ProviderCallbacks
{
	private final Client client;

	@Inject
	public ContextualCursorCache(ContextualCursorConfig config, PluginManager pluginManager, CustomCursorPlugin customCursorPlugin,
	                             Client client, ItemManager itemManager, SpriteManager spriteManager)
	{
		this(
			client,
			(double) config.getCursorScale() / 100,
			(double) config.getItemScale() / 100,
			config.isCursorSmoothScalingEnabled(),
			config.isItemSmoothScalingEnabled(),
			config.isCustomDefaultCursorEnabled(),
			config.isDefaultCursorOverlayEnabled(),
			config.shouldPersistSpells(),
			config.shouldPersistItems(),
			config.isShowUseItemCursorEnabled(),
			config.isDebugTooltipEnabled(),
			config.getCursorTheme(),
			config.getCursorBackgroundMode(),
			pluginManager.isPluginActive(customCursorPlugin),
			client.getGameState() != GameState.LOGGED_IN,
			mouseInsideBounds(client.getMouseCanvasPosition(), client),
			false,
			null
		);
		spriteCache = new ImageCache(this, client, itemManager, spriteManager);
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
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks() {
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
