/*
 * Copyright (c) 2019-2022 Enriath <ikada@protonmail.ch>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.hydrox.contextualcursor;

import lombok.Getter;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ContextualCursor
{
	BLANK("blank"),
	GENERIC("generic"), //Cursor inside background

	BANK("bank", "bank", "bank-cargo"),
	CLOSE("close", "close", "disembark"),
	CONFIGURE(SpriteID.OptionsIcons._51, "configure", "configuration"), // Wrench sprite
	DRINK("drink", "drink"),
	DROP("drop", "drop", "empty", "deposit", "quick-deposit", "deposit-cargo", "empty basket"),
	EAT("eat", "eat"),
	ENTER("enter", "climb-into", "enter", "exit", "yanille", "varrock", "seers' village", "camelot",
		"grand exchange", "watchtower", "go-through"),
	EQUIP("equip", "wield", "wear", "equip"),
	EXCHANGE(SpriteID.GeSmallicons.GUIDE_PRICE, "exchange", "trade", "trade with", "collect", "buy-boat"),
	FRIEND(SpriteID.SideiconsInterface.FRIENDS, "add friend"),
	IGNORE(SpriteID.SideiconsInterface.IGNORES, "add ignore"),
	IMPOSSIBLE("impossible", "destroy", "stop-navigating", "cancel-task"),
	LADDER("ladder", "climb"),
	LADDER_DOWN("ladder_down", "climb-down", "climb down"),
	LADDER_UP("ladder_up", "climb-up", "climb up"),
	OPEN("open", "open"),
	PICK_UP("pick_up", "take", "withdraw" ,"fill", "take-cargo", "take-last-cargo", "collect-from", "take-from", "pick-up", "take-knife"),
	PLANK("plank", "buy-plank"),
	READ("read", "read", "story"),
	REPORT(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING, "report"),
	SEARCH("search", "search", "lookup", "examine", "view", "look-inside", "inspect"),
	TALK("talk", "talk", "talk-to", "talk to", "command"),
	UNTIE("untie", "tether"),
	USE("use", "use"),
	WIKI("wiki", "lookup-entity"),

	// Sailing
	NAVIGATE(SpriteID.IconSailingFacilities24x24._4, "navigate"), // Ship's wheel
	SALVAGE(SpriteID.IconSailingFacilities24x24._5, "deploy"), // Salvage hook
	UNSET_SAILS(SpriteID.IconSailingFacilities24x24._0, "un-set"), // Empty sails
	SET_SAILS(SpriteID.IconSailingFacilities24x24._1, "set"), // Sails
	TRIM_SAILS(SpriteID.IconSailingFacilities24x24._2, "trim"), // Luffed sails
	CANNON(SpriteID.IconSailingFacilities24x24._6, "check-ammunition", "reset-ammunition"),
	TRAWLING_NET(SpriteID.IconSailingFacilities24x24._12, "raise"),
	TRAWLING_NET_LOWER(SpriteID.IconSailingFacilities24x24._15, "lower"),
	WIND(SpriteID.IconSailingFacilities24x24._7, "release-mote"),

	// Skills
	AGILITY(SpriteID.Staticons.AGILITY, "balance", "balance-across", "climb-across", "climb-on", "climb-over",
		"cross", "grab", "grapple", "hurdle", "jump", "jump-up", "jump-to", "jump-off", "jump-in", "jump-on", "kick",
		"leap", "shoot", "squeeze-past", "squeeze-through", "swing", "swing across", "swing-across", "swing-on", "tap",
		"tag", "teeth-grip", "tread-softly", "vault", "walk-on", "walk-across", "crawl-through", "jump-over", "escape"),
	ATTACK(SpriteID.Staticons.ATTACK, "attack"),
	CONSTRUCTION(SpriteID.Staticons2.CONSTRUCTION, "build", "remove", "craft", "modify"),
	COOKING(SpriteID.Staticons.COOKING, "cook", "churn", "cook-at", "prepare-fish"),
	CRAFTING(SpriteID.Staticons.CRAFTING, "spin", "weave"),
	FARMING(SpriteID.Staticons2.FARMING, "check-health", "harvest", "rake", "pick", "pick-fruit", "clear", "pay"),
	FIREMAKING(SpriteID.Staticons.FIREMAKING, "light", "feed"),
	FISHING(SpriteID.Staticons.FISHING, "net", "bait", "lure", "small net", "harpoon", "cage", "big net",
		"use-rod", "fish", "take-net"),
	FLETCHING(SpriteID.Staticons.FLETCHING, "carve", "decorate"),
	HERBLORE(SpriteID.Staticons.HERBLORE, "clean"),
	HUNTER(SpriteID.Staticons2.HUNTER, "catch", "lay", "dismantle", "reset", "check"),
	MAGIC(SpriteID.Staticons.MAGIC, "spellbook", "teleport", "teleport menu", "ancient", "lunar", "arceuus", "standard"), // `venerate` interferes with the Dark Altar's RC use
	MINING(SpriteID.Staticons.MINING, "mine", "smash-to-bits"),
	PRAYER(SpriteID.Staticons.PRAYER, "pray", "bury", "pray-at", "offer-fish", "scatter"),
	RANGED(SpriteID.Staticons.RANGED, "fire", "fire-at"),
	RUNECRAFTING(SpriteID.Staticons2.RUNECRAFT, "craft-rune", "imbue"),
	SMITHING(SpriteID.Staticons.SMITHING, "smelt", "smith", "hammer", "refine"),
	SLAYER(SpriteID.Staticons2.SLAYER, "assignment"),
	STRENGTH(SpriteID.Staticons.STRENGTH, "bang", "move"),
	THIEVING(SpriteID.Staticons.THIEVING, "steal-from", "pickpocket", "search for traps", "pick-lock"),
	WOODCUTTING(SpriteID.Staticons.WOODCUTTING, "chop down", "chop-down", "chop", "cut", "hack"),
	SAILING(SpriteID.Staticons2.SAILING, "board", "board-previous", "board-friend", "dock", "customise-boat",
		"recover-boat", "sort-salvage", "chart", "pry-open", "collect-data", "start-trial", "start-previous-rank");

	private BufferedImage cursor;
	private Integer spriteID;
	private String[] actions;

	ContextualCursor(String cursor_path, String ... actions)
	{
		this.cursor = ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", cursor_path));
		this.actions = actions;
	}

	ContextualCursor(int spriteID, String ... actions)
	{
		this.spriteID = spriteID;
		this.actions = actions;
	}

	private static final Map<String, ContextualCursor> cursorMap = new HashMap<>();

	static
	{
		for (ContextualCursor cursor : values())
		{
			for (String action : cursor.actions)
			{
				cursorMap.put(action, cursor);
			}
		}
	}

	static ContextualCursor get(String action)
	{
		return cursorMap.getOrDefault(action.toLowerCase(), GENERIC);
	}
}
