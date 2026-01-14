package com.github.ldavid432.contextualcursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorConfig.GROUP;
import com.github.ldavid432.contextualcursor.config.ScaleMethod;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(GROUP)
public interface ContextualCursorConfig extends Config
{
	String GROUP = "contextualcursor";
	String DEBUG_TOOLTIP = "showDebugTooltip";
	String SCALE = "scale";
	String SCALE_METHOD = "scaleMethod";

	@ConfigSection(
		name = "Scale",
		description = "Adjust the cursor scaling",
		position = 0
	)
	String scaleSection = "scaleSection";

	@Range(min = 5)
	@Units(Units.PERCENT)
	@ConfigItem(
		name = "Cursor Scale",
		description = "Scale the contextual cursors size",
		keyName = SCALE,
		position = 0,
		section = scaleSection
	)
	default int getCursorScale()
	{
		return 100;
	}

	@ConfigItem(
		name = "Scale Method",
		description = "Algorithm used to scale the cursor. Has no effect if Cursor Scale is 1",
		keyName = SCALE_METHOD,
		position = 1,
		section = scaleSection
	)
	default ScaleMethod getCursorScaleMethod()
	{
		return ScaleMethod.Smooth;
	}

	@ConfigSection(
		name = "Ignores",
		description = "Don't show the contextual cursor for certain targets",
		position = 1
	)
	String ignoreSection = "ignoreSection";

	@ConfigItem(
		name = "Ignore NPCs",
		description = "Don't show contextual cursors on NPCs",
		keyName = "ignoreNpcs",
		position = 0,
		section = ignoreSection
	)
	default boolean shouldIgnoreNpcs()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Objects",
		description = "Don't show contextual cursors on objects",
		keyName = "ignoreObjects",
		position = 1,
		section = ignoreSection
	)
	default boolean shouldIgnoreObjects()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Inventory Items",
		description = "Don't show contextual cursors on items in your inventory",
		keyName = "ignoreItems",
		position = 2,
		section = ignoreSection
	)
	default boolean shouldIgnoreItems()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Spells",
		description = "Don't show contextual cursors on spells",
		keyName = "ignoreSpells",
		position = 3,
		section = ignoreSection
	)
	default boolean shouldIgnoreSpells()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Ground Items",
		description = "Don't show contextual cursors on items on the ground",
		keyName = "ignoreGroundItems",
		position = 4,
		section = ignoreSection
	)
	default boolean shouldIgnoreGroundItems()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Players",
		description = "Don't show contextual cursors on players",
		keyName = "ignorePlayers",
		position = 5,
		section = ignoreSection
	)
	default boolean shouldIgnorePlayers()
	{
		return false;
	}

	@ConfigItem(
		name = "Ignore Interfaces",
		description = "Don't show contextual cursors on interfaces. It is not recommended to disable this as interface matching is not very accurate.",
		keyName = "ignoreInterfaces",
		position = 100,
		section = ignoreSection,
		// Maybe will look at adding this eventually but probably need to be more thorough with what is and isn't an interface
		//  Hopefully inventory and spellbook are good enough for now
		hidden = true
	)
	default boolean shouldIgnoreInterfaces()
	{
		return true;
	}

	@ConfigSection(
		name = "Debug",
		description = "Debugging options",
		position = 100,
		closedByDefault = true
	)
	String debugSection = "debugSection";

	@ConfigItem(
		name = "Debug Tooltip",
		description = "Shows a tooltip containing debug information on the current menu entry.<br>" +
			"If you'd like a specific menu entry to be added to the plugin you can screenshot<br>" +
			"the tooltip of your desired menu entry and include it in a GitHub issue.",
		keyName = DEBUG_TOOLTIP,
		position = 0,
		section = debugSection
	)
	default boolean isDebugTooltipEnabled()
	{
		return false;
	}

}

// TODO: ignore players
