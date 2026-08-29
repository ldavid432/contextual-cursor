/*
 * Copyright (c) 2019-2022 Enriath <ikada@protonmail.ch>
 * Copyright (c) 2026 Lake David <ldavid432@gmail.com>
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

import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.OffsetCursor;
import com.github.ldavid432.contextualcursor.cursor.ScaledPoint;
import com.github.ldavid432.contextualcursor.cursor.local.ItemCursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isNpc;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isObject;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTarget;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTargetOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.not;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.optionStartsWith;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetContains;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetEndsWith;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetNamed;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetStartsWith;
import com.github.ldavid432.contextualcursor.sprite.CacheSprite;
import com.github.ldavid432.contextualcursor.sprite.ItemSprite;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.SpriteID;

@Slf4j
public enum ContextualCursor implements Cursor
{
	BANK("bank", "bank", "coffer",
		hasAllOf(hasOption("use"), targetNamed("bank chest"))),
	CLOSE("close", "close", "shut"),
	CHARGE("charge", "charge"),
	CONFIGURE(SpriteID.OptionsIcons._51, "configure", "configuration", "config", "animation", "cosmetics",
		"preferences", "activity", "change", "toggle", "features", "options", "functions", "settings", "setup",
		optionStartsWith("toggle-"), optionStartsWith("toggle ")),
	DIG("dig", "dig", "clear", "dig-up"),
	DRINK("drink", "drink", "drink-from"),
	DROP("drop", "drop", "empty", "deposit", "quick-deposit", "empty basket", "bank-cargo", "store",
		"place-item", "discard",
		optionStartsWith("deposit-"),
		optionStartsWith("store-"),
		optionStartsWith("remove-")),
	EAT("eat", "eat", "eat-from", "guzzle"),
	ENTER("enter", "climb-into", "enter", "go-through", "pass-through", "pass", "climb-through",
		"quick-enter", "walk-through", "quick-slide", "climb-under", "quick-pass", "home",
		optionStartsWith("enter-")),
	EQUIP("equip", "wield", "wear", "equip"),
	EXCHANGE(SpriteID.GeSmallicons.GUIDE_PRICE, "exchange", "trade", "trade with", "buy-boat", "buy", "trade-in",
		"shop", "rewards",
		optionStartsWith("trade-")),
	EXIT("exit", "exit", "exit-through", "escape", "quick-escape", "leave", "quick-exit", "disembark",
		optionStartsWith("exit-")),
	FRIEND(SpriteID.SideiconsInterface.FRIENDS, "add friend"),
	HEAL("heal", "heal", "help", "apply", "medic"),
	IGNORE(SpriteID.SideiconsInterface.IGNORES, "add ignore"),
	IMPOSSIBLE("impossible", "destroy", "stop-navigating", "cancel-task", "remove", "dismiss", "forfeit"),
	LADDER("ladder", "climb"),
	LADDER_DOWN("ladder_down", "climb-down", "climb down", "bottom-floor", "go-down", "descend", "walk-down"),
	LADDER_UP("ladder_up", "climb-up", "climb up", "top-floor", "walk-up"),
	LISTEN("listen", "listen", optionStartsWith("listen-")),
	MUSIC(SpriteID.SideiconsInterface.MUSIC, "play"),
	OPEN("open", "open", "release", "quick-open"),
	PICK_LOCK("picklock", "pick-lock", "picklock"),
	PICK_UP("pick_up", "take", "withdraw", "fill", "collect-from", "pick-up", "collect", "claim",
		optionStartsWith("take-"), optionStartsWith("take "),
		hasAllOf(hasOption("harvest"), isNpc()),  // Various NPC corpses
		hasAllOf(MenuEntryMatchers.hasOption("search", "big-search"), targetStartsWith("reward"))  // various minigame reward objects
	),
	PLANK("plank", "buy-plank"),
	PROD(new ItemSprite(ItemID.CATTLEPROD), hasOption("prod")),
	PULL("pull", "pull"),
	PUSH("push", "push"),
	READ("read", "read", "story", "guide", "log"),
	REPORT(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING, "report"),
	SEARCH("search", "examine", "view", "inspect", "investigate", "peek", "admire", "look", "check",
		"prospect", "observe", "peer-into", "scry",
		optionStartsWith("look-"), optionStartsWith("look "),
		optionStartsWith("check-"), optionStartsWith("check "),
		hasAllOf(hasOption("search"), not(targetNamed("wiki"))),
		hasAllOf(hasOption("lookup"), not(targetNamed("wiki")), not(targetStartsWith("wiki ->")))
	),
	SKUll(SpriteID.HEADICONS_PK, "skull"),
	STATS(SpriteID.SideiconsInterface.STATS, "statistics", "stats", "view stats", "league-statistics", "lap-count"),
	TALK("talk", "talk", "talk-to", "talk to", "command", optionStartsWith("tell-")),
	TRAVEL("travel", "travel", "zanaris", "charter", "transport", "follow", "quick-travel", "fly",
		"glider", optionStartsWith("gilder-to"),
		optionStartsWith("last-destination"), optionStartsWith("charter-to")),
	UNCHARGE("uncharge", "uncharge", "unload"),
	UNLOCK("unlock", "unlock",
		// "Use foo key --> something" | "Use foo key bar --> something"
		hasAllOf(hasOption("use"), isWidgetTarget(), hasAnyOf(targetContains("key ->"), hasAllOf(targetContains(" key "), not(targetContains("half")), targetContains(" ->"))))),
	ROPE("untie", "tether", "tie-rope", isWidgetTargetOption("use", "rope")),
	USE("use", "use", "pet", "touch", "rub", "feel"),
	WIKI("wiki", "lookup-entity", targetNamed("wiki"), targetStartsWith("wiki ->")),

	// Sailing
	NAVIGATE(SpriteID.IconSailingFacilities24x24._4, hasAllOf(hasOption("navigate"), targetNamed("helm"))), // Ship's wheel
	SALVAGE(SpriteID.IconSailingFacilities24x24._5, "deploy"), // Salvage hook
	UNSET_SAILS(SpriteID.IconSailingFacilities24x24._0, hasAllOf(hasOption("un-set"), targetNamed("sails"))),
	SET_SAILS(SpriteID.IconSailingFacilities24x24._1, hasAllOf(hasOption("set"), targetNamed("sails"))),
	TRIM_SAILS(SpriteID.IconSailingFacilities24x24._2, hasAllOf(hasOption("trim"), isObject())), // Avoid trimmable capes
	CANNON(SpriteID.IconSailingFacilities24x24._6, "check-ammunition", "reset-ammunition",
		hasAllOf(targetEndsWith("cannon"), MenuEntryMatchers.hasOption("operate"))),
	TRAWLING_NET(SpriteID.IconSailingFacilities24x24._12, "raise",
		hasAllOf(targetEndsWith("trawling net"), MenuEntryMatchers.hasOption("operate"))),
	TRAWLING_NET_LOWER(SpriteID.IconSailingFacilities24x24._15, "lower"),
	CHUM_STATION(SpriteID.IconSailingFacilities24x24._16, hasAllOf(MenuEntryMatchers.hasOption("operate"),
		hasAnyOf(targetStartsWith("chum"), targetNamed("advanced chum station")))),
	WIND(SpriteID.IconSailingFacilities24x24._7, "release-mote",
		hasAllOf(hasOption("harvest"), targetNamed("crystal extractor"))),

	// PoH altar spellbooks
	ANCIENT_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_ANCIENT_MAGICKS, "ancient"),
	ARCEUUS_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_ARCEUUS, "arceuus"),
	// TODO: Magic cape spellbook swap
	STANDARD_SPELLBOOK(SpriteID.SideiconsInterface.MAGIC, hasAllOf(hasOption("standard"), hasAnyOf(targetEndsWith("altar"), targetStartsWith("altar")))), // Avoid DKs lair
	LUNAR_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_LUNAR, "lunar"),

	// Skills
	AGILITY(SpriteID.Staticons.AGILITY, "balance", "balance-across", "climb-across", "climb-on", "climb-over",
		"cross", "grab", "grapple", "hurdle", "jump", "kick", "leap", "shoot", "squeeze-past", "squeeze-through", "swing", "tap",
		"tag", "teeth-grip", "tread-softly", "vault", "walk-on", "walk-across", "crawl-through", "slide", "slide-along",
		optionStartsWith("jump-"),
		optionStartsWith("swing-"), optionStartsWith("swing "),
		hasAllOf(hasOption("navigate"), not(targetNamed("helm")))), // underwater agility obstacles
	ATTACK(SpriteID.Staticons.ATTACK, "attack", "hit"),
	CONSTRUCTION(SpriteID.Staticons2.CONSTRUCTION, "build", "modify", "upgrade", "build-trap",
		hasAllOf(hasOption("craft"), targetNamed("shipwrights' workbench"))),
	COOKING(SpriteID.Staticons.COOKING, "cook", "churn", "cook-at", "prepare-fish"),
	CRAFTING(SpriteID.Staticons.CRAFTING, "spin", "weave", "sing-crystal", "start-golem", "shape-golem", "insert-core",
		hasAllOf(hasOption("craft"), not(targetNamed("shipwrights' workbench"))), // crafting table / clockmaker's benches
		hasAllOf(hasOption("fire"), targetNamed("pottery oven"))),
	FARMING(SpriteID.Staticons2.FARMING, "check-health", "rake", "pick-fruit", "cure", "prune",
		hasAllOf(hasOption("harvest"), isObject(), not(targetNamed("crystal extractor"))), // Harvesting crops only
		hasAllOf(hasOption("collect"), targetEndsWith("coral")), // Collecting coral
		hasAllOf(optionStartsWith("pay"), isNpc()), // Paying farmers
		hasAllOf(hasAnyOf(hasOption("pick"), optionStartsWith("pick-")), isObject()), // Avoid zygomites, use pick-x for fruit trees
		hasAllOf(hasOption("tend-to"), targetStartsWith("flowering bush"))
	),
	FIREMAKING(SpriteID.Staticons.FIREMAKING, "light", "rub-together", "burn-down", "burn",
		hasAllOf(hasOption("feed"), targetNamed("brazier")), // avoid feeding duke
		hasAllOf(hasOption("tend-to"), targetEndsWith("campfire"))), // avoid flowering bushes
	FISHING(SpriteID.Staticons.FISHING, "net", "lure", "small net", "harpoon", "cage", "big net",
		"use-rod", "fish", "take-net", "fish-in", "rod-fish", hasAllOf(hasOption("bait"), isNpc())), // Bait fishing spots
	FLETCHING(SpriteID.Staticons.FLETCHING, "carve", "decorate", "fletch"),
	HERBLORE(SpriteID.Staticons.HERBLORE, "potions",
		hasAllOf(MenuEntryMatchers.hasOption("clean"), targetStartsWith("grimy"))),
	HUNTER(SpriteID.Staticons2.HUNTER, "catch", "lay", "set-trap", "fur clothing", "fur-clothing",
		"quick-falcon", "trap", "tease", "hide-in", "spear", "rumour",
		hasAllOf(hasOption("bait"), isObject())), // Crab traps
	MAGIC(SpriteID.Staticons.MAGIC, "spellbook", "teleport", "teleport menu", "study",
		hasAllOf(hasOption("venerate"), not(targetNamed("dark altar")))), // PoH spellbook altars
	MINING(SpriteID.Staticons.MINING, "mine", "smash-to-bits", "chip", "mine-through"),
	PRAYER(SpriteID.Staticons.PRAYER, "pray", "bury", "pray-at", "offer-fish", "scatter", "bask", "sacrifice",
		"worship", "bless", "recharge-prayer", "make-offering", "preach"),
	RANGED(SpriteID.Staticons.RANGED, "fire", "fire-at", "shoot-at"),
	RUNECRAFTING(SpriteID.Staticons2.RUNECRAFT, "craft-rune", "imbue", "reinvigorate",
		hasAllOf(hasOption("venerate"), targetNamed("dark altar"))), // Avoid PoH spellbook altars
	SMITHING(SpriteID.Staticons.SMITHING, "smelt", "smith", "hammer", "refine"),
	SLAYER(SpriteID.Staticons2.SLAYER, "assignment"),
	STRENGTH(SpriteID.Staticons.STRENGTH, "bang", "move"),
	THIEVING(SpriteID.Staticons.THIEVING, "pickpocket", "search for traps", "bribe", "disarm", "distract", "pick-pocket",
		optionStartsWith("steal-"), optionStartsWith("steal ")),
	WOODCUTTING(SpriteID.Staticons.WOODCUTTING, "chop down", "chop-down", "chop", "cut", "hack", "cut-down"),
	SAILING(SpriteID.Staticons2.SAILING, "board", "board-previous", "board-friend", "dock", "customise-boat",
		"recover-boat", "sort-salvage", "chart", "pry-open", "collect-data", "start-trial", "start-previous-rank",
		"manage-crew", "quick-board"),

	// Portal nexus + jewellery box destinations only - all other menu options covered by other cursors above
	TELEPORT_OBJECT("enter", hasAnyOf(targetNamed("portal nexus"), targetEndsWith("jewellery box"))),
	;

	@NonNull
	@Getter
	private final Sprite sprite;
	@Getter
	private final MenuEntryMatcher matcher;

	ContextualCursor(String cursorPath, Object... matchers)
	{
		this(new ResourceSprite(cursorPath), assembleMatcher(matchers));
	}

	ContextualCursor(int spriteID, Object... matchers)
	{
		this(new CacheSprite(spriteID), assembleMatcher(matchers));
	}

	ContextualCursor(@Nonnull Sprite sprite, MenuEntryMatcher matcher)
	{
		this.sprite = sprite;
		this.matcher = matcher;
	}

	private static MenuEntryMatcher assembleMatcher(Object... inputMatchers)
	{
		List<MenuEntryMatcher> matchers = new ArrayList<>(inputMatchers.length);

		for (Object matcher : inputMatchers)
		{
			if (matcher instanceof String)
			{
				matchers.add(hasOption((String) matcher));
			}
			else if (matcher instanceof MenuEntryMatcher)
			{
				matchers.add((MenuEntryMatcher) matcher);
			}
		}

		if (matchers.size() == 1)
		{
			return matchers.get(0);
		}

		return hasAnyOf(matchers.toArray(MenuEntryMatcher[]::new));
	}

	@NonNull
	public static ContextualCursorDefinition toCursorDefinition(boolean isOSRS)
	{
		return new ContextualCursorDefinition(
			List.of(values()),
			List.of(ItemCursor.values()),
			new OffsetCursor(new ResourceSprite("generic" + (isOSRS ? "_osrs" : "")), new ScaledPoint(0, 0)),
			new OffsetCursor(new ResourceSprite("blank" + (isOSRS ? "_osrs" : "")), new ScaledPoint(-5, 0)),
			new ScaledPoint(16, 18)
		);
	}
}
