package com.github.ldavid432.contextualcursor.menuentry.predicates;

public interface StringPredicate extends ValuePredicate<String>
{
	StringPredicate STARTS_WITH = String::startsWith;
	StringPredicate ENDS_WITH = String::endsWith;
	StringPredicate CONTAINS = String::contains;

	static StringPredicate asStringPredicate(ValuePredicate<String> predicate)
	{
		return predicate::compare;
	}
}
