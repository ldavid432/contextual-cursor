package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.MenuEntryField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class SimpleValueMatcher<T> implements MenuEntryValueMatcher<T>
{
	@Getter
	private T value;
	private MenuEntryField<T> field;
	private ValuePredicate<T> predicate;

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return predicate.compare(
			field.getEntryValue(menuEntry),
			value
		);
	}

	@Override
	public T getEntryValue(MenuEntry entry)
	{
		return field.getEntryValue(entry);
	}

	@Override
	public boolean compare(T o, T o2)
	{
		return predicate.compare(o, o2);
	}
}

