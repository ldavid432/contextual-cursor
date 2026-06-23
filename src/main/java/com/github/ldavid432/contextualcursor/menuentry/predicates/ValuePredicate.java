package com.github.ldavid432.contextualcursor.menuentry.predicates;

@FunctionalInterface
public interface ValuePredicate<T>
{
	boolean compare(T t1, T t2);

	static <T> ValuePredicate<T> equals()
	{
		return T::equals;
	}
}
