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

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.ContextualCursorConfig;
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.CURSOR_BACKGROUND_MODE;
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
import com.github.ldavid432.contextualcursor.ContextualCursorState;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.buildGson;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.handleChangelog;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.loadLocalCursorDefinition;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.mouseInsideBounds;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.github.ldavid432.contextualcursor.overlay.ContextualCursorV2DrawOverlay;
import com.github.ldavid432.contextualcursor.overlay.ContextualCursorV2WorkerOverlay;
import com.github.ldavid432.contextualcursor.overlay.StateProvider;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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
	private ContextualCursorV2DrawOverlay drawOverlayV2;
	@Inject
	private ContextualCursorV2WorkerOverlay workerOverlayV2;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private ContextualCursorConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private Client client;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private CursorProvider cursorProvider;

	@Inject
	private StateProvider stateProvider;

	@Getter
	@Setter
	@Nullable
	private ContextualCursorState previousState;
	@Getter
	@Setter
	@Nullable
	private ContextualCursorState currentState;
	@Getter
	@Setter
	@Nullable
	private ContextualCursorState nextState;

	@Inject
	private Gson runeliteGson;

	private Gson contextualCursorGson;

	@Inject
	private ContextualCursorCache cache;

	private final List<ProviderCallbacks> callbacks = new ArrayList<>();

	@Getter
	private final Map<MenuTarget, Boolean> ignoredTargets = new HashMap<>();

	private final MouseListener mouseListener = new MouseAdapter()
	{
		@Override
		public MouseEvent mouseEntered(MouseEvent mouseEvent)
		{
			cache.setAltPressed(mouseEvent.isAltDown());
			return mouseEvent;
		}

		@Override
		public MouseEvent mouseExited(MouseEvent mouseEvent)
		{
			cache.setAltPressed(mouseEvent.isAltDown());
			return mouseEvent;
		}
	};

	protected void startUp()
	{
		contextualCursorGson = buildGson(runeliteGson);

		updateCursorDefinition();

		currentState = stateProvider.defaultCursorState();

		overlayManager.add(workerOverlayV2);
		overlayManager.add(drawOverlayV2);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(mouseListener);

		// Provide initial values to all subscribers
		callbacks.add(cursorProvider);
		callbacks.add(stateProvider);
		callbacks.add(cache);
		updateScaleEnabled();
		updateTheme();
		updateIgnores();
		updateScale();

		handleChangelog(config, chatMessageManager, client, cache.isCustomCursorPluginEnabled(), configManager);
	}

	private void updateCursorDefinition()
	{
		ContextualCursorDefinition definition = null;
		switch (config.getCursorSource())
		{
			case LOCAL_JSON:
				try
				{
					definition = loadLocalCursorDefinition(contextualCursorGson, cache.getCursorTheme().getLocalFileName());
					break;
				}
				catch (Exception e)
				{
					log.error("Could not load local cursor JSON", e);
					// fall-through
				}
			case JAVA:
				definition = ContextualCursor.toCursorDefinition(cache.getCursorTheme() == CursorTheme.OSRS);
				break;
		}

		assert definition != null;

		List<Cursor> cursors = new ArrayList<>(definition.getCursors());

		definition = definition.withCursors(cursors);
		definition.updateScale(cache.getCursorScale());

		cursorProvider.setDefinition(definition);
	}

	@Override
	protected void shutDown()
	{
		callCallbacks(ProviderCallbacks::onShutdown);

		callbacks.remove(cursorProvider);
		callbacks.remove(stateProvider);
		callbacks.remove(cache);

		overlayManager.remove(workerOverlayV2);
		overlayManager.remove(drawOverlayV2);

		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(mouseListener);

		previousState = null;
		currentState = null;
		nextState = null;
	}

	private void callCallbacks(Consumer<ProviderCallbacks> block)
	{
		for (ProviderCallbacks callback : callbacks)
		{
			block.accept(callback);
		}
	}

	// TODO: Show while hopping?
	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGGED_IN:
				cache.setLoggedOut(false);
				break;
			case LOGIN_SCREEN:
				cache.setLoggedOut(true);
				nextState = stateProvider.defaultCursorState();
				drawOverlayV2.render(null);
				currentState = null;
				previousState = null;
				nextState = null;
				break;
			default:
				break;
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		if (config.getHideKeybind().matches(keyEvent))
		{
			cache.setAltPressed(keyEvent.isAltDown());
		}
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		if (config.getHideKeybind().matches(keyEvent))
		{
			cache.setAltPressed(keyEvent.isAltDown());
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
				cache.setDebugTooltipEnabled(config.isDebugTooltipEnabled());
			}
			else if (event.getKey().equals(SCALE) || event.getKey().equals(ITEM_SCALE))
			{
				updateScale();
			}
			else if (event.getKey().equals(SCALE_SMOOTHING) || event.getKey().equals(ITEM_SCALE_SMOOTHING))
			{
				updateScaleEnabled();
			}
			else if (event.getKey().equals(CUSTOM_CURSOR))
			{
				cache.setCustomDefaultCursorEnabled(config.isCustomDefaultCursorEnabled());
			}
			else if (event.getKey().equals(CURSOR_THEME))
			{
				updateTheme();
			}
			else if (event.getKey().equals(DEFAULT_CURSOR_OVERLAY))
			{
				cache.setDefaultCursorOverlayEnabled(config.isDefaultCursorOverlayEnabled());
			}
			else if (event.getKey().equals(PERSIST_SPELLS))
			{
				cache.setPersistSpells(config.shouldPersistSpells());
			}
			else if (event.getKey().equals(PERSIST_ITEMS))
			{
				cache.setPersistItems(config.shouldPersistItems());
			}
			else if (event.getKey().equals(USE_ITEM_CURSOR))
			{
				cache.setShowUseItemCursorEnabled(config.isShowUseItemCursorEnabled());
			}
			else if (event.getKey().equals(CURSOR_BACKGROUND_MODE))
			{
				cache.setCursorBackgroundMode(config.getCursorBackgroundMode());
			}
		}
		else if ("runelite".equals(event.getGroup()) && "customcursorplugin".equals(event.getKey()))
		{
			cache.setCustomCursorPluginEnabled(Boolean.parseBoolean(event.getNewValue()));
			// Delaying this until after CustomCursorPlugin has run its shutdown which clears the cursor
			clientThread.invoke(() -> {
				if (!cache.isCustomCursorPluginEnabled())
				{
					stateProvider.setLastExternalCustomCursor(null);
				}
			});
		}
	}

	private void updateIgnores()
	{
		for (MenuTarget target : MenuTarget.VALUES)
		{
			ignoredTargets.put(target, target.isIgnored(config));
		}
	}

	private void updateScale()
	{
		cache.setCursorScale((double) config.getCursorScale() / 100);
		cache.setItemScale((double) config.getItemScale() / 100);
		callCallbacks(c -> c.onScaleChange(cache.getCursorScale(), cache.getItemScale()));
	}

	private void updateScaleEnabled()
	{
		cache.setCursorSmoothScalingEnabled(config.isCursorSmoothScalingEnabled());
		cache.setItemSmoothScalingEnabled(config.isItemSmoothScalingEnabled());
		callCallbacks(c -> c.onScaleSmoothingChange(cache.isCursorSmoothScalingEnabled(), cache.isItemSmoothScalingEnabled()));
	}

	private void updateTheme()
	{
		cache.setCursorTheme(config.getCursorTheme());
		updateCursorDefinition();
		callCallbacks(c -> c.onThemeChange(cache.getCursorTheme()));
	}

	// TODO: move into render()?
	@Subscribe
	public void onClientTick(ClientTick event)
	{
		Point mousePos = client.getMouseCanvasPosition();
		cache.setCursorInBounds(mouseInsideBounds(mousePos, client));
	}
}
