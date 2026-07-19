package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@AllArgsConstructor
public class SimpleStringMatcher implements MenuEntryMatcher
{
	@Getter(onMethod_ = @VisibleForTesting)
	private String value;
	@Getter(onMethod_ = @VisibleForTesting)
	private StringField field;
	@Getter(onMethod_ = @VisibleForTesting)
	private StringPredicate predicate;
	@Getter
	private final String type = "string";

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return predicate.apply(
			field.apply(menuEntry),
			value
		);
	}
}
