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

import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.gameval.SpriteID;

@AllArgsConstructor
public enum SpellSprite
{
	// Standard
	WIND_STRIKE(SpriteID.Magicon.WIND_STRIKE),
	CONFUSE(SpriteID.Magicon.CONFUSE),
	WATER_STRIKE(SpriteID.Magicon.WATER_STRIKE),
	LVL_1_ENCHANT("lvl-1 enchant", SpriteID.Magicon.LVL_1_ENCHANT),
	EARTH_STRIKE(SpriteID.Magicon.EARTH_STRIKE),
	WEAKEN(SpriteID.Magicon.WEAKEN),
	FIRE_STRIKE(SpriteID.Magicon.FIRE_STRIKE),
	WIND_BOLT(SpriteID.Magicon.WIND_BOLT),
	CURSE(SpriteID.Magicon.CURSE),
	BIND(SpriteID.Magicon2.BIND),
	LOW_LEVEL_ALCHEMY(SpriteID.Magicon.LOW_LEVEL_ALCHEMY),
	WATER_BOLT(SpriteID.Magicon.WATER_BOLT),
	LVL_2_ENCHANT("lvl-2 enchant", SpriteID.Magicon.LVL_2_ENCHANT),
	EARTH_BOLT(SpriteID.Magicon.EARTH_BOLT),
	TELEKINETIC_GRAB(SpriteID.Magicon.TELEKINETIC_GRAB),
	FIRE_BOLT(SpriteID.Magicon.FIRE_BOLT),
	CRUMBLE_UNDEAD(SpriteID.Magicon.CRUMBLE_UNDEAD),
	WIND_BLAST(SpriteID.Magicon.WIND_BLAST),
	SUPERHEAT_ITEM(SpriteID.Magicon.SUPERHEAT_ITEM),
	WATER_BLAST(SpriteID.Magicon.WATER_BLAST),
	LVL_3_ENCHANT("lvl-3 enchant", SpriteID.Magicon.LVL_3_ENCHANT),
	IBAN_BLAST(SpriteID.Magicon.IBAN_BLAST),
	SNARE(SpriteID.Magicon2.SNARE),
	MAGIC_DART(SpriteID.Magicon2.MAGIC_DART),
	EARTH_BLAST(SpriteID.Magicon.EARTH_BLAST),
	HIGH_LEVEL_ALCHEMY(SpriteID.Magicon.HIGH_LEVEL_ALCHEMY),
	CHARGE_WATER_ORB(SpriteID.Magicon.CHARGE_WATER_ORB),
	LVL_4_ENCHANT("lvl-4 enchant", SpriteID.Magicon.LVL_4_ENCHANT),
	FIRE_BLAST(SpriteID.Magicon.FIRE_BLAST),
	CHARGE_EARTH_ORB(SpriteID.Magicon.CHARGE_EARTH_ORB),
	SARADOMIN_STRIKE(SpriteID.Magicon.SARADOMIN_STRIKE),
	CLAWS_OF_GUTHIX(SpriteID.Magicon.CLAWS_OF_GUTHIX),
	FLAMES_OF_ZAMORAK(SpriteID.Magicon.FLAMES_OF_ZAMORAK),
	WIND_WAVE(SpriteID.Magicon.WIND_WAVE),
	CHARGE_FIRE_ORB(SpriteID.Magicon.CHARGE_FIRE_ORB),
	WATER_WAVE(SpriteID.Magicon.WATER_WAVE),
	CHARGE_AIR_ORB(SpriteID.Magicon.CHARGE_AIR_ORB),
	VULNERABILITY(SpriteID.Magicon.VULNERABILITY),
	LVL_5_ENCHANT("lvl-5 enchant", SpriteID.Magicon.LVL_5_ENCHANT),
	EARTH_WAVE(SpriteID.Magicon.EARTH_WAVE),
	ENFEEBLE(SpriteID.Magicon.ENFEEBLE),
	TELEOTHER_LUMBRIDGE(SpriteID.Magicon2.TELEOTHER_LUMBRIDGE),
	FIRE_WAVE(SpriteID.Magicon.FIRE_WAVE),
	ENTANGLE(SpriteID.Magicon2.ENTANGLE),
	STUN(SpriteID.Magicon.STUN),
	WIND_SURGE(SpriteID.Magicon2.WIND_SURGE),
	TELEOTHER_FALADOR(SpriteID.Magicon2.TELEOTHER_FALADOR),
	WATER_SURGE(SpriteID.Magicon2.WATER_SURGE),
	TELE_BLOCK(SpriteID.Magicon2.TELE_BLOCK),
	LVL_6_ENCHANT("lvl-6 enchant", SpriteID.Magicon2.LVL_6_ENCHANT),
	TELEOTHER_CAMELOT(SpriteID.Magicon2.TELEOTHER_CAMELOT),
	EARTH_SURGE(SpriteID.Magicon2.EARTH_SURGE),
	LVL_7_ENCHANT("lvl-7 enchant", SpriteID.Magicon2.LVL_7_ENCHANT),
	FIRE_SURGE(SpriteID.Magicon2.FIRE_SURGE),
	// Ancients
	SMOKE_RUSH(SpriteID.Magicon2.SMOKE_RUSH),
	SHADOW_RUSH(SpriteID.Magicon2.SHADOW_RUSH),
	BLOOD_RUSH(SpriteID.Magicon2.BLOOD_RUSH),
	ICE_RUSH(SpriteID.Magicon2.ICE_RUSH),
	SMOKE_BURST(SpriteID.Magicon2.SMOKE_BURST),
	SHADOW_BURST(SpriteID.Magicon2.SHADOW_BURST),
	BLOOD_BURST(SpriteID.Magicon2.BLOOD_BURST),
	ICE_BURST(SpriteID.Magicon2.ICE_BURST),
	SMOKE_BLITZ(SpriteID.Magicon2.SMOKE_BLITZ),
	SHADOW_BLITZ(SpriteID.Magicon2.SHADOW_BLITZ),
	BLOOD_BLITZ(SpriteID.Magicon2.BLOOD_BLITZ),
	ICE_BLITZ(SpriteID.Magicon2.ICE_BLITZ),
	SMOKE_BARRAGE(SpriteID.Magicon2.SMOKE_BARRAGE),
	SHADOW_BARRAGE(SpriteID.Magicon2.SHADOW_BARRAGE),
	BLOOD_BARRAGE(SpriteID.Magicon2.BLOOD_BARRAGE),
	ICE_BARRAGE(SpriteID.Magicon2.ICE_BARRAGE),
	// Lunars
	CURE_PLANT(SpriteID.LunarMagicOn.CURE_PLANT),
	MONSTER_EXAMINE(SpriteID.LunarMagicOn.MONSTER_EXAMINE),
	STAT_SPY(SpriteID.LunarMagicOn.STAT_SPY),
	FERTILE_SOIL(SpriteID.LunarMagicOn.FERTILE_SOIL),
	PLANK_MAKE(SpriteID.LunarMagicOn.PLANK_MAKE),
	CURE_OTHER(SpriteID.LunarMagicOn.CURE_OTHER),
	STAT_RESTORE_POT_SHARE(SpriteID.LunarMagicOn.STAT_RESTORE_POT_SHARE),
	BOOST_POTION_SHARE(SpriteID.LunarMagicOn.BOOST_POTION_SHARE),
	ENERGY_TRANSFER(SpriteID.LunarMagicOn.ENERGY_TRANSFER),
	HEAL_OTHER(SpriteID.LunarMagicOn.HEAL_OTHER),
	VENGEANCE_OTHER(SpriteID.LunarMagicOn.VENGEANCE_OTHER),
	// Arceuus
	BASIC_REANIMATION(SpriteID.MagicNecroOn.BASIC_REANIMATION),
	ADEPT_REANIMATION(SpriteID.MagicNecroOn.ADEPT_REANIMATION),
	EXPERT_REANIMATION(SpriteID.MagicNecroOn.EXPERT_REANIMATION),
	MASTER_REANIMATION(SpriteID.MagicNecroOn.MASTER_REANIMATION),
	RESURRECT_CROPS(SpriteID.MagicNecroOn.RESURRECT_CROPS),
	DARK_LURE(SpriteID.MagicNecroOn.DARK_LURE),
	MARK_OF_DARKNESS(SpriteID.MagicNecroOn.MARK_OF_DARKNESS),
	GHOSTLY_GRASP(SpriteID.MagicNecroOn.GHOSTLY_GRASP),
	SKELETAL_GRASP(SpriteID.MagicNecroOn.SKELETAL_GRASP),
	UNDEAD_GRASP(SpriteID.MagicNecroOn.UNDEAD_GRASP),
	INFERIOR_DEMONBANE(SpriteID.MagicNecroOn.INFERIOR_DEMONBANE),
	SUPERIOR_DEMONBANE(SpriteID.MagicNecroOn.SUPERIOR_DEMONBANE),
	DARK_DEMONBANE(SpriteID.MagicNecroOn.DARK_DEMONBANE);

	private final String name;
	final int spriteID;

	SpellSprite(int spriteID)
	{
		this(null, spriteID);
	}

	private String getName()
	{
		return name != null ? name : this.name().toLowerCase().replace("_", " ");
	}

	private static final Map<String, SpellSprite> map = new HashMap<>();

	static
	{
		for (SpellSprite spell : values())
		{
			map.put(spell.getName(), spell);
		}
	}

	static SpellSprite get(String spell)
	{
		return map.get(spell);
	}
}
