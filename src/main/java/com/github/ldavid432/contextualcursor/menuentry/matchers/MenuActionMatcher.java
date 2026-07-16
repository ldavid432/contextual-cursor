package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import org.apache.commons.lang3.ArrayUtils;

@ToString
public class MenuActionMatcher implements MenuEntryMatcher
{
	@Getter
	private final String type;

	@NonNull
	private final MenuAction[] types;

	public MenuActionMatcher(String type, MenuAction... types)
	{
		this.type = type;
		this.types = types;
	}

	@Override
	public boolean matches(MenuEntry menuEntry)
	{
		return ArrayUtils.contains(types, menuEntry.getType());
	}
}
