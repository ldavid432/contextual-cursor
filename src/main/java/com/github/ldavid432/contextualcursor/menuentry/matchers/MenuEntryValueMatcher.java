package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import java.util.function.BiFunction;
import net.runelite.api.MenuEntry;

public interface MenuEntryValueMatcher<T> extends MenuEntryMatcher, BiFunction<T, T, Boolean>
{
	T getValue();

	T getField(MenuEntry entry);

	@Override
	default boolean matches(MenuEntry menuEntry)
	{
		return apply(getValue(), getField(menuEntry));
	}
}
