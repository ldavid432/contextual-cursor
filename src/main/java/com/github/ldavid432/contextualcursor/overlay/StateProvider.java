package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorState;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.isExternalCustomCursor;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.cursor.OffsetCursor;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.github.ldavid432.contextualcursor.sprite.SpriteContext;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.GENERIC_CURSOR_NAME;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StateProvider implements ProviderCallbacks
{
	private final CursorProvider cursorProvider;
	private final MenuEntryProvider menuEntryProvider;
	private final SpriteProvider spriteProvider;
	private final ContextualCursorPlugin plugin;
	private final SelectedItemProvider selectedItemProvider;
	private final ClientUI clientUI;
	private final SpriteContext spriteContext;

	private Tooltip contextualCursorSpacerTooltip;
	private Tooltip defaultCursorSpacerTooltip;
	private Cursor defaultCursor = null;
	@Setter
	private Cursor lastExternalCustomCursor = null;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks()
	{
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			updateScale();
			defaultCursor = null;
		}

		@Override
		public void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing)
		{
			defaultCursor = null;
		}

		@Override
		public void onThemeChange(CursorTheme theme)
		{
			defaultCursor = null;
		}

		@Override
		public void onShutdown()
		{
			defaultCursor = null;
		}
	};

	public ContextualCursorState getState()
	{
		Cursor currentCursor = clientUI.getCurrentCursor();
		if (isExternalCustomCursor(currentCursor) && lastExternalCustomCursor != currentCursor)
		{
			lastExternalCustomCursor = currentCursor;
		}

		// Cursors set by external plugin
		if (currentCursor.getType() != Cursor.DEFAULT_CURSOR && currentCursor.getType() != Cursor.CUSTOM_CURSOR)
		{
			return ContextualCursorState.externalCursor(currentCursor);
		}

		if (plugin.isAltPressed())
		{
			if (plugin.isCustomCursorPluginEnabled())
			{
				return ContextualCursorState.externalCursor(lastExternalCustomCursor);
			}
			else
			{
				return ContextualCursorState.clearCursor();
			}
		}

		// Selection takes precedence over menu entries
		Sprite sprite = selectedItemProvider.getSelectedSprite();

		if (sprite == null)
		{
			MenuEntry menuEntry = null;
			if (plugin.isCursorInBounds())
			{
				menuEntry = menuEntryProvider.getMenuEntry();
			}

			if (menuEntry == null)
			{
				return defaultCursorState();
			}

			sprite = spriteProvider.getSprite(menuEntry, menuEntryProvider.getLastSubmenuEntry(), menuEntryProvider.isInSubmenu());
		}

		return contextualCursor(sprite);
	}

	// Reset cursor to a default cursor state, one of: no custom cursor, external custom cursor, our custom cursor, or our custom overlay
	public ContextualCursorState defaultCursorState()
	{
		if (lastExternalCustomCursor != null)
		{
			return ContextualCursorState.externalCursor(lastExternalCustomCursor);
		}
		else if (plugin.canOverrideDefaultCursor())
		{
			return pluginDefaultCursorState();
		}
		else if (!plugin.isCustomCursorPluginEnabled())
		{
			return ContextualCursorState.clearCursor();
		}

		return null;
	}

	private ContextualCursorState pluginDefaultCursorState()
	{
		if (plugin.isDefaultCursorOverlayEnabled())
		{
			if (!plugin.isLoggedOut() && plugin.isCursorInBounds())
			{
				return ContextualCursorState.genericCursorOverlay(cursorProvider.getDefaultCursor(), defaultCursorSpacerTooltip);
			}
			else
			{
				// Overlays don't render outside the game canvas or when logged out
				return ContextualCursorState.clearCursor();
			}
		}
		else
		{
			if (defaultCursor == null)
			{
				defaultCursor = createGenericCursor();
			}
			return ContextualCursorState.genericCursor(defaultCursor);
		}
	}

	private ContextualCursorState contextualCursor(Sprite sprite)
	{
		// TODO: Cache the scaled point?
		OffsetCursor contextualCursor = new OffsetCursor(sprite, cursorProvider.getForegroundCursorCenter());
		contextualCursor.updateScale(plugin.getCursorScale());

		if (plugin.isCursorBackgroundHidden())
		{
			// Merge default state + contextual state
			ContextualCursorState defaultState = defaultCursorState();
			if (plugin.canDefaultCursorOverrideWithOverlay())
			{
				return ContextualCursorState.contextualCursor(
					contextualCursor,
					defaultState.getCursorBackground(),
					// TODO: Maybe this spacer should be smaller
					contextualCursorSpacerTooltip
				);
			}
			else
			{
				return ContextualCursorState.contextualCursorHybrid(
					contextualCursor,
					defaultState.getCursor()
				);
			}
		}
		else
		{
			return ContextualCursorState.contextualCursor(
				contextualCursor,
				cursorProvider.getBackgroundCursor(),
				contextualCursorSpacerTooltip
			);
		}
	}

	public void updateScale()
	{
		int spacerHeight = (int) ((40 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			contextualCursorSpacerTooltip = new ContextualCursorTooltip("contextual-cursor-spacer", spacerHeight);
		}
		else
		{
			contextualCursorSpacerTooltip = null;
		}

		spacerHeight = (int) ((25 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			defaultCursorSpacerTooltip = new ContextualCursorTooltip("default-cursor-spacer", spacerHeight);
		}
		else
		{
			defaultCursorSpacerTooltip = null;
		}
	}

	private Cursor createGenericCursor()
	{
		BufferedImage icon = cursorProvider.getDefaultCursor().getSprite().getImage(spriteContext);
		BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(icon, 0, 0, null);
		g.dispose();

		return Toolkit.getDefaultToolkit().createCustomCursor(
			result,
			new java.awt.Point(0, 0),
			GENERIC_CURSOR_NAME
		);
	}

	@ToString(of = "name")
	static class ContextualCursorTooltip extends Tooltip
	{

		// Just used in toString
		private final String name;

		public ContextualCursorTooltip(String name, int height)
		{
			super(new ImageComponent(new BufferedImage(1, height, BufferedImage.TYPE_INT_ARGB)));
			this.name = name;
		}
	}

}
