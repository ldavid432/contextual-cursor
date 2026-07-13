package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@AllArgsConstructor
public class NotMatcher implements MenuEntryMatcher
{
	@Getter(onMethod_ = @VisibleForTesting)
	private MenuEntryMatcher parent;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return !parent.matches(menuEntry);
	}
}
