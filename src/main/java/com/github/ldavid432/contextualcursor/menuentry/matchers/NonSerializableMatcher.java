package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class NonSerializableMatcher implements MenuEntryMatcher
{
	private Predicate<MenuEntry> predicate;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return predicate.test(menuEntry);
	}

	@Override
	public String getType()
	{
		throw new RuntimeException("Cannot be serialized!");
	}
}
