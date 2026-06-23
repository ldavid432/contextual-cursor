package com.github.ldavid432.contextualcursor.menuentry.field;

import net.runelite.api.MenuEntry;

@FunctionalInterface
public interface MenuEntryField<T>
{
	T getEntryValue(MenuEntry menuEntry);
}
