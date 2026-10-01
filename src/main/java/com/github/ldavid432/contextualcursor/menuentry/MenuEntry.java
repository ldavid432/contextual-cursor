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
@Value(staticConstructor = "of")
public class MenuEntry
{
	@Nullable
	NPC npc;

	@Nullable
	Player player;

	@Nullable
	Integer itemId;

	boolean isItemOp;

	MenuAction type;

	String option;

	String target;

	@Nullable
	MenuEntry parent;

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

	public boolean isSubmenuEntry()
	{
		return parent != null;
	}

	@Nullable
	public static MenuEntry of(@Nullable net.runelite.api.MenuEntry entry, @Nullable net.runelite.api.MenuEntry parent)
	{
		if (entry == null)
		{
			return null;
		}
		return of(entry, MenuEntry.of(parent, (MenuEntry) null));
	}

	@Nullable
	public static MenuEntry of(@Nullable net.runelite.api.MenuEntry entry, @Nullable MenuEntry parent)
	{
		if (entry == null)
		{
			return null;
		}
		return of(
			entry.getNpc(),
			entry.getPlayer(),
			entry.getItemId() >= 0 ? entry.getItemId() : null,
			entry.isItemOp(),
			entry.getType(),
			entry.getOption(),
			entry.getTarget(),
			parent
		);
	}
}
