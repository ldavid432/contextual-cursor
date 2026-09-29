package com.github.ldavid432.contextualcursor.menuentry.field;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntry;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
public enum StringField
{
	OPTION(entry -> sanitize(entry.getOption())),
	TARGET(entry -> sanitize(entry.getTarget()));

	@Delegate
	private final Function<MenuEntry, String> function;
}
