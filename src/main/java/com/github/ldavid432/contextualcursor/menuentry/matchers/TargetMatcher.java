package com.github.ldavid432.contextualcursor.menuentry.matchers;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import com.github.ldavid432.contextualcursor.menuentry.predicates.GlobalPredicates;
import java.util.function.BiFunction;

public class TargetMatcher extends SimpleValueMatcher<String>
{
	public TargetMatcher(String target, BiFunction<String, String, Boolean> predicate)
	{
		super(target, entry -> sanitize(entry.getTarget()), predicate);
	}

	public TargetMatcher(String target)
	{
		this(target, GlobalPredicates.equals());
	}
}
