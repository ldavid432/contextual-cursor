package com.github.ldavid432.contextualcursor.menuentry.matchers;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import static com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate.asStringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;

// TODO: Just deserialize SimpleValueMatcher directly?
public class OptionMatcher extends SimpleValueMatcher<String>
{
	public OptionMatcher(String option, StringPredicate predicate)
	{
		// TODO: Sanitize in super?
		super(option, entry -> sanitize(entry.getOption()), predicate);
	}

	public OptionMatcher(String option)
	{
		this(option, asStringPredicate(ValuePredicate.equals()));
	}
}
