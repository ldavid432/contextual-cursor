package com.github.ldavid432.contextualcursor.menuentry.field;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;

public interface StringField extends MenuEntryField<String>
{
	StringField OPTION = entry -> sanitize(entry.getOption());
	StringField TARGET = entry -> sanitize(entry.getTarget());
}
