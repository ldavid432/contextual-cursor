package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import static com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate.asStringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;

// TODO: Just deserialize SimpleValueMatcher directly?
public class OptionMatcher extends SimpleValueMatcher<String>
{
	public OptionMatcher(String option, StringPredicate predicate)
	{
		// TODO: Sanitize in super?
		super(option, StringField.OPTION, predicate);
	}

	public OptionMatcher(String option)
	{
		this(option, asStringPredicate(ValuePredicate.equals()));
	}

	@SuppressWarnings("unused") // gson constructor
	private OptionMatcher()
	{
		this(null, null);
	}
}
