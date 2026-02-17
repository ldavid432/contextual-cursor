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

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAllOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isGroundItem;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isNpc;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isObject;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isSpell;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isWidgetTarget;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.not;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.optionIsAnyOf;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.optionStartsWith;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetEndsWith;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetNamed;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.targetStartsWith;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.SpriteID;
import net.runelite.client.util.Text;

@Slf4j
public enum ContextualCursor
{
	BANK("bank", optionIsAnyOf("bank", "coffer"),
		hasAllOf(hasOption("use"), targetNamed("bank chest"))),
	CLOSE("close", "close", "disembark"),
	CONFIGURE(SpriteID.OptionsIcons._51, "configure", "configuration"), // Wrench sprite
	DIG(Sprite.of("dig", true), optionIsAnyOf("dig", "clear", "dig-up")),
	DRINK("drink", "drink"),
	DROP("drop", "drop", "empty", "deposit", "quick-deposit", "deposit-cargo", "empty basket", "bank-cargo"),
	EAT("eat", "eat", "eat-from"),
	ENTER("enter", "climb-into", "enter", "exit", "yanille", "varrock", "seers' village", "camelot",
		"grand exchange", "watchtower", "go-through", "pass-through", "pass", "climb-through", "quick-enter"),
	EQUIP("equip", "wield", "wear", "equip"),
	EXCHANGE(SpriteID.GeSmallicons.GUIDE_PRICE, optionIsAnyOf("exchange", "trade", "trade with", "buy-boat"),
		hasAllOf(hasOption("collect"), not(targetEndsWith("coral")))),
	FRIEND(SpriteID.SideiconsInterface.FRIENDS, "add friend"),
	IGNORE(SpriteID.SideiconsInterface.IGNORES, "add ignore"),
	IMPOSSIBLE("impossible", "destroy", "stop-navigating", "cancel-task"),
	LADDER("ladder", "climb"),
	LADDER_DOWN("ladder_down", "climb-down", "climb down", "bottom-floor", "go-down"),
	LADDER_UP("ladder_up", "climb-up", "climb up", "top-floor"),
	OPEN("open", "open"),
	PICK_LOCK(Sprite.of("picklock", true), optionIsAnyOf("pick-lock", "picklock")),
	PICK_UP("pick_up", optionIsAnyOf("take", "withdraw", "fill", "collect-from", "pick-up"),
		hasAllOf(hasOption("harvest"), isNpc()),  // Various NPC corpses
		hasAllOf(optionIsAnyOf("search", "big-search"), targetStartsWith("reward")),  // various minigame reward objects
		optionStartsWith("take-")
	),
	PLANK("plank", "buy-plank"),
	READ("read", "read", "story", "guide"),
	REPORT(SpriteID.PvpwIcons.DEADMAN_EXCLAMATION_MARK_SKULLED_WARNING, "report"),
	SEARCH("search", optionIsAnyOf("examine", "view", "look-inside", "inspect", "investigate", "peek", "look-at"),
		hasAllOf(hasOption("check"), not(isGroundItem())), // Avoid hunter traps
		hasAllOf(hasOption("search"), not(targetNamed("wiki"))),
		hasAllOf(hasOption("lookup"), not(targetNamed("wiki")), not(targetStartsWith("wiki ->")))
	),
	SKUll(SpriteID.HEADICONS_PK, "skull"),
	TALK("talk", "talk", "talk-to", "talk to", "command"),
	TRAVEL(Sprite.of("travel", true), hasAnyOf(optionIsAnyOf("travel", "zanaris", "charter"),
		optionStartsWith("last-destination"), optionStartsWith("charter-to"))),
	UNTIE("untie", hasOption("tether"), isWidgetTarget("use", "rope")),
	USE("use", "use", "pet"),
	WIKI("wiki", hasOption("lookup-entity"), targetNamed("wiki"), targetStartsWith("wiki ->")),

