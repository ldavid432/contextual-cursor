package com.github.ldavid432.contextualcursor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CursorTheme
{
	OSRS("OldSchool"),
	RS2("RuneScape 2"),
	;

	private final String displayText;

	@Override
	public String toString()
	{
		return displayText;
	}
}
