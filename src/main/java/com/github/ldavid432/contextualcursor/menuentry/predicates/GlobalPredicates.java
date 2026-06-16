package com.github.ldavid432.contextualcursor.menuentry.predicates;

import java.util.function.BiFunction;

public class GlobalPredicates
{
	public static <T> BiFunction<T, T, Boolean> equals()
	{
		return T::equals;
	}
}
