package com.github.ldavid432.contextualcursor.menuentry.field;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public enum StringField implements MenuEntryField<String>
{
	OPTION(entry -> sanitize(entry.getOption())),
	TARGET(entry -> sanitize(entry.getTarget()));

	@Delegate
	private final MenuEntryField<String> function;
}
