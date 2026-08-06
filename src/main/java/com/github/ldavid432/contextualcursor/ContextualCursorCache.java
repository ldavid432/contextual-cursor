package com.github.ldavid432.contextualcursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.mouseInsideBounds;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.customcursor.CustomCursorPlugin;

@Setter
@Getter
@Singleton
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextualCursorCache
{
	@Inject
	public ContextualCursorCache(ContextualCursorConfig config, PluginManager pluginManager, CustomCursorPlugin customCursorPlugin,
	                             Client client)
	{
		this(
			(double) config.getCursorScale() / 100,
			(double) config.getItemScale() / 100,
			config.isCursorSmoothScalingEnabled(),
			config.isItemSmoothScalingEnabled(),
			config.isCustomDefaultCursorEnabled(),
			config.isDefaultCursorOverlayEnabled(),
			config.shouldPersistSpells(),
			config.shouldPersistItems(),
			config.isShowUseItemCursorEnabled(),
			config.isOverlayV2(),
			config.isCursorBackgroundHidden(),
			config.isDebugTooltipEnabled(),
			config.getCursorTheme(),
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
	private boolean isOverlayV2;
	private boolean isCursorBackgroundHidden;
	private boolean isDebugTooltipEnabled;
	private CursorTheme cursorTheme;

	// non-config values
	private boolean isCustomCursorPluginEnabled;
	private boolean isLoggedOut;
	private boolean isCursorInBounds;
	private boolean altPressed;

	public boolean canOverrideDefaultCursor()
	{
		return !isCustomCursorPluginEnabled && isCustomDefaultCursorEnabled;
	}

	public boolean canDefaultCursorOverrideWithOverlay()
	{
		return canOverrideDefaultCursor() && isDefaultCursorOverlayEnabled && !isLoggedOut && isCursorInBounds;
	}
}
