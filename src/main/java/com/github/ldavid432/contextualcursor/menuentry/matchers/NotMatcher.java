package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntry;

@ToString
@RequiredArgsConstructor
public class NotMatcher implements MenuEntryMatcher
{
	@Getter(onMethod_ = @VisibleForTesting)
	@NonNull
	private MenuEntryMatcher matcher;
	@SuppressWarnings("unused")
	private final String type = "not";

	@Override
	public boolean matches(@Nonnull MenuEntry menuEntry)
	{
		return !matcher.matches(menuEntry);
	}

}
