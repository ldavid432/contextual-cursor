package com.github.ldavid432.contextualcursor.menuentry;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher.Operator;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleStringMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.StaticMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import java.util.Arrays;
import java.util.function.Function;
import net.runelite.api.MenuAction;
import net.runelite.client.util.Text;

public class MenuEntryMatchers
{
	// General matchers

	public static MenuEntryMatcher not(MenuEntryMatcher matcher)
	{
		return new NotMatcher(matcher);
	}

	public static MenuEntryMatcher hasAnyOf(MenuEntryMatcher... matchers)
	{
		if (matchers.length == 1)
		{
			return matchers[0];
		}
		return new CompositeMatcher(Operator.OR, matchers);
	}

	public static MenuEntryMatcher hasAllOf(MenuEntryMatcher... matchers)
	{
		if (matchers.length == 1)
		{
			return matchers[0];
		}
		return new CompositeMatcher(Operator.AND, matchers);
	}

	// Menu options

	public static MenuEntryMatcher hasOption(String... options)
	{
		if (options.length == 1)
		{
			return hasOption(options[0]);
		}
		return hasAnyOf(mapArray(options, MenuEntryMatchers::hasOption));
	}

	public static MenuEntryMatcher hasOption(String option)
	{
		return new SimpleStringMatcher(option, StringField.OPTION, StringPredicate.EQUALS);
	}

	public static MenuEntryMatcher optionStartsWith(String optionPrefix)
	{
		return new SimpleStringMatcher(optionPrefix, StringField.OPTION, StringPredicate.STARTS_WITH);
	}

	// Targets

	public static MenuEntryMatcher targetNamed(String target)
	{
		return new SimpleStringMatcher(target, StringField.TARGET, StringPredicate.EQUALS);
	}

	public static MenuEntryMatcher targetStartsWith(String targetPrefix)
	{
		return new SimpleStringMatcher(targetPrefix, StringField.TARGET, StringPredicate.STARTS_WITH);
	}

	public static MenuEntryMatcher targetEndsWith(String targetSuffix)
	{
		return new SimpleStringMatcher(targetSuffix, StringField.TARGET, StringPredicate.ENDS_WITH);
	}

	public static MenuEntryMatcher targetContains(String targetContents)
	{
		return new SimpleStringMatcher(targetContents, StringField.TARGET, StringPredicate.CONTAINS);
	}

	public static MenuEntryMatcher isWidgetTargetOption(String option, String fromTarget)
	{
		return hasAllOf(hasOption(option), isWidgetTarget(), targetStartsWith(fromTarget + " ->"));
	}

	// Menu Actions

	public static MenuEntryMatcher menuActionIsAnyOf(MenuAction... actions)
	{

		return hasAnyOf(mapArray(actions, MenuEntryMatchers::hasMenuAction));
	}

	public static MenuEntryMatcher hasMenuAction(MenuAction action)
	{
		return e -> e.getType() == action;
	}

	// Static Matchers (maybe just delete these and use enums directly?)

	public static MenuEntryMatcher isNpc()
	{
		return StaticMatcher.IS_NPC;
	}

	public static MenuEntryMatcher isObject()
	{
		return StaticMatcher.IS_OBJECT;
	}

	public static MenuEntryMatcher isGroundItem()
	{
		return StaticMatcher.IS_GROUND_ITEM;
	}

	public static MenuEntryMatcher isInterfaceItem()
	{
		return StaticMatcher.IS_INTERFACE_ITEM;
	}

	public static MenuEntryMatcher isMovement()
	{
		return StaticMatcher.IS_MOVEMENT;
	}

	public static MenuEntryMatcher isCancel()
	{
		return StaticMatcher.IS_CANCEL;
	}

	// this doesn't work great
	public static MenuEntryMatcher isInterface()
	{
		return StaticMatcher.IS_INTERFACE;
	}

	public static MenuEntryMatcher isSpell()
	{
		return StaticMatcher.IS_SPELL;
	}

	public static MenuEntryMatcher isPlayer()
	{
		return StaticMatcher.IS_PLAYER;
	}

	public static MenuEntryMatcher isWidgetTarget()
	{
		return StaticMatcher.IS_WIDGET_TARGET;
	}

	// Util

	public static String sanitize(String text)
	{
		return Text.removeTags(Text.sanitize(text).toLowerCase());
	}

	private static <T> MenuEntryMatcher[] mapArray(T[] array, Function<T, MenuEntryMatcher> map)
	{
		return Arrays.stream(array).map(map).toArray(MenuEntryMatcher[]::new);
	}
}
