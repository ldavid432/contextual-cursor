package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.menuentry.MenuTarget;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.MENU_EXTRA_TOP;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.MENU_OPTION_HEIGHT;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MenuEntryProvider
{
	private final Client client;
	private final ContextualCursorPlugin plugin;

	@Getter
	private MenuEntry lastSubmenuEntry = null;
	@Getter
	private boolean isInSubmenu = false;

	@Setter
	@Nullable
	private BiFunction<MenuEntry, Boolean, Void> listener = null;

	public MenuEntry getMenuEntry()
	{
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
			if (listener != null)
			{
				listener.apply(menuEntry, false);
			}
			return menuEntry;
		}
		else
		{
			if (listener != null)
			{
				listener.apply(menuEntry, true);
			}
			return null;
		}
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

}
