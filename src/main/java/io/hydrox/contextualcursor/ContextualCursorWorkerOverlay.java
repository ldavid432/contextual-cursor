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

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.Text;
import javax.inject.Inject;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ContextualCursorWorkerOverlay extends Overlay
{
	private static final Cursor BLANK_MOUSE = Toolkit.getDefaultToolkit().createCustomCursor(
		new BufferedImage(32, 32,BufferedImage.TYPE_INT_ARGB),
		new java.awt.Point(0, 0),
		"blank"
	);
	private static final Tooltip SPACER_TOOLTIP = new Tooltip(
		new ImageComponent(new BufferedImage(1, 10, BufferedImage.TYPE_INT_ARGB))
	);
	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)(?:</col>| -> )");
	private static final int MENU_OPTION_HEIGHT = 15;
	private static final int MENU_EXTRA_TOP = 4;
	private static final Set<MenuAction> IGNORED_ACTIONS = Sets.newHashSet(
		MenuAction.WALK, MenuAction.CC_OP, MenuAction.CANCEL, MenuAction.CC_OP_LOW_PRIORITY, MenuAction.SET_HEADING
	);

	private final Client client;
	private final ClientUI clientUI;
	private final ContextualCursorPlugin plugin;
	private final SpriteManager spriteManager;
	private final TooltipManager tooltipManager;

	// Last top level menu entry that has a submenu
	private MenuEntry lastSubmenuEntry;
	private boolean isInSubmenu;
	private boolean cursorOverriden;
	private Cursor originalCursor;

	@Inject
	ContextualCursorWorkerOverlay(Client client, ClientUI clientUI, ContextualCursorPlugin plugin,
								  SpriteManager spriteManager, TooltipManager tooltipManager)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.HIGHEST);
		this.client = client;
		this.clientUI = clientUI;
		this.plugin = plugin;
		this.spriteManager = spriteManager;
		this.tooltipManager = tooltipManager;
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

	void resetCursor()
	{
		if (cursorOverriden)
		{
			cursorOverriden = false;
			plugin.setSpriteToDraw(null);
			if (originalCursor != null)
			{
				clientUI.setCursor(originalCursor);
			}
			else
			{
				clientUI.resetCursor();
			}
		}
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

		if (menuEntry == null ||
			(!(menuEntry.isItemOp() || (isInSubmenu && menuEntry.getItemId() > 0))
			&& !menuEntry.getOption().equals("Examine")
			&& IGNORED_ACTIONS.contains(menuEntry.getType())))
		{
			resetCursor();
			return null;
		}

		processEntry(menuEntry.getType(), menuEntry.getOption(),  menuEntry.getTarget(), isInSubmenu);
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

	private static final Set<MenuAction> SPELL_TYPES = Sets.newHashSet(
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.WIDGET_TARGET_ON_PLAYER,
		MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, MenuAction.WIDGET_TARGET_ON_WIDGET, MenuAction.WIDGET_TARGET
	);

	private void processEntry(MenuAction type, String option, String target, boolean isSubMenu)
	{
		final ContextualCursor cursor;
		if (SPELL_TYPES.contains(type) && option.equals("Cast"))
		{
			final Matcher spellFinder = SPELL_FINDER.matcher(target.toLowerCase());

			if (!spellFinder.find())
			{
				return;
			}

			final String spellText = spellFinder.group(1);
			final SpellSprite spell = SpellSprite.get(Text.sanitize(spellText));
			if (spell == null) {
				return;
			}

			final BufferedImage magicSprite = spriteManager.getSprite(spell.spriteID, 0);
			if (magicSprite == null)
			{
				return;
			}

			setSpriteToDraw(magicSprite);
			return;
		}
		else if (option.equals("Lookup") && Text.removeTags(target).startsWith("Wiki ->"))
		{
			cursor = ContextualCursor.WIKI;
		}
		else
		{
			cursor = ContextualCursor.get(Text.removeTags(option));
		}

		// If we don't have a cursor for the submenu entry then use the parent cursor
		final MenuEntry lastSubmenuEntry1 = lastSubmenuEntry;
		if (cursor == ContextualCursor.GENERIC && isSubMenu && lastSubmenuEntry1 != null) {
			processEntry(lastSubmenuEntry1.getType(), lastSubmenuEntry1.getOption(), lastSubmenuEntry1.getTarget(), false);
			return;
		}

		if (cursor == null)
		{
			resetCursor();
			return;
		}

		BufferedImage sprite = cursor.getCursor();
		if (cursor.getSpriteID() != null)
		{
			if (client.getSpriteOverrides().containsKey(cursor.getSpriteID()))
			{
				sprite = client.getSpriteOverrides().get(cursor.getSpriteID()).toBufferedImage();
			}
			else
			{
				sprite = spriteManager.getSprite(cursor.getSpriteID(), 0);
			}
			if (sprite == null)
			{
				return;
			}
		}
		if (sprite != null)
		{
			setSpriteToDraw(sprite);
		}
	}

	private void setSpriteToDraw(BufferedImage sprite)
	{
		storeOriginalCursor();
		clientUI.setCursor(BLANK_MOUSE);
		cursorOverriden = true;
		plugin.setSpriteToDraw(sprite);
		// Add an empty tooltip to keep real tooltips out of the way
		tooltipManager.addFront(SPACER_TOOLTIP);
	}
}
