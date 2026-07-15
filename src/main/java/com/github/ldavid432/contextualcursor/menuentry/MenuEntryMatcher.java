package com.github.ldavid432.contextualcursor.menuentry;

import java.util.function.Predicate;
import net.runelite.api.MenuEntry;

public interface MenuEntryMatcher extends Predicate<MenuEntry>
{
	boolean matches(MenuEntry menuEntry);

	@Override
	default boolean test(MenuEntry menuEntry) {
		return matches(menuEntry);
	}

	String getType();
}
