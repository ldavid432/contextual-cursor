package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import lombok.ToString;

@ToString(callSuper = true)
public class TargetMatcher extends SimpleValueMatcher<String>
{
	public TargetMatcher(String target, StringPredicate predicate)
	{
		super(target, StringField.TARGET, predicate, "target");
	}

	public TargetMatcher(String target)
	{
		this(target, StringPredicate.EQUALS);
	}

	@SuppressWarnings("unused") // gson constructor
	private TargetMatcher()
	{
		this(null);
	}
}
