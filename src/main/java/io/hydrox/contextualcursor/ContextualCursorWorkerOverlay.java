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
import com.github.ldavid432.contextualcursor.CursorSkin;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static io.hydrox.contextualcursor.ContextualCursor.GENERIC_CURSOR;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.client.plugins.interfacestyles.Skin;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

@Slf4j
public class ContextualCursorWorkerOverlay extends Overlay
{
	private static final Cursor BLANK_MOUSE = Toolkit.getDefaultToolkit().createCustomCursor(
		new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
		new java.awt.Point(0, 0),
		"blank"
	);
	private static final int MENU_OPTION_HEIGHT = 15;
	private static final int MENU_EXTRA_TOP = 4;

	private final Client client;
	private final ClientUI clientUI;
	private final ContextualCursorPlugin plugin;
	private final TooltipManager tooltipManager;
	private final ContextualCursorConfig config;

	// Last top level menu entry that has a submenu
	private MenuEntry lastSubmenuEntry;
	private boolean isInSubmenu;
	private boolean cursorOverriden;
	private Cursor originalCursor;
	private Tooltip spacerTooltip;
	public Cursor genericCursor;

	@Inject
	ContextualCursorWorkerOverlay(Client client, ClientUI clientUI, ContextualCursorPlugin plugin,
								  TooltipManager tooltipManager, ContextualCursorConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(1f);
		this.client = client;
		this.clientUI = clientUI;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;
		this.config = config;
	}

	private void storeOriginalCursor()
	{
		if (cursorOverriden)
		{
			return;
		}
		final Cursor currentCursor = clientUI.getCurrentCursor();
		if (!currentCursor.getName().equals("blank"))
		{
			originalCursor = clientUI.getCurrentCursor();
		}
	}

	void shutdown()
	{
		genericCursor = null;
		if (!plugin.isCustomCursorPluginEnabled())
		{
			clientUI.resetCursor();
		}
	}

	void resetCursor()
	{
		resetCursor(config.isCustomCursorEnabled());
	}

	void resetCursor(boolean isCustomCursorEnabled)
	{
		if (cursorOverriden)
		{
			cursorOverriden = false;
			plugin.setSpriteToDraw(null);
			if (originalCursor != null)
			{
				clientUI.setCursor(originalCursor);
			}
			else if (!plugin.isCustomCursorPluginEnabled() && isCustomCursorEnabled)
			{
				if (genericCursor == null)
				{
					genericCursor = createGenericCursor();
				}
				clientUI.setCursor(genericCursor);
			}
			else
			{
				clientUI.resetCursor();
			}
		}
		else if (!plugin.isCustomCursorPluginEnabled() && isCustomCursorEnabled)
		{
			if (genericCursor == null)
			{
				genericCursor = createGenericCursor();
			}
			clientUI.setCursor(genericCursor);
		}
		else if (!isCustomCursorEnabled)
		{
			clientUI.resetCursor();
		}
	}

	private Cursor createGenericCursor()
	{
		BufferedImage icon = ContextualCursor.GENERIC_CURSOR.getImage(plugin.isOSRSSkin());
		BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(icon, 0, 0, null);
		g.dispose();

		return Toolkit.getDefaultToolkit().createCustomCursor(
			result,
			new java.awt.Point(0, 0),
			"generic"
		);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isAltPressed())
		{
			resetCursor();
			return null;
		}

		final MenuEntry menuEntry;

		if (client.isMenuOpen())
		{
			menuEntry = processMenu(client.getMenu());
		}
		else
		{
			isInSubmenu = false;
			lastSubmenuEntry = null;
			final MenuEntry[] menuEntries = client.getMenu().getMenuEntries();
			int last = menuEntries.length - 1;

			if (last < 0)
			{
				return null;
			}

			menuEntry = menuEntries[last];
		}

		if (menuEntry == null || isEntryIgnored(menuEntry, isInSubmenu))
		{
			debugTooltip(true, menuEntry);
			resetCursor();
			return null;
		}

