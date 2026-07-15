package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@AllArgsConstructor
public class NonNullMatcher<T> implements MenuEntryMatcher
{
	@Getter
	private String type;
	private Function<MenuEntry, T> getOption;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return getOption.apply(menuEntry) != null;
	}
}
