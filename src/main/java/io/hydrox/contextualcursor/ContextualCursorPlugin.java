/*
 * Copyright (c) 2020-2022 Enriath <ikada@protonmail.ch>
 * Copyright (c) 2026 Lake David <ldavid432@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.hydrox.contextualcursor;

import com.github.ldavid432.contextualcursor.ContextualCursorConfig;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.CURSOR_THEME;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.CUSTOM_CURSOR;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.DEBUG_TOOLTIP;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.DEFAULT_CURSOR_OVERLAY;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.ITEM_SCALE;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.ITEM_SCALE_SMOOTHING;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.PERSIST_ITEMS;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.PERSIST_SPELLS;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.SCALE;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.SCALE_SMOOTHING;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.USE_ITEM_CURSOR;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.handleChangelog;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.mouseInsideBounds;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.cursor.ItemCursor;
import com.github.ldavid432.contextualcursor.cursor.SpellCursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.customcursor.CustomCursorPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Contextual Cursor",
	description = "RSHD-style image cursors",
	tags = {"cursor", "rs3", "rs2", "rshd", "context", "theme", "hover"}
)
@Slf4j
@PluginDependency(CustomCursorPlugin.class)
public class ContextualCursorPlugin extends Plugin implements KeyListener
{
	@Inject
	private ContextualCursorDrawOverlay contextualCursorDrawOverlay;
	@Inject
	private ContextualCursorWorkerOverlay contextualCursorWorkerOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private ContextualCursorConfig config;

	@Inject
	private CustomCursorPlugin customCursorPlugin;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private CursorProvider cursorProvider;

	@Getter
	private boolean altPressed;

	@Getter
	private CursorTheme cursorTheme;

	@Getter
	private boolean isDebugTooltipEnabled;

	@Getter
	@Setter
	@Nullable
	private Sprite spriteToDraw;

	@Getter
	private double cursorScale = 1.0;

	@Getter
	private double itemScale = 1.0;

	@Getter
	private boolean isCursorSmoothScalingEnabled;

	@Getter
	private boolean isItemSmoothScalingEnabled;

	@Getter
	private boolean isCustomCursorPluginEnabled;

	@Getter
	private boolean isCustomDefaultCursorEnabled;

	@Getter
	private boolean isDefaultCursorOverlayEnabled;

	@Getter
	private boolean isPersistSpells;

	@Getter
	private boolean isPersistItems;

	@Getter
	private boolean isShowUseItemCursorEnabled;

	@Getter
	private boolean isLoggedOut = true;

	@Getter
	@Setter
	private boolean isCursorInBounds;

	public boolean canOverrideDefaultCursor()
	{
		return !isCustomCursorPluginEnabled && isCustomDefaultCursorEnabled;
	}

	public boolean canDefaultCursorOverrideWithOverlay()
	{
		return canOverrideDefaultCursor() && isDefaultCursorOverlayEnabled && !isLoggedOut && isCursorInBounds;
	}

	// TODO: Surely we can reduce the number of booleans here??

	@Getter
	private final Map<MenuTarget, Boolean> ignoredTargets = new HashMap<>();

	private final MouseListener mouseListener = new MouseAdapter()
	{
		@Override
		public MouseEvent mouseEntered(MouseEvent mouseEvent)
		{
			altPressed = mouseEvent.isAltDown();
			return mouseEvent;
		}

		@Override
		public MouseEvent mouseExited(MouseEvent mouseEvent)
		{
			altPressed = mouseEvent.isAltDown();
			return mouseEvent;
		}
	};

	protected void startUp()
	{
		List<Cursor> cursors = new ArrayList<>();
		cursors.add(new ItemCursor(client, this));
		cursors.addAll(List.of(ContextualCursor.values()));
		cursors.add(new SpellCursor());
		cursorProvider.setCursors(cursors);

		overlayManager.add(contextualCursorWorkerOverlay);
		overlayManager.add(contextualCursorDrawOverlay);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(mouseListener);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			isLoggedOut = false;
		}

		Point mousePos = client.getMouseCanvasPosition();
		isCursorInBounds = mouseInsideBounds(mousePos, client);

		isCustomDefaultCursorEnabled = config.isCustomDefaultCursorEnabled();
		isCursorSmoothScalingEnabled = config.isCursorSmoothScalingEnabled();
		isItemSmoothScalingEnabled = config.isItemSmoothScalingEnabled();
		cursorTheme = config.getCursorTheme();
		isDefaultCursorOverlayEnabled = config.isDefaultCursorOverlayEnabled();
		updateIgnores();
		updateCursorScale();
		itemScale = (double) config.getItemScale() / 100;
		isPersistItems = config.shouldPersistItems();
		isPersistSpells = config.shouldPersistSpells();
		isShowUseItemCursorEnabled = config.isShowUseItemCursorEnabled();
		isCustomCursorPluginEnabled = pluginManager.isPluginActive(customCursorPlugin);
		contextualCursorWorkerOverlay.resetCursor();

		handleChangelog(config, chatMessageManager, client, isCustomCursorPluginEnabled);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(contextualCursorWorkerOverlay);
		overlayManager.remove(contextualCursorDrawOverlay);
		contextualCursorWorkerOverlay.shutdown();
		clearImages();
		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(mouseListener);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
				isLoggedOut = false;
				if (getSpriteToDraw() == null)
				{
					contextualCursorWorkerOverlay.resetCursor();
				}
				break;
			case LOGIN_SCREEN:
				isLoggedOut = true;
				contextualCursorWorkerOverlay.resetCursor();
				break;
			case LOADING:
				break;
			default:
				contextualCursorWorkerOverlay.resetCursor();
				break;
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		if (config.getHideKeybind().matches(keyEvent))
		{
			// Only trigger on initial press
			boolean resetCursor = !altPressed && keyEvent.isAltDown();
			altPressed = keyEvent.isAltDown();
			if (resetCursor)
			{
				contextualCursorWorkerOverlay.resetCursor();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		if (config.getHideKeybind().matches(keyEvent))
		{
			boolean resetCursor = altPressed && !keyEvent.isAltDown();
			altPressed = keyEvent.isAltDown();
			if (resetCursor)
			{
				contextualCursorWorkerOverlay.resetCursor();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{

	}

	@Provides
	ContextualCursorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ContextualCursorConfig.class);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (Objects.equals(event.getGroup(), ContextualCursorConfig.GROUP))
		{
			if (event.getKey().startsWith("ignore"))
			{
				updateIgnores();
			}
			else if (event.getKey().equals(DEBUG_TOOLTIP))
			{
				isDebugTooltipEnabled = config.isDebugTooltipEnabled();
			}
			else if (event.getKey().equals(SCALE))
			{
				updateCursorScale();
				clearImages();
			}
			else if (event.getKey().equals(SCALE_SMOOTHING))
			{
				isCursorSmoothScalingEnabled = config.isCursorSmoothScalingEnabled();
				clearImages();
			}
			else if (event.getKey().equals(CUSTOM_CURSOR))
			{
				isCustomDefaultCursorEnabled = config.isCustomDefaultCursorEnabled();
				contextualCursorWorkerOverlay.resetCursor();
			}
			else if (event.getKey().equals(CURSOR_THEME))
			{
				cursorTheme = config.getCursorTheme();
				clearImages();
				contextualCursorWorkerOverlay.updateTheme();
			}
			else if (event.getKey().equals(DEFAULT_CURSOR_OVERLAY))
			{
				isDefaultCursorOverlayEnabled = config.isDefaultCursorOverlayEnabled();
				contextualCursorWorkerOverlay.genericOverlayToggled();
			}
			else if (event.getKey().equals(ITEM_SCALE))
			{
				itemScale = (double) config.getItemScale() / 100;
				// TODO: Can potentially only clear item images here
				clearImages();
			}
			else if (event.getKey().equals(ITEM_SCALE_SMOOTHING))
			{
				isItemSmoothScalingEnabled = config.isItemSmoothScalingEnabled();
				clearImages();
			}
			else if (event.getKey().equals(PERSIST_SPELLS))
			{
				isPersistSpells = config.shouldPersistSpells();
			}
			else if (event.getKey().equals(PERSIST_ITEMS))
			{
				isPersistItems = config.shouldPersistItems();
			}
			else if (event.getKey().equals(USE_ITEM_CURSOR))
			{
				isShowUseItemCursorEnabled = config.isShowUseItemCursorEnabled();
			}
		}
		else if ("runelite".equals(event.getGroup()) && "customcursorplugin".equals(event.getKey()))
		{
			isCustomCursorPluginEnabled = Boolean.parseBoolean(event.getNewValue());
			// Delaying this until after CustomCursorPlugin has run its shutdown which clears the cursor
			clientThread.invoke(() -> contextualCursorWorkerOverlay.resetCursor());
		}
	}

	private void updateIgnores()
	{
		for (MenuTarget target : MenuTarget.VALUES)
		{
			ignoredTargets.put(target, target.isIgnored(config));
		}
	}

	private void updateCursorScale()
	{
		cursorScale = (double) config.getCursorScale() / 100;
		contextualCursorWorkerOverlay.updateScale();
		contextualCursorDrawOverlay.updateScale();
	}

	private void clearImages()
	{
		ContextualCursor.clearImages();
		SpellSprite.clearImages();
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		contextualCursorWorkerOverlay.checkLastCursor();
		Point mousePos = client.getMouseCanvasPosition();
		// if previously outside and now inside or vice versa
		boolean inOrOutBoundsChanged = isCursorInBounds == !mouseInsideBounds(mousePos, client);

		isCursorInBounds = mouseInsideBounds(mousePos, client);

		if (inOrOutBoundsChanged)
		{
			contextualCursorWorkerOverlay.resetCursor();
		}
	}
}
