package com.github.ldavid432.contextualcursor.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CursorTheme
{
	OSRS("OldSchool", "osrs-cursors"),
	RS2("RuneScape 2", "rs2-cursors");

	private final String displayText;
	private final String localFileName;

	@Override
	public String toString()
	{
		return displayText;
	}
}
