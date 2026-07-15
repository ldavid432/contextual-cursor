package com.github.ldavid432.contextualcursor.menuentry;

import com.github.ldavid432.contextualcursor.ContextualCursorConfig;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isCancel;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isGroundItem;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isInterface;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isInterfaceItem;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isMovement;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isNpc;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isObject;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isPlayer;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isSpell;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.not;
import io.hydrox.contextualcursor.ContextualCursor;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import net.runelite.api.MenuEntry;

public enum MenuTarget
{
	ALWAYS_IGNORED(c -> true, isMovement(), isCancel()),
	ITEM(ContextualCursorConfig::shouldIgnoreItems, isInterfaceItem()),
	SPELL(ContextualCursorConfig::shouldIgnoreSpells, isSpell()),
	INTERFACE(ContextualCursorConfig::shouldIgnoreInterfaces, hasAllOf(isInterface(), not(ContextualCursor.WIKI.getMatcher()))),
	NPC(ContextualCursorConfig::shouldIgnoreNpcs, isNpc()),
	GROUND_ITEM(ContextualCursorConfig::shouldIgnoreGroundItems, isGroundItem()),
	OBJECT(ContextualCursorConfig::shouldIgnoreObjects, isObject()),
	PLAYER(ContextualCursorConfig::shouldIgnorePlayers, isPlayer()),
	OTHER(c -> false, e -> true);

	private final Function<ContextualCursorConfig, Boolean> isIgnored;
	@Getter
	private final Predicate<MenuEntry> matcher;

	public boolean isIgnored(ContextualCursorConfig config)
	{
		return isIgnored.apply(config);
	}

	@SafeVarargs
	MenuTarget(Function<ContextualCursorConfig, Boolean> isIgnored, Predicate<MenuEntry>... matchers)
	{
		this.isIgnored = isIgnored;
		this.matcher = menuEntry -> Arrays.stream(matchers).anyMatch(matcher -> matcher.test(menuEntry));
	}

	public static final MenuTarget[] VALUES = values();
}
