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

import java.awt.Graphics2D;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.customcursor.CustomCursorPlugin;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

@PluginDescriptor(
	name = "Contextual Cursor",
	description = "RSHD-style image cursors"
)
@Slf4j
@PluginDependency(CustomCursorPlugin.class)
public class ContextualCursorPlugin extends Plugin implements KeyListener, MouseListener
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
	private CustomCursorPlugin customCursorPlugin;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ClientUI clientUI;

	@Inject
	private ClientThread clientThread;

	@Getter
	private boolean altPressed;

	@Getter
	@Setter
	private BufferedImage spriteToDraw;

	private BufferedImage cursorGeneric;

	private boolean customCursorPluginEnabled = false;

	protected void startUp()
	{
		overlayManager.add(contextualCursorWorkerOverlay);
		overlayManager.add(contextualCursorDrawOverlay);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(this);

		cursorGeneric = createGeneric();
		customCursorPluginEnabled = pluginManager.isPluginEnabled(customCursorPlugin);
		updateCursor();

	}

	private BufferedImage createGeneric()
	{
		BufferedImage icon = ContextualCursor.GENERIC.getCursor();
		BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(icon, 0, 0, null);
		g.dispose();

		return result;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(contextualCursorWorkerOverlay);
		overlayManager.remove(contextualCursorDrawOverlay);
		contextualCursorWorkerOverlay.resetCursor();
		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(this);

		if (!customCursorPluginEnabled)
		{
			clientUI.resetCursor();
		}
	}

	private void updateCursor()
	{

		if (customCursorPluginEnabled)
		{
			return;
		}

		clientUI.setCursor(cursorGeneric, "contextual-generic");

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!"runelite".equals(event.getGroup()) || !"customcursorplugin".equals(event.getKey()))
		{
			return;
		}

		boolean enabled = Boolean.parseBoolean(event.getNewValue());
		customCursorPluginEnabled = enabled;

		if (!enabled)
		{
			clientThread.invoke(this::updateCursor);
		}
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

	// Beyond this point is junk.
	// Look upon this method-bloat and despair!

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		return mouseEvent;
	}

	@Override
	public void keyTyped(KeyEvent keyEvent)
	{

	}
}
