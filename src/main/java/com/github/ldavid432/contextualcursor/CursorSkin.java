package com.github.ldavid432.contextualcursor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CursorSkin
{
    OSRS("OldSchool"),
    RS2("Runescape 2");

    private final String name;

}