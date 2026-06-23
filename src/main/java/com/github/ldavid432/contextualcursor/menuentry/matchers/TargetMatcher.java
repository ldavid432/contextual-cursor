package com.github.ldavid432.contextualcursor.menuentry.matchers;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import static com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate.asStringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;

public class TargetMatcher extends SimpleValueMatcher<String>
{
	public TargetMatcher(String target, StringPredicate predicate)
	{
		super(target, entry -> sanitize(entry.getTarget()), predicate);
	}

	public TargetMatcher(String target)
	{
		this(target, asStringPredicate(ValuePredicate.equals()));
	}
}
