package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import lombok.ToString;

@ToString(callSuper = true)
public class OptionMatcher extends SimpleValueMatcher<String>
{
	public OptionMatcher(String option, StringPredicate predicate)
	{
		super(option, StringField.OPTION, predicate);
	}

	public OptionMatcher(String option)
	{
		this(option, StringPredicate.EQUALS);
	}

	@SuppressWarnings("unused") // gson constructor
	private OptionMatcher()
	{
		this(null);
	}
}
