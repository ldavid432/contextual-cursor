package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import static com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate.asStringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;

public class TargetMatcher extends SimpleValueMatcher<String>
{
	public TargetMatcher(String target, StringPredicate predicate)
	{
		super(target, StringField.TARGET, predicate);
	}

	public TargetMatcher(String target)
	{
		this(target, asStringPredicate(ValuePredicate.equals()));
	}

	@SuppressWarnings("unused") // gson constructor
	private TargetMatcher()
	{
		this(null, null);
	}
}
