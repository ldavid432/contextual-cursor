package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.runelite.api.MenuEntry;

@ToString
@AllArgsConstructor
public class CompositeMatcher implements MenuEntryMatcher
{
	@Getter(onMethod_ = @VisibleForTesting)
	@NonNull
	private Operator operator;
	@Getter(onMethod_ = @VisibleForTesting)
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
