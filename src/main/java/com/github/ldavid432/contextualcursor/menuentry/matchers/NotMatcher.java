package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import lombok.AllArgsConstructor;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class NotMatcher implements MenuEntryMatcher
{
	private MenuEntryMatcher parent;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return !parent.matches(menuEntry);
	}
}
