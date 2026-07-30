package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorState;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
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

	private Tooltip contextualCursorspacerTooltip;
	private Tooltip defaultCursorSpacerTooltip;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks() {
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			updateScale();
		}
	};

	public ContextualCursorState getState()
	{
		if (plugin.isAltPressed())
		{
			if (plugin.isCustomCursorPluginEnabled())
			{
				return ContextualCursorState.externalCursor(cursorProvider.getLastCustomCursor());
			} else {
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

		return ContextualCursorState.contextualCursor(sprite, cursorProvider.getBackgroundCursorSprite(), contextualCursorspacerTooltip);
	}

	// Reset cursor to a default cursor state, one of: no custom cursor, external custom cursor, our custom cursor, or our custom overlay
	public ContextualCursorState defaultCursorState()
	{
		Cursor lastCustomCursor = cursorProvider.getLastCustomCursor();
		if (lastCustomCursor != null)
		{
			return ContextualCursorState.externalCursor(lastCustomCursor);
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
				return ContextualCursorState.genericCursorOverlay(cursorProvider.getDefaultCursorSprite(), defaultCursorSpacerTooltip);
			}
			else
			{
				// Overlays don't render outside the game canvas or when logged out
				return ContextualCursorState.clearCursor();
			}
		}
		else
		{
			return ContextualCursorState.genericCursor(cursorProvider.getDefaultCursor());
		}
	}

	public void updateScale()
	{
		int spacerHeight = (int) ((40 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			contextualCursorspacerTooltip = new ContextualCursorTooltip("contextual-cursor-spacer", spacerHeight);
		}
		else
		{
			contextualCursorspacerTooltip = null;
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

	@ToString(of = "name")
	static class ContextualCursorTooltip extends Tooltip {

		// Just used in toString
		private final String name;

		public ContextualCursorTooltip(String name, int height)
		{
			super(new ImageComponent(new BufferedImage(1, height, BufferedImage.TYPE_INT_ARGB)));
			this.name = name;
		}
	}

}
