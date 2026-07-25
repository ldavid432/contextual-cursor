package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.github.ldavid432.contextualcursor.state.ContextualCursorState;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class StateProvider
{
	private final CursorProvider cursorProvider;
	private final MenuEntryProvider menuEntryProvider;
	private final SpriteProvider spriteProvider;
	private final ContextualCursorPlugin plugin;
	private final SelectedItemProvider selectedItemProvider;

	private Tooltip contextualCursorspacerTooltip;
	private Tooltip defaultCursorSpacerTooltip;

	@Getter
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks() {
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			updateScale();
		}
	};

	public ContextualCursorState getState(@Nullable ContextualCursorState previousState)
	{
		// Selection takes precedence over menu entries
		Sprite sprite = selectedItemProvider.getSelectedSprite();

		if (sprite == null)
		{
			MenuEntry menuEntry = null;
			if (!plugin.isAltPressed() && plugin.isCursorInBounds())
			{
				menuEntry = menuEntryProvider.getMenuEntry();
			}

			if (menuEntry == null)
			{
				// TODO: Confirm these checks
				if ((previousState != null && previousState.getCursorForeground() != null) ||
					plugin.canDefaultCursorOverrideWithOverlay() || !plugin.isCursorInBounds() || plugin.isLoggedOut())
				{
					return defaultCursorState(previousState);
				}
				return null;
			}

			sprite = spriteProvider.getSprite(menuEntry, menuEntryProvider.getLastSubmenuEntry(), menuEntryProvider.isInSubmenu());
		}

		return ContextualCursorState.contextualCursor(sprite, cursorProvider.getBackgroundCursorSprite(), contextualCursorspacerTooltip);
	}

	// Reset cursor to a default cursor state, one of: no custom cursor, external custom cursor, our custom cursor, or our custom overlay
	public ContextualCursorState defaultCursorState(@Nullable ContextualCursorState previousState)
	{
		if (previousState != null && previousState.getCursorForeground() != null)
		{
			Cursor savedCursor = cursorProvider.getLastExternalCursor();
			if (savedCursor != null)
			{
				return ContextualCursorState.externalCursor(savedCursor);
			}
		}

		if (plugin.canOverrideDefaultCursor())
		{
			return pluginDefaultCursorState();
		}
		else if (!plugin.isCustomCursorPluginEnabled())
		{
			return ContextualCursorState.clearCursor();
		}
		else
		{
			// custom cursor plugin is ON - restore the custom cursor
			Cursor savedCursor = cursorProvider.getLastExternalCursor();
			if (savedCursor != null)
			{
				return ContextualCursorState.externalCursor(savedCursor);
			}

			if (cursorProvider.getLastDefaultCursor() != null)
			{
				return ContextualCursorState.genericCursor(cursorProvider.getLastDefaultCursor());
			}
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
