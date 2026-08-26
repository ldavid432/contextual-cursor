package com.github.ldavid432.contextualcursor.overlay;

import static com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers.hasOption;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import io.hydrox.contextualcursor.SpellSprite;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import net.runelite.api.MenuEntry;
import net.runelite.client.util.Text;

@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SpellProvider
{
	private static final Pattern SPELL_FINDER = Pattern.compile(">(.*?)(?:</col>| -> )");

	@Nullable
	public Sprite getSpellSprite(@Nullable MenuEntry menuEntry)
	{
		if (menuEntry == null || !hasOption("cast", "resurrect", "reanimate").matches(menuEntry))
		{
			return null;
		}

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
}
