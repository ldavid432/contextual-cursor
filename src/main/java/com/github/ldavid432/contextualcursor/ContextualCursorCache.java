package com.github.ldavid432.contextualcursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.flipImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.loadImage;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.mouseInsideBounds;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.config.CursorBackgroundMode;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.CacheSprite;
import com.github.ldavid432.contextualcursor.sprite.ItemSprite;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
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
	private final ItemManager itemManager;
	private final SpriteManager spriteManager;

	@Inject
	public ContextualCursorCache(ContextualCursorConfig config, PluginManager pluginManager, CustomCursorPlugin customCursorPlugin,
	                             Client client, ItemManager itemManager, SpriteManager spriteManager)
	{
		this(
			client, itemManager, spriteManager,
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
			false
		);
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

	// TODO: Move into separate class?
	private final LoadingCache<Sprite, BufferedImage> spriteCache = CacheBuilder
		.newBuilder()
		.maximumSize(64)
		.build(
			new CacheLoader<>()
			{
				@Nullable
				private BufferedImage getBaseImage(Sprite sprite)
				{
					if (sprite instanceof CacheSprite)
					{
						int id = ((CacheSprite) sprite).getId();
						if (client.getSpriteOverrides().containsKey(id))
						{
							return client.getSpriteOverrides().get(id).toBufferedImage();
						}
						else
						{
							return spriteManager.getSprite(id, 0);
						}
					}
					else if (sprite instanceof ResourceSprite)
					{
						return loadImage(((ResourceSprite) sprite).getFileName());
					}
					else if (sprite instanceof ItemSprite)
					{
						ItemSprite itemSprite = (ItemSprite) sprite;
						return itemManager.getImage(itemSprite.getId(), itemSprite.getQuantity(), false);
					}
					else
					{
						return null;
					}
				}

				private double getScale(Sprite sprite)
				{
					if (sprite instanceof ItemSprite)
					{
						return getItemScale();
					}
					return getCursorScale();
				}

				private boolean isSmoothScalingEnabled(Sprite sprite)
				{
					if (sprite instanceof ItemSprite)
					{
						return isItemSmoothScalingEnabled();
					}
					return isCursorSmoothScalingEnabled();
				}

				@Nullable
				@Override
				public BufferedImage load(@Nonnull Sprite sprite)
				{
					BufferedImage image = getBaseImage(sprite);

					image = scaleImage(image, getScale(sprite), isSmoothScalingEnabled(sprite));

					if (sprite.isInverted())
					{
						image = flipImage(image);
					}
					return image;
				}
			}
		);

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
