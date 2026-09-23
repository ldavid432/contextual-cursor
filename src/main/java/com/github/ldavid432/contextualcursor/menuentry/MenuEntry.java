package com.github.ldavid432.contextualcursor.menuentry;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.sanitize;
import javax.annotation.Nullable;
import lombok.Value;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;

/**
 * Wraps net.runelite.api.MenuEntry so we can add our own logic
 */
@Value
public class MenuEntry
{
	NPC npc;

	Player player;

	Integer itemId;

	MenuAction type;

	String option;

	String target;

	public String getTarget()
	{
		return sanitize(target);
	}

	public String getOption()
	{
		return sanitize(option);
	}

	public boolean isTargetNpc()
	{
		return npc != null;
	}

	public boolean isTargetPlayer()
	{
		return player != null;
	}

	public boolean isTargetItem()
	{
		return itemId != null;
	}

	@Nullable
	public static MenuEntry of(@Nullable net.runelite.api.MenuEntry entry)
	{
		if (entry == null) return null;
		return new MenuEntry(
			entry.getNpc(),
			entry.getPlayer(),
			entry.getItemId() >= 0 ? entry.getItemId() : null,
			entry.getType(),
			entry.getOption(),
			entry.getTarget()
		);
	}
}
