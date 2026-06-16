package com.github.ldavid432.contextualcursor.menuentry.matchers;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import com.github.ldavid432.contextualcursor.menuentry.predicates.GlobalPredicates;
import java.util.function.BiFunction;

public class OptionMatcher extends SimpleValueMatcher<String>
{
	public OptionMatcher(String option, BiFunction<String, String, Boolean> predicate)
	{
		// TODO: Sanitize in super?
		super(option, entry -> sanitize(entry.getOption()), predicate);
	}

	public OptionMatcher(String option)
	{
		this(option, GlobalPredicates.equals());
	}
}
