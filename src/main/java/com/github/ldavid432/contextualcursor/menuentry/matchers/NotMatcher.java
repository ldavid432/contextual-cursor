package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@RequiredArgsConstructor
public class NotMatcher implements MenuEntryMatcher
{
	@Getter(onMethod_ = @VisibleForTesting)
	@NonNull
	private MenuEntryMatcher parent;

	@Getter
	private final String type = "not";

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return !parent.matches(menuEntry);
	}

}
