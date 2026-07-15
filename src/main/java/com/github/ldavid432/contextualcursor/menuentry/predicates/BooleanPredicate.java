package com.github.ldavid432.contextualcursor.menuentry.predicates;

import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.runelite.api.MenuEntry;

@AllArgsConstructor
public enum BooleanPredicate
{
	IS_ITEM(MenuEntry::isItemOp),
	HAS_ITEM_ID(entry -> entry.getItemId() > 0),
	;

	@Delegate
	private final Predicate<MenuEntry> predicate;
}