	// Sailing
	NAVIGATE(SpriteID.IconSailingFacilities24x24._4, hasAllOf(hasOption("navigate"), targetNamed("helm"))), // Ship's wheel
	SALVAGE(SpriteID.IconSailingFacilities24x24._5, "deploy"), // Salvage hook
	UNSET_SAILS(SpriteID.IconSailingFacilities24x24._0, hasAllOf(hasOption("un-set"), targetNamed("sails"))),
	SET_SAILS(SpriteID.IconSailingFacilities24x24._1, hasAllOf(hasOption("set"), targetNamed("sails"))),
	TRIM_SAILS(SpriteID.IconSailingFacilities24x24._2, hasAllOf(hasOption("trim"), isObject())), // Avoid trimmable capes
	CANNON(SpriteID.IconSailingFacilities24x24._6, optionIsAnyOf("check-ammunition", "reset-ammunition"),
		hasAllOf(targetEndsWith("cannon"), optionIsAnyOf("operate"))),
	TRAWLING_NET(SpriteID.IconSailingFacilities24x24._12, optionIsAnyOf("raise"),
		hasAllOf(targetEndsWith("trawling net"), optionIsAnyOf("operate"))),
	TRAWLING_NET_LOWER(SpriteID.IconSailingFacilities24x24._15, "lower"),
	CHUM_STATION(SpriteID.IconSailingFacilities24x24._16, hasAllOf(optionIsAnyOf("operate"),
		hasAnyOf(targetStartsWith("chum"), targetNamed("advanced chum station")))),
	WIND(SpriteID.IconSailingFacilities24x24._7, optionIsAnyOf("release-mote"),
		hasAllOf(hasOption("harvest"), targetNamed("crystal extractor"))),

