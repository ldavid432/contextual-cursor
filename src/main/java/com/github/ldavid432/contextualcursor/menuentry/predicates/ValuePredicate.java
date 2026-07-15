package com.github.ldavid432.contextualcursor.menuentry.predicates;

// TODO: Maybe delete this
@FunctionalInterface
public interface ValuePredicate<T>
{
	boolean compare(T t1, T t2);
}
