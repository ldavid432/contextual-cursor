package com.github.ldavid432.contextualcursor.menuentry;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.runelite.api.MenuAction;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

public class MenuEntryMatchers
{
	// General matchers

	public static MenuEntryMatcher not(MenuEntryMatcher matcher)
	{
		return menuEntry -> !matcher.matches(menuEntry);
	}

	public static MenuEntryMatcher hasAnyOf(MenuEntryMatcher... matchers)
	{
		return menuEntry -> Stream.of(matchers).anyMatch(matcher -> matcher.matches(menuEntry));
	}

	public static MenuEntryMatcher hasAllOf(MenuEntryMatcher... matchers)
	{
		return menuEntry -> Stream.of(matchers).allMatch(matcher -> matcher.matches(menuEntry));
	}

	// Menu options

	public static MenuEntryMatcher optionIsAnyOf(String... options)
	{
		return menuEntry -> Stream.of(options).map(MenuEntryMatchers::hasOption).anyMatch(matcher -> matcher.matches(menuEntry));
	}

	public static MenuEntryMatcher hasOption(String option)
	{
		return menuEntry -> Objects.equals(menuEntry.getOption().toLowerCase(), option);
	}

	public static MenuEntryMatcher optionStartsWith(String optionPrefix)
	{
		return onOption(optionPrefix, String::startsWith);
	}

	private static MenuEntryMatcher onOption(String option, BiFunction<String, String, Boolean> test)
	{
		return menuEntry -> test.apply(sanitize(menuEntry.getOption()), option);
	}

	// NPCs

	public static MenuEntryMatcher isNpc()
	{
		return menuEntry -> menuEntry.getNpc() != null;
	}

	// Objects

	public static MenuEntryMatcher isObject()
	{
		return menuEntry -> ArrayUtils.contains(OBJECT_TYPES, menuEntry.getType());
	}

	private static final MenuAction[] OBJECT_TYPES = {
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION, MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION, MenuAction.GAME_OBJECT_FIFTH_OPTION
	};

	// Ground Items

	public static MenuEntryMatcher isGroundItem()
	{
		return menuEntry -> ArrayUtils.contains(GROUND_ITEM_TYPES, menuEntry.getType());
	}

	private static final MenuAction[] GROUND_ITEM_TYPES = {
		MenuAction.GROUND_ITEM_FIRST_OPTION, MenuAction.GROUND_ITEM_SECOND_OPTION, MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION, MenuAction.GROUND_ITEM_FIFTH_OPTION
	};

	// Targets

	private static MenuEntryMatcher onTarget(String target, BiFunction<String, String, Boolean> test)
	{
		return menuEntry -> test.apply(sanitize(menuEntry.getTarget()), target);
	}

	public static MenuEntryMatcher targetNamed(String target)
	{
		return onTarget(target, String::equals);
	}

	public static MenuEntryMatcher targetStartsWith(String targetPrefix)
	{
		return onTarget(targetPrefix, String::startsWith);
	}

	public static MenuEntryMatcher targetEndsWith(String targetSuffix)
	{
		return onTarget(targetSuffix, String::endsWith);
	}

	// Util

	private static String sanitize(String text)
	{
		return Text.removeTags(Text.sanitize(text).toLowerCase());
	}
}
