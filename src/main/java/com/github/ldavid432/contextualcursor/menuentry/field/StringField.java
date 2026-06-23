package com.github.ldavid432.contextualcursor.menuentry.field;

import net.runelite.api.MenuEntry;

public interface StringField extends MenuEntryField<String>
{
	StringField OPTION = MenuEntry::getOption;
	StringField TARGET = MenuEntry::getTarget;
}
