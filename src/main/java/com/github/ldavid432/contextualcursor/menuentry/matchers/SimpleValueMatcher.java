package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.MenuEntryField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@AllArgsConstructor
public class SimpleValueMatcher<T> implements MenuEntryValueMatcher<T>
{
	@Getter(onMethod_ = @VisibleForTesting)
	private T value;
	@Getter(onMethod_ = @VisibleForTesting)
	private MenuEntryField<T> field;
	@Getter(onMethod_ = @VisibleForTesting)
	private ValuePredicate<T> predicate;
	@Getter
	private String type;

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

