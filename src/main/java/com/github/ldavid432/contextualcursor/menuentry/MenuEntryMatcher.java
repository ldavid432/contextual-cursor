package com.github.ldavid432.contextualcursor.menuentry;

import java.util.function.Predicate;

@FunctionalInterface
public interface MenuEntryMatcher extends Predicate<MenuEntry>
{
	boolean matches(MenuEntry menuEntry);

	@Override
	default boolean test(MenuEntry menuEntry)
	{
		return matches(menuEntry);
	}
}
