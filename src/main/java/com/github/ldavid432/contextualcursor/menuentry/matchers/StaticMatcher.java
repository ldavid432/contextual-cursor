package com.github.ldavid432.contextualcursor.menuentry.matchers;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTarget;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.menuActionIsAnyOf;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;

/**
 * Matchers that take no parameters, these will always be static. We can optimize serialization by making these an enum
 * which results in them being serialized as a string.
 */
@AllArgsConstructor
public enum StaticMatcher implements MenuEntryMatcher
{
	IS_OBJECT(
		MenuAction.GAME_OBJECT_FIRST_OPTION, MenuAction.GAME_OBJECT_SECOND_OPTION, MenuAction.GAME_OBJECT_THIRD_OPTION,
		MenuAction.GAME_OBJECT_FOURTH_OPTION, MenuAction.GAME_OBJECT_FIFTH_OPTION, MenuAction.EXAMINE_OBJECT
	),
	IS_GROUND_ITEM(
		MenuAction.GROUND_ITEM_FIRST_OPTION, MenuAction.GROUND_ITEM_SECOND_OPTION, MenuAction.GROUND_ITEM_THIRD_OPTION,
		MenuAction.GROUND_ITEM_FOURTH_OPTION, MenuAction.GROUND_ITEM_FIFTH_OPTION, MenuAction.EXAMINE_ITEM_GROUND
	),
	IS_WIDGET_TARGET(
		MenuAction.WIDGET_TARGET_ON_GAME_OBJECT, MenuAction.WIDGET_TARGET_ON_NPC, MenuAction.WIDGET_TARGET_ON_PLAYER,
		MenuAction.WIDGET_TARGET_ON_GROUND_ITEM, MenuAction.WIDGET_TARGET_ON_WIDGET, MenuAction.WIDGET_TARGET
	),
	IS_MOVEMENT(MenuAction.WALK, MenuAction.SET_HEADING),
	IS_INTERFACE(MenuAction.CC_OP, MenuAction.CC_OP_LOW_PRIORITY),
	IS_CANCEL(MenuAction.CANCEL),
	IS_SPELL(hasAllOf(isWidgetTarget(), hasOption("cast", "resurrect", "reanimate"))),
	IS_ITEM(MenuEntry::isItemOp),
	HAS_ITEM_ID(entry -> entry.getItemId() > 0),
	IS_INTERFACE_ITEM(hasAnyOf(IS_ITEM, hasAllOf(HAS_ITEM_ID, hasOption("use", "examine")))),
	IS_NPC(e -> e.getNpc() != null),
	IS_PLAYER(e -> e.getPlayer() != null),
	;

	StaticMatcher(MenuAction... actions)
	{
		matcher = menuActionIsAnyOf(actions);
	}

	@Delegate
	private final MenuEntryMatcher matcher;
}
