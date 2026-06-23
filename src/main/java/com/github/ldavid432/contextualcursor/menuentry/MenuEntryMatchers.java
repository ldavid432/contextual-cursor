package com.github.ldavid432.contextualcursor.menuentry;

import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher.Operator;
import com.github.ldavid432.contextualcursor.menuentry.matchers.MenuActionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NonNullMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.OptionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.TargetMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import java.util.Arrays;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.ArrayUtils;

public class MenuEntryMatchers
{
	// General matchers

	public static MenuEntryMatcher not(MenuEntryMatcher matcher)
	{
		return new NotMatcher(matcher);
	}

	public static MenuEntryMatcher hasAnyOf(MenuEntryMatcher... matchers)
	{
		return new CompositeMatcher(Operator.OR, matchers);
	}

	public static MenuEntryMatcher hasAllOf(MenuEntryMatcher... matchers)
	{
		return new CompositeMatcher(Operator.AND, matchers);
	}

	// Menu options

	public static MenuEntryMatcher optionIsAnyOf(String... options)
	{
		return hasAnyOf(Arrays.stream(options).map(MenuEntryMatchers::hasOption).toArray(MenuEntryMatcher[]::new));
	}

	public static MenuEntryMatcher hasOption(String option)
	{
		return new OptionMatcher(option);
	}

	public static MenuEntryMatcher optionStartsWith(String optionPrefix)
	{
		return new OptionMatcher(optionPrefix, StringPredicate.STARTS_WITH);
	}

	// NPCs

	public static MenuEntryMatcher isNpc()
	{
		return new NonNullMatcher<>(MenuEntry::getNpc);
	}

	// Objects

	public static MenuEntryMatcher isObject()
	{
		return menuEntry -> ArrayUtils.contains(OBJECT_TYPES, menuEntry.getType());
	}

	private static final MenuAction[] OBJECT_TYPES = {
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION, MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION, MenuAction.GAME_OBJECT_FIFTH_OPTION, MenuAction.EXAMINE_OBJECT
	};

	// Ground Items

	public static MenuEntryMatcher isGroundItem()
	{
		return new MenuActionMatcher(GROUND_ITEM_TYPES);
	}

	private static final MenuAction[] GROUND_ITEM_TYPES = {
		MenuAction.GROUND_ITEM_FIRST_OPTION, MenuAction.GROUND_ITEM_SECOND_OPTION, MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION, MenuAction.GROUND_ITEM_FIFTH_OPTION, MenuAction.EXAMINE_ITEM_GROUND
	};

	// Targets

	public static MenuEntryMatcher targetNamed(String target)
	{
		return new TargetMatcher(target);
	}

	public static MenuEntryMatcher targetStartsWith(String targetPrefix)
	{
		return new TargetMatcher(targetPrefix, StringPredicate.STARTS_WITH);
	}

	public static MenuEntryMatcher targetEndsWith(String targetSuffix)
	{
		return new TargetMatcher(targetSuffix, StringPredicate.ENDS_WITH);
	}

	public static MenuEntryMatcher targetContains(String targetContents)
	{
		return new TargetMatcher(targetContents, StringPredicate.CONTAINS);
	}

	// Action types

	public static MenuEntryMatcher isMovement()
	{
		return new MenuActionMatcher(MenuAction.WALK, MenuAction.SET_HEADING);
	}

	public static MenuEntryMatcher isCancel()
	{
		return new MenuActionMatcher(MenuAction.CANCEL);
	}

	// this doesn't work great
	public static MenuEntryMatcher isInterface()
	{
		return new MenuActionMatcher(MenuAction.CC_OP, MenuAction.CC_OP_LOW_PRIORITY);
	}

	// Spellbook spells

	public static MenuEntryMatcher isSpell()
	{
		return hasAllOf(isWidgetTarget(), hasOption("cast"));
	}

	// Players

	public static MenuEntryMatcher isPlayer()
	{
		return new NonNullMatcher<>(MenuEntry::getPlayer);
	}

	// Widget

	private static final MenuAction[] WIDGET_TYPES = new MenuAction[]{
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.WIDGET_TARGET_ON_PLAYER,
		MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, MenuAction.WIDGET_TARGET_ON_WIDGET, MenuAction.WIDGET_TARGET
	};

	public static MenuEntryMatcher isWidgetTarget()
	{
		return new MenuActionMatcher(WIDGET_TYPES);
	}

	public static MenuEntryMatcher isWidgetTarget(String option, String fromTarget)
	{
		return hasAllOf(hasOption(option), isWidgetTarget(), targetStartsWith(fromTarget + " ->"));
	}

	// Util

	public static String sanitize(String text)
	{
		return Text.removeTags(Text.sanitize(text).toLowerCase());
	}
}