		debugTooltip(false, menuEntry);
		processEntry(menuEntry, isInSubmenu);
		return null;
	}

	private MenuEntry processMenu(Menu menu)
	{
		final MenuEntry lastSubmenuEntry1 = lastSubmenuEntry;
		// This is slightly bugged since there is a small margin around menus where they will stay open (see other comment below)
		//  so this may think you are in a submenu if you move just outside the parent menu where the submenu would appear
		//  Not much we can do about it unless there is a way to determine if a submenu is open or closed
		if (isCursorOutsideMenu(menu) && lastSubmenuEntry1 != null && lastSubmenuEntry1.getSubMenu() != null && !isCursorOutsideMenu(lastSubmenuEntry1.getSubMenu()))
		{
			return processSubmenu(lastSubmenuEntry1.getSubMenu());
		}
		// Outside of parent menu, not in a submenu
		else if (isCursorOutsideMenu(menu))
		{
			return null;
		}

		final MenuEntry hoveredMenuEntry = getHoveredMenuEntry(menu);
		if (hoveredMenuEntry == null)
		{
			return null;
		}

		// This can bug out on stuff with submenus too close together (looking at you forestry basket)
		//  since there is some logic to allow you to move over other entries and to the submenu without closing the submenu
		//  https://github.com/runelite/runelite/issues/19670#issuecomment-3621605835
		if (hoveredMenuEntry.getSubMenu() != null)
		{
			lastSubmenuEntry = hoveredMenuEntry;
		}

		isInSubmenu = false;

		return hoveredMenuEntry;
	}

	private MenuEntry processSubmenu(Menu submenu)
	{
		final MenuEntry hoveredMenuEntry = getHoveredMenuEntry(submenu);
		if (hoveredMenuEntry == null)
		{
			return null;
		}

		isInSubmenu = true;

		return hoveredMenuEntry;
	}

	private MenuEntry getHoveredMenuEntry(Menu menu)
	{
		final MenuEntry[] menuEntries = menu.getMenuEntries();

		final int fromTop = (client.getMouseCanvasPosition().getY() - MENU_EXTRA_TOP) - menu.getMenuY();

		final int index = menuEntries.length - (fromTop / MENU_OPTION_HEIGHT);

		if (index >= menuEntries.length || index < 0)
		{
			return null;
		}

		return menuEntries[index];
	}

	private boolean isCursorOutsideMenu(Menu menu)
	{
		return menu.getMenuX() > client.getMouseCanvasPosition().getX() || menu.getMenuX() + menu.getMenuWidth() < client.getMouseCanvasPosition().getX();
	}

	private void processEntry(MenuEntry menuEntry, boolean isSubMenu)
	{
		Sprite sprite = ContextualCursor.get(menuEntry);

		// If we don't have a cursor for the submenu entry then use the parent cursor
		final MenuEntry lastSubmenuEntry1 = lastSubmenuEntry;
		if (sprite == null && isSubMenu && lastSubmenuEntry1 != null)
		{
			processEntry(lastSubmenuEntry1, false);
			return;
		}
		else
		{
			sprite = Objects.requireNonNullElse(sprite, GENERIC_CURSOR);
		}

		setSpriteToDraw(sprite);
	}

	private boolean isEntryIgnored(MenuEntry entry, boolean isInSubmenu)
	{
		MenuTarget target = mapTarget(entry, isInSubmenu);
		return plugin.getIgnoredTargets().getOrDefault(target, false);
	}

	private MenuTarget mapTarget(MenuEntry entry, boolean isInSubmenu)
	{
		for (MenuTarget target : MenuTarget.VALUES)
		{
			if (target.getMatcher().matches(entry) ||
				// isInSubmenu isn't available in the MenuEntryMatcher
				(target == MenuTarget.ITEM && isInSubmenu && entry.getItemId() > 0))
			{
				return target;
			}
		}
		return MenuTarget.OTHER;
	}

	private void setSpriteToDraw(Sprite sprite)
	{
		storeOriginalCursor();
		clientUI.setCursor(BLANK_MOUSE);
		cursorOverriden = true;
		plugin.setSpriteToDraw(sprite);
		// Add an empty tooltip to keep real tooltips out of the way
		if (spacerTooltip != null)
		{
			tooltipManager.addFront(spacerTooltip);
		}
	}

	private void debugTooltip(boolean isIgnored, MenuEntry entry)
	{
		if (entry == null || !plugin.isDebugTooltipEnabled())
		{
			return;
		}

		String ignored = null;
		if (isIgnored)
		{
			ignored = ColorUtil.wrapWithColorTag("Ignored", Color.RED);
		}

		String name = String.format("option=%s, type=%s", entry.getOption(), entry.getType());
		if (entry.getTarget() != null && !entry.getTarget().isBlank())
		{
			name += String.format(", target=%s", entry.getTarget());
		}

		String item = null;
		if (entry.isItemOp() || entry.getItemId() > 0)
		{
			item = String.format("Item: isItemOp=%s, id=%s", entry.isItemOp(), entry.getItemId());
		}

		String npc = null;
		if (entry.getNpc() != null)
		{
			npc = String.format("NPC: %s", entry.getNpc().getName());
		}

		String player = null;
		if (entry.getPlayer() != null)
		{
			player = String.format("PLayer: %s", entry.getPlayer().getName());
		}

		tooltipManager.addFront(
			new Tooltip(
				Stream.of(ignored, name, item, npc, player)
					.filter(Objects::nonNull)
					.reduce((s1, s2) -> s1 + "<br>" + s2)
					.get()
			)
		);
	}

	public void updateScale()
	{
		int spacerHeight = (int) ((40 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			spacerTooltip = new Tooltip(
				new ImageComponent(new BufferedImage(1, (int) ((40 * plugin.getCursorScale()) - 30), BufferedImage.TYPE_INT_ARGB))
			);
		}
		else
		{
			spacerTooltip = null;
		}
	}
}
