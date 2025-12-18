package com.github.ldavid432.contextualcursor.menuentry;

import net.runelite.api.MenuEntry;

public interface MenuEntryMatcher
{
	boolean matches(MenuEntry menuEntry);
}
