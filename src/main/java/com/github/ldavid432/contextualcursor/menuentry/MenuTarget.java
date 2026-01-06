package com.github.ldavid432.contextualcursor.menuentry;

import com.github.ldavid432.contextualcursor.ContextualCursorConfig;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isCancel;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isGroundItem;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isInterface;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isMovement;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isNpc;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isObject;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isPlayer;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isSpell;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.optionIsAnyOf;
import java.util.function.Function;
import lombok.Getter;
import net.runelite.api.MenuEntry;

public enum MenuTarget
{
	ALWAYS_IGNORED(c -> true, isMovement(), isCancel()),
	ITEM(ContextualCursorConfig::shouldIgnoreItems, MenuEntry::isItemOp,
		hasAllOf(
			entry -> entry.getItemId() > 0,
			optionIsAnyOf("use", "examine")
		)),
	SPELL(ContextualCursorConfig::shouldIgnoreSpells, isSpell()),
	INTERFACE(ContextualCursorConfig::shouldIgnoreInterfaces, isInterface()),
	NPC(ContextualCursorConfig::shouldIgnoreNpcs, isNpc()),
	GROUND_ITEM(ContextualCursorConfig::shouldIgnoreGroundItems, isGroundItem()),
	OBJECT(ContextualCursorConfig::shouldIgnoreObjects, isObject()),
	PLAYER(ContextualCursorConfig::shouldIgnorePlayers, isPlayer()),
	OTHER(c -> false, e -> true);

	private final Function<ContextualCursorConfig, Boolean> isIgnored;
	@Getter
	private final MenuEntryMatcher matcher;

	public boolean isExcluded(ContextualCursorConfig config)
	{
		return isIgnored.apply(config);
	}

	MenuTarget(Function<ContextualCursorConfig, Boolean> isIgnored, MenuEntryMatcher... matchers)
	{
		this.isIgnored = isIgnored;
		this.matcher = MenuEntryMatchers.hasAnyOf(matchers);
	}

	public static final MenuTarget[] VALUES = values();
}
