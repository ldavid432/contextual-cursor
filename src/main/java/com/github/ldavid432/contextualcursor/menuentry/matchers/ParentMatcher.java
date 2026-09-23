package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntry;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParentMatcher implements MenuEntryMatcher
{
	@SuppressWarnings("unused")
	private final String type = "parent";

	@Getter(onMethod_ = @VisibleForTesting)
	@NonNull
	private MenuEntryMatcher matcher;

	@Override
	public boolean matches(@Nonnull MenuEntry menuEntry)
	{
		return menuEntry.getParent() != null && matcher.matches(menuEntry.getParent());
	}
}
