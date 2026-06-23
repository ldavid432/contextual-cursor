package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.MenuEntryField;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import net.runelite.api.MenuEntry;

public interface MenuEntryValueMatcher<T> extends MenuEntryMatcher, ValuePredicate<T>, MenuEntryField<T>
{
	T getValue();

	@Override
	default boolean matches(MenuEntry menuEntry)
	{
		return compare(getValue(), getEntryValue(menuEntry));
	}
}
