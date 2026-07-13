package com.github.ldavid432.contextualcursor.menuentry.predicates;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public enum StringPredicate implements ValuePredicate<String>
{
	STARTS_WITH(String::startsWith),
	ENDS_WITH(String::endsWith),
	CONTAINS(String::contains),
	EQUALS(String::equals);

	@Delegate
	private final ValuePredicate<String> predicate;
}
