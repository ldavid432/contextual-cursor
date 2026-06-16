package com.github.ldavid432.contextualcursor.menuentry.matchers;

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class SimpleValueMatcher<T> implements MenuEntryValueMatcher<T>
{
	@Getter
	private T value;
	private Function<MenuEntry, T> getOption;
	private BiFunction<T, T, Boolean> predicate;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return predicate.apply(getOption.apply(menuEntry), value);
	}

	@Override
	public T getField(MenuEntry entry)
	{
		return getOption.apply(entry);
	}

	@Override
	public Boolean apply(T o, T o2)
	{
		return predicate.apply(o, o2);
	}
}

