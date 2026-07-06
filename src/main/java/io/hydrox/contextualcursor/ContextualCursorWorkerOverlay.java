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

import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import com.github.ldavid432.contextualcursor.sprite.SpriteContext;
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
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

// TODO: Break up this class, it is getting bloated

@Slf4j
public class ContextualCursorWorkerOverlay extends Overlay
{
	private static final String BLANK_CURSOR_NAME = "contextual-cursor-blank";
	private static final String GENERIC_CURSOR_NAME = "contextual-cursor-generic";
	private static final Cursor BLANK_MOUSE = Toolkit.getDefaultToolkit().createCustomCursor(
		new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB),
		new java.awt.Point(0, 0),
		BLANK_CURSOR_NAME
	);
	private static final int MENU_OPTION_HEIGHT = 15;
	private static final int MENU_EXTRA_TOP = 4;

	private final Client client;
	private final ClientUI clientUI;
	private final ContextualCursorPlugin plugin;
	private final TooltipManager tooltipManager;
	private final CursorProvider cursorProvider;
	private final SpriteContext spriteContext;

	// Last top level menu entry that has a submenu
	private MenuEntry lastSubmenuEntry;
	private Sprite persistedSprite;
	private boolean isInSubmenu;
	private boolean cursorOverriden;
	private Cursor originalCursor;
	private Cursor lastDefaultCursor;
	private Tooltip spacerTooltip;
	private Tooltip genericSpacerTooltip;
	public Cursor genericCursor;

	@Inject
	ContextualCursorWorkerOverlay(Client client, ClientUI clientUI, ContextualCursorPlugin plugin,
	                              TooltipManager tooltipManager, CursorProvider cursorProvider, SpriteContext spriteContext)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		// Also allow on world map and welcome screen
		drawAfterInterface(InterfaceID.TOPLEVEL_DISPLAY);
		setPriority(1f);
		this.client = client;
		this.clientUI = clientUI;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;
		this.cursorProvider = cursorProvider;
		this.spriteContext = spriteContext;
	}

	private void storeOriginalCursor()
	{
		if (cursorOverriden)
		{
			return;
		}
		final Cursor currentCursor = clientUI.getCurrentCursor();
		if (isNotIgnoredCursor(currentCursor))
		{
			// Don't accidentally save things like the loading cursor
			if (isSavableCursorType(currentCursor))
			{
				log.debug("Storing original cursor: {}", currentCursor.getName());
				originalCursor = currentCursor;
			}
			else if (lastDefaultCursor != null)
			{
				log.debug("Storing last default cursor: {}", currentCursor.getName());
				originalCursor = lastDefaultCursor;
			}
			else
			{
				log.debug("NOT storing original cursor (no cursor to store): {}", currentCursor.getName());
			}
		}
		else
		{
			log.debug("NOT storing original cursor (wrong type): {}", currentCursor.getName());
		}
	}

	private boolean isNotIgnoredCursor(Cursor cursor)
	{
		return !cursor.getName().equals(BLANK_CURSOR_NAME) && !cursor.getName().equals(GENERIC_CURSOR_NAME);
	}

	private boolean isSavableCursorType(Cursor cursor)
	{
		return cursor.getType() == Cursor.DEFAULT_CURSOR || cursor.getType() == Cursor.CUSTOM_CURSOR;
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
		if (cursorOverriden)
		{
			cursorOverriden = false;
			plugin.setSpriteToDraw(null);
			boolean wasCursorRestored = restoreOriginalCursor();
			if (wasCursorRestored)
			{
				return;
			}
		}

		if (plugin.canOverrideDefaultCursor())
		{
			setGenericCursor();
		}
		else if (!plugin.isCustomCursorPluginEnabled())
		{
			log.debug("Resetting cursor (resetCursor)");
			clientUI.resetCursor();
		}
		else
		{
			// custom cursor plugin is ON - restore the custom cursor
			boolean wasCursorRestored = restoreOriginalCursor();
			if (!wasCursorRestored && lastDefaultCursor != null)
			{
				log.debug("Restoring last found cursor: {}", lastDefaultCursor.getName());
				clientUI.setCursor(lastDefaultCursor);
			}
		}
	}

	private boolean restoreOriginalCursor()
	{
		if (originalCursor != null)
		{
			log.debug("Restoring cursor: {}", originalCursor.getName());
			clientUI.setCursor(originalCursor);
			originalCursor = null;
			return true;
		}
		return false;
	}

	private void setGenericCursor()
	{
		if (plugin.isDefaultCursorOverlayEnabled())
		{
			if (!plugin.isLoggedOut() && plugin.isCursorInBounds())
			{
				log.debug("Restoring Generic overlay cursor");
				clientUI.setCursor(BLANK_MOUSE);
			}
			else
			{
				log.debug("Resetting cursor (setGenericCursor)");
				clientUI.resetCursor();
			}
		}
		else
		{
			if (genericCursor == null)
			{
				genericCursor = createGenericCursor();
			}
			log.debug("Restoring Generic cursor");
			clientUI.setCursor(genericCursor);
		}
	}

	private Cursor createGenericCursor()
	{
		BufferedImage icon = cursorProvider.getDefaultCursorSprite().getImage(spriteContext);
		BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(icon, 0, 0, null);
		g.dispose();

		return Toolkit.getDefaultToolkit().createCustomCursor(
			result,
			new java.awt.Point(0, 0),
			GENERIC_CURSOR_NAME
		);
	}

	public void checkLastCursor()
	{
		Cursor currentCursor = clientUI.getCurrentCursor();

		if (isNotIgnoredCursor(currentCursor) && isSavableCursorType(currentCursor) && lastDefaultCursor != currentCursor)
		{
			log.debug("Setting last default cursor: {}", currentCursor.getName());
			lastDefaultCursor = currentCursor;
		}
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isAltPressed() || !plugin.isCursorInBounds())
		{
			return null;
		}

		// Selection takes precedence over menu entries
		boolean isShowingSelectedCursor = processSelected();
		if (isShowingSelectedCursor)
		{
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

		if (menuEntry != null && !isEntryIgnored(menuEntry, isInSubmenu))
		{
			debugTooltip(false, menuEntry);
			processEntry(menuEntry, isInSubmenu);
		}
		else
		{
			debugTooltip(true, menuEntry);
			if (plugin.getSpriteToDraw() != null)
			{
				resetCursor();
			}
			else if (plugin.canDefaultCursorOverrideWithOverlay())
			{
				if (genericSpacerTooltip != null)
				{
					tooltipManager.addFront(genericSpacerTooltip);
				}
			}
		}
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
		Sprite sprite = cursorProvider.getSprite(menuEntry);

		// If we don't have a cursor for the submenu entry then use the parent cursor
		final MenuEntry lastSubmenuEntry1 = lastSubmenuEntry;
		if (sprite == null && isSubMenu && lastSubmenuEntry1 != null)
		{
			processEntry(lastSubmenuEntry1, false);
			return;
		}
		else
		{
			sprite = Objects.requireNonNullElse(sprite, cursorProvider.getDefaultCursorSprite());
		}

		setSpriteToDraw(sprite);
	}

	private boolean processSelected()
	{
		if (client.isWidgetSelected())
		{
			if (persistedSprite != null)
			{
				setSpriteToDraw(persistedSprite);
				return true;
			}
			else
			{
				Widget selectedWidget = client.getSelectedWidget();
				if (selectedWidget == null)
				{
					return false;
				}
				// TODO: Separate these two booleans, theoretically we should grab the sprite from the plugin when isShowUseItemCursorEnabled is on? - if other item sprites are added then isPersistItems grab find those too
				if (plugin.isShowUseItemCursorEnabled() && plugin.isPersistItems() && selectedWidget.getItemId() > 0)
				{
					log.debug("Persisting item {}", selectedWidget.getItemId());
					// TODO: Item sprite cache
					persistedSprite = itemSprite().id(selectedWidget.getItemId()).build();
					setSpriteToDraw(persistedSprite);
					return true;
				}
				else if (plugin.isPersistSpells() &&
					selectedWidget.getParent() != null &&
					selectedWidget.getParent().getId() == InterfaceID.MagicSpellbook.SPELLLAYER)
				{
					log.debug("Persisting spell");
					// (Theoretically) The instant a spell is selected the sprite should be the spell sprite so we can just use getSpriteToDraw
					//  Otherwise we would have to add logic to match the widget sprite ID to the spell which isn't ideal since spells have multiple sprites with different resolutions
					persistedSprite = plugin.getSpriteToDraw();
					setSpriteToDraw(persistedSprite);
					return true;
				}
			}
		}
		else if (persistedSprite != null)
		{
			persistedSprite = null;
		}
		return false;
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
			player = String.format("Player: %s", entry.getPlayer().getName());
		}

		tooltipManager.addFront(
			new Tooltip(
				Stream.of("Contextual Cursor Debug:", ignored, name, item, npc, player)
					.filter(Objects::nonNull)
					.reduce((s1, s2) -> s1 + "<br>" + s2)
					.orElse(name)
			)
		);
	}

	public void updateScale()
	{
		int spacerHeight = (int) ((40 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			spacerTooltip = new Tooltip(
				new ImageComponent(new BufferedImage(1, spacerHeight, BufferedImage.TYPE_INT_ARGB))
			);
		}
		else
		{
			spacerTooltip = null;
		}

		spacerHeight = (int) ((25 * plugin.getCursorScale()) - 30);
		if (spacerHeight > 0)
		{
			genericSpacerTooltip = new Tooltip(
				new ImageComponent(new BufferedImage(1, spacerHeight, BufferedImage.TYPE_INT_ARGB))
			);
		}
		else
		{
			genericSpacerTooltip = null;
		}
		genericCursor = null;
	}

	public void updateTheme()
	{
		genericCursor = null;
	}

	public void genericOverlayToggled()
	{
		if (!plugin.canOverrideDefaultCursor())
		{
			return;
		}

		resetCursor();
	}
}