	// PoH altar spellbooks
	ANCIENT_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_ANCIENT_MAGICKS, "ancient"),
	ARCEUUS_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_ARCEUUS, "arceuus"),
	STANDARD_SPELLBOOK(SpriteID.SideiconsInterface.MAGIC, hasAllOf(hasOption("standard"), targetEndsWith("altar"))), // Avoid DKs lair
	LUNAR_SPELLBOOK(SpriteID.SideiconsInterface.SPELLBOOK_LUNAR, "lunar"),

	// Skills
	AGILITY(SpriteID.Staticons.AGILITY, optionIsAnyOf("balance", "balance-across", "climb-across", "climb-on", "climb-over",
		"cross", "grab", "grapple", "hurdle", "jump", "jump-up", "jump-to", "jump-off", "jump-in", "jump-on", "kick",
		"leap", "shoot", "squeeze-past", "squeeze-through", "swing", "swing across", "swing-across", "swing-on", "tap",
		"tag", "teeth-grip", "tread-softly", "vault", "walk-on", "walk-across", "crawl-through", "jump-over", "escape"),
		hasAllOf(hasOption("navigate"), not(targetNamed("helm")))), // underwater agility obstacles
	ATTACK(SpriteID.Staticons.ATTACK, "attack"),
	CONSTRUCTION(SpriteID.Staticons2.CONSTRUCTION, optionIsAnyOf("build", "remove", "modify", "upgrade", "build-trap"),
		hasAllOf(hasOption("craft"), targetNamed("shipwrights' workbench"))),
	COOKING(SpriteID.Staticons.COOKING, "cook", "churn", "cook-at", "prepare-fish"),
	CRAFTING(SpriteID.Staticons.CRAFTING, optionIsAnyOf("spin", "weave"),
		hasAllOf(hasOption("craft"), not(targetNamed("shipwrights' workbench")))), // crafting table / clockmaker's benches
	FARMING(SpriteID.Staticons2.FARMING, optionIsAnyOf("check-health", "rake", "pick-fruit", "pay", "cure", "prune"),
		hasAllOf(hasOption("harvest"), isObject(), not(targetNamed("crystal extractor"))), // Harvesting crops only
		hasAllOf(hasOption("collect"), targetEndsWith("coral")), // Collecting coral
		hasAllOf(optionStartsWith("pay"), isNpc()), // Paying farmers
		hasAllOf(hasAnyOf(hasOption("pick"), optionStartsWith("pick-")), isObject()) // Avoid zygomites, use pick-x for fruit trees
	),
	FIREMAKING(SpriteID.Staticons.FIREMAKING, "light", "feed"),
	FISHING(SpriteID.Staticons.FISHING, optionIsAnyOf("net", "lure", "small net", "harpoon", "cage", "big net",
		"use-rod", "fish", "take-net"), hasAllOf(hasOption("bait"), isNpc())), // Bait fishing spots
	FLETCHING(SpriteID.Staticons.FLETCHING, "carve", "decorate"),
	HERBLORE(SpriteID.Staticons.HERBLORE, hasAllOf(optionIsAnyOf("clean"), targetStartsWith("grimy"))),
	HUNTER(SpriteID.Staticons2.HUNTER, optionIsAnyOf("catch", "lay", "dismantle", "reset", "set-trap"),
		hasAllOf(hasOption("check"), isGroundItem()), // Various hunter traps
		hasAllOf(hasOption("bait"), isObject())), // Crab traps
	MAGIC(SpriteID.Staticons.MAGIC, optionIsAnyOf("spellbook", "teleport", "teleport menu", "study"),
		hasAllOf(hasOption("venerate"), not(targetNamed("dark altar")))), // PoH spellbook altars
	MINING(SpriteID.Staticons.MINING, "mine", "smash-to-bits", "chip"),
	PRAYER(SpriteID.Staticons.PRAYER, "pray", "bury", "pray-at", "offer-fish", "scatter"),
	RANGED(SpriteID.Staticons.RANGED, "fire", "fire-at"),
	RUNECRAFTING(SpriteID.Staticons2.RUNECRAFT, optionIsAnyOf("craft-rune", "imbue", "reinvigorate"),
		hasAllOf(hasOption("venerate"), targetNamed("dark altar"))), // Avoid PoH spellbook altars
	SMITHING(SpriteID.Staticons.SMITHING, "smelt", "smith", "hammer", "refine"),
	SLAYER(SpriteID.Staticons2.SLAYER, "assignment"),
	STRENGTH(SpriteID.Staticons.STRENGTH, "bang", "move"),
	THIEVING(SpriteID.Staticons.THIEVING, "steal-from", "pickpocket", "search for traps", "bribe"),
	WOODCUTTING(SpriteID.Staticons.WOODCUTTING, "chop down", "chop-down", "chop", "cut", "hack"),
	SAILING(SpriteID.Staticons2.SAILING, "board", "board-previous", "board-friend", "dock", "customise-boat",
		"recover-boat", "sort-salvage", "chart", "pry-open", "collect-data", "start-trial", "start-previous-rank", "manage-crew"),

	SPELL(null, isSpell())
		{
			@Override
			protected Sprite getSprite(MenuEntry menuEntry)
			{
				final Matcher spellFinder = SPELL_FINDER.matcher(menuEntry.getTarget().toLowerCase());

				if (!spellFinder.find())
				{
					return null;
				}

				final String spellText = spellFinder.group(1);
				final SpellSprite spell = SpellSprite.get(Text.sanitize(spellText));
				if (spell == null)
				{
					return null;
				}

				return spell.getSprite();
			}
		},
	;

	private final Sprite sprite;
	@Getter
	private final MenuEntryMatcher matcher;

	// Basic cursor with only global actions
	ContextualCursor(String cursorPath, String... actions)
	{
		this(Sprite.of(cursorPath), optionIsAnyOf(actions));
	}

	// Basic cursor with only global actions
	ContextualCursor(int spriteID, String... actions)
	{
		this(Sprite.of(spriteID), optionIsAnyOf(actions));
	}

	// Cursor with specific matchers
	ContextualCursor(String cursorPath, MenuEntryMatcher... matchers)
	{
		this(Sprite.of(cursorPath), hasAnyOf(matchers));
	}

	// Cursor with specific matchers
	ContextualCursor(int spriteID, MenuEntryMatcher... matchers)
	{
		this(Sprite.of(spriteID), hasAnyOf(matchers));
	}

	ContextualCursor(Sprite sprite, MenuEntryMatcher matcher)
	{
		this.sprite = sprite;
		this.matcher = matcher;
	}

	protected Sprite getSprite(MenuEntry menuEntry)
	{
		return sprite;
	}

	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)(?:</col>| -> )");
	private static final ContextualCursor[] values = values();

	public static Sprite get(MenuEntry menuEntry)
	{
		for (ContextualCursor cursor : values)
		{
			if (cursor.matcher.matches(menuEntry))
			{
				return cursor.getSprite(menuEntry);
			}
		}
		return null;
	}

	static final ResourceSprite BLANK_CURSOR = new ResourceSprite("blank");
	static final ResourceSprite GENERIC_CURSOR = new ResourceSprite("generic");

	static void clearImages()
	{
		for (ContextualCursor cursor : values)
		{
			if (cursor.sprite != null)
			{
				cursor.sprite.clearImage();
			}
		}

		BLANK_CURSOR.clearImage();
		GENERIC_CURSOR.clearImage();
	}
}
