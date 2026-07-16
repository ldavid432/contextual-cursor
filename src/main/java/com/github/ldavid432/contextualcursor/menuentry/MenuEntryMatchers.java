package com.github.ldavid432.contextualcursor.menuentry;

import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.matchers.BooleanMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher.Operator;
import com.github.ldavid432.contextualcursor.menuentry.matchers.MenuActionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NonNullMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NonSerializableMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleStringMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.BooleanPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import java.util.Arrays;
import java.util.function.Predicate;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
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

	// For uses of MenuEntryMatcher that don't need to be serialized (i.e. in MenuTarget)
	public static MenuEntryMatcher nonSerializable(Predicate<MenuEntry> predicate)
	{
		return new NonSerializableMatcher(predicate);
	}

	// Menu options

	public static MenuEntryMatcher hasOption(String... options)
	{
		if (options.length == 1)
		{
			return hasOption(options[0]);
		}
		return hasAnyOf(Arrays.stream(options).map(MenuEntryMatchers::hasOption).toArray(MenuEntryMatcher[]::new));
	}

	public static MenuEntryMatcher hasOption(String option)
	{
		return new SimpleStringMatcher(option, StringField.OPTION, StringPredicate.EQUALS);
	}

	public static MenuEntryMatcher optionStartsWith(String optionPrefix)
	{
		return new SimpleStringMatcher(optionPrefix, StringField.OPTION, StringPredicate.STARTS_WITH);
	}

	// NPCs

	public static MenuEntryMatcher isNpc()
	{
		return new NonNullMatcher<>("isNpc", MenuEntry::getNpc);
	}

	// Objects

	public static MenuEntryMatcher isObject()
	{
		return new MenuActionMatcher("isObject", OBJECT_TYPES);
	}

	private static final MenuAction[] OBJECT_TYPES = {
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION, MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION, MenuAction.GAME_OBJECT_FIFTH_OPTION, MenuAction.EXAMINE_OBJECT
	};

	// Ground Items

	public static MenuEntryMatcher isGroundItem()
	{
		return new MenuActionMatcher("isGroundItem", GROUND_ITEM_TYPES);
	}

	private static final MenuAction[] GROUND_ITEM_TYPES = {
		MenuAction.GROUND_ITEM_FIRST_OPTION, MenuAction.GROUND_ITEM_SECOND_OPTION, MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION, MenuAction.GROUND_ITEM_FIFTH_OPTION, MenuAction.EXAMINE_ITEM_GROUND
	};

	// Inventory Items

	public static MenuEntryMatcher isInterfaceItem()
	{
		return hasAllOf(
			new BooleanMatcher(BooleanPredicate.IS_ITEM),
			hasAllOf(new BooleanMatcher(BooleanPredicate.HAS_ITEM_ID), hasOption("use", "examine"))
		);
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

	// Action types

	public static MenuEntryMatcher isMovement()
	{
		return new MenuActionMatcher("isMovement", MenuAction.WALK, MenuAction.SET_HEADING);
	}

	public static MenuEntryMatcher isCancel()
	{
		return new MenuActionMatcher("isCancel", MenuAction.CANCEL);
	}

	// this doesn't work great
	public static MenuEntryMatcher isInterface()
	{
		return new MenuActionMatcher("isInterface", MenuAction.CC_OP, MenuAction.CC_OP_LOW_PRIORITY);
	}

	// Spellbook spells

	public static MenuEntryMatcher isSpell()
	{
		return hasAllOf(isWidgetTarget(), hasOption("cast", "resurrect", "reanimate"));
	}

	// Players

	public static MenuEntryMatcher isPlayer()
	{
		return new NonNullMatcher<>("isPlayer", MenuEntry::getPlayer);
	}

	// Widget

	private static final MenuAction[] WIDGET_TYPES = new MenuAction[]{
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.WIDGET_TARGET_ON_PLAYER,
		MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, MenuAction.WIDGET_TARGET_ON_WIDGET, MenuAction.WIDGET_TARGET
	};

	public static MenuEntryMatcher isWidgetTarget()
	{
		return new MenuActionMatcher("isWidgetTarget", WIDGET_TYPES);
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
