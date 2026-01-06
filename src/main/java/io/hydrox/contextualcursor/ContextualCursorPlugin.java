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
import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.DEBUG_TOOLTIP;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.google.inject.Provides;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Contextual Cursor",
	description = "RSHD-style image cursors",
	tags = {"cursor", "rs3", "rs2", "rshd", "context"}
)
@Slf4j
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

	@Getter
	private boolean altPressed;

	@Getter
	private boolean isDebugTooltipEnabled;

	@Getter
	@Setter
	private BufferedImage spriteToDraw;

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

		updateIgnores();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(contextualCursorWorkerOverlay);
		overlayManager.remove(contextualCursorDrawOverlay);
		contextualCursorWorkerOverlay.resetCursor();
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
			else if (Objects.equals(event.getKey(), DEBUG_TOOLTIP))
			{
				isDebugTooltipEnabled = config.isDebugTooltipEnabled();
			}
		}
	}

	private void updateIgnores()
	{
		for (MenuTarget target : MenuTarget.VALUES)
		{
			ignoredTargets.put(target, target.isIgnored(config));
		}
	}

	private void clearImages()
	{
		ContextualCursor.clearImages();
		SpellSprite.clearImages();
	}
}
