package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.isSpell;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import io.hydrox.contextualcursor.SpellSprite;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.Text;

public class SpellCursor implements Cursor
{
	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)(?:</col>| -> )");

	@Override
	public Sprite getSprite(MenuEntry menuEntry)
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

	@Override
	public MenuEntryMatcher getMatcher()
	{
		return isSpell();
	}
}
