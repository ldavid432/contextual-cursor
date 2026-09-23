package com.github.ldavid432.contextualcursor.menuentry;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface MenuEntryMatcher extends Predicate<MenuEntry>
{
	boolean matches(@Nonnull MenuEntry menuEntry);

	@Override
	default boolean test(MenuEntry menuEntry)
	{
		return matches(menuEntry);
	}
}
