package com.github.ldavid432.contextualcursor.menuentry.predicates;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;

// TODO: Doesn't need to be an enum
@AllArgsConstructor
public enum StringPredicate implements BiFunction<String, String, Boolean>
{
	EQUALS(String::equals),
	STARTS_WITH(String::startsWith),
	ENDS_WITH(String::endsWith),
	CONTAINS(String::contains),
	;

	private final BiFunction<String, String, Boolean> predicate;


	@Override
	public Boolean apply(String s, String s2)
	{
		return predicate.apply(s, s2);
	}
}
