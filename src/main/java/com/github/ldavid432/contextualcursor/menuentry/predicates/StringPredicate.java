package com.github.ldavid432.contextualcursor.menuentry.predicates;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public enum StringPredicate
{
	STARTS_WITH(String::startsWith),
	ENDS_WITH(String::endsWith),
	CONTAINS(String::contains),
	EQUALS(String::equals);

	@Delegate
	private final BiFunction<String, String, Boolean> predicate;
}
