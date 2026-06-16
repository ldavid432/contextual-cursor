package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public class CompositeMatcher implements MenuEntryMatcher
{
	@NonNull
	private Operator operator;
	@NonNull
	private List<MenuEntryMatcher> children;

	public CompositeMatcher(@Nonnull Operator operator, MenuEntryMatcher... matchers)
	{
		this.operator = operator;
		this.children = List.of(matchers);
	}

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		switch (operator)
		{
			case AND:
				return children.stream().allMatch(matcher -> matcher.matches(menuEntry));
			case OR:
				return children.stream().anyMatch(matcher -> matcher.matches(menuEntry));
		}

		return false;
	}

	public enum Operator
	{
		AND,
		OR
	}
}
