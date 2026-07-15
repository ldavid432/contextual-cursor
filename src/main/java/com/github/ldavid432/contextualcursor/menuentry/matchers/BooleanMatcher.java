package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.BooleanPredicate;
import lombok.AllArgsConstructor;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class BooleanMatcher implements MenuEntryMatcher
{
	private BooleanPredicate predicate;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return predicate.test(menuEntry);
	}

	@Override
	public String getType()
	{
		return "boolean";
	}
}
