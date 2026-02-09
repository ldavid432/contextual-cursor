/*
 * Copyright (c) 2020-2022 Enriath <ikada@protonmail.ch>
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
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.*;
import com.github.ldavid432.contextualcursor.CursorSkin;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
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
import net.runelite.client.util.ColorUtil;

@PluginDescriptor(
	name = "Contextual Cursor",
	description = "RSHD-style image cursors",
	tags = {"cursor", "rs3", "rs2", "rshd", "context", "theme"}
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

	@Getter
	private boolean altPressed;

	@Getter
	private boolean isOSRSSkin;

	@Getter
	private boolean isDebugTooltipEnabled;

	@Getter
	@Setter
	@Nullable
	private Sprite spriteToDraw;

	@Getter
	private double cursorScale = 1.0;

	@Getter
	private boolean isSmoothScalingEnabled;

	@Getter
	private boolean isCustomCursorPluginEnabled;

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
		overlayManager.add(contextualCursorWorkerOverlay);
		overlayManager.add(contextualCursorDrawOverlay);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(mouseListener);

		isSmoothScalingEnabled = config.isCursorSmoothScalingEnabled();
		isOSRSSkin = config.skin() == CursorSkin.OSRS;
		updateIgnores();
		updateScale();
		isCustomCursorPluginEnabled = pluginManager.isPluginActive(customCursorPlugin);
		contextualCursorWorkerOverlay.resetCursor();

		// Since last seen version wasn't in 1.0 checking for only it will trigger for everyone who installs the plugin.
		//  By only triggering this during startup while not logged in we can "better" attempt to determine if this is a previous install or not.
		//  Still not totally accurate but better than nothing.
		if (config.getLastSeenVersion() < ContextualCursorConfig.CURRENT_VERSION)
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				// Existing install (theoretically)
				chatMessageManager.queue(
					QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(
							ColorUtil.wrapWithColorTag("Contextual Cursor has been updated!<br>", Color.RED) +
								ColorUtil.wrapWithColorTag("* The plugin has a new maintainer (for a few months now) if you have any feedback please leave it on the *new* github<br>", Color.RED) +
								ColorUtil.wrapWithColorTag("* Cursor themes are now supported! Currently includes osrs and rs2 (default)", Color.RED)
						)
						.build()
				);
			}
			else if (client.getGameState() == GameState.LOGGED_IN)
			{
				// New install (theoretically)
				if (!isCustomCursorPluginEnabled)
				{
					// Ideally this would be true by default, but we don't want to suddenly enable it for everyone who's been using this plugin for years.
					// For now only enable by default for new users (if they don't already have a custom cursor)
					config.setCustomCursorEnabled(true);
				}
			}
			config.setLastSeenVersion(ContextualCursorConfig.CURRENT_VERSION);
		}
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
		if (event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.LOADING)
		{
			contextualCursorWorkerOverlay.resetCursor();
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent)
	{
		altPressed = keyEvent.isAltDown();
	}

	@Override
	public void keyReleased(KeyEvent keyEvent)
	{
		altPressed = keyEvent.isAltDown();
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
				updateScale();
			}
			else if (event.getKey().equals(SCALE_SMOOTHING))
			{
				isSmoothScalingEnabled = config.isCursorSmoothScalingEnabled();
				contextualCursorDrawOverlay.rerenderImages();
			}
			else if (event.getKey().equals(SKIN))
			{
				isOSRSSkin = config.skin() == CursorSkin.OSRS;
				ContextualCursor.clearImages();
				contextualCursorWorkerOverlay.genericCursor = null;
				contextualCursorDrawOverlay.setBlankCursor(isOSRSSkin);
				contextualCursorDrawOverlay.rerenderImages();
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

	private void updateScale()
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
}
