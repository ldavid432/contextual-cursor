package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.sprite.ItemSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import io.hydrox.contextualcursor.SpellSprite;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedWidgetProvider
{
	private final Client client;
	private final ContextualCursorCache cache;

	private Sprite persistedSprite = null;

	@Nullable
	public Sprite getSelectedSprite()
	{
		if (client.isWidgetSelected())
		{
			processSelected();
		}
		else if (persistedSprite != null)
		{
			persistedSprite = null;
		}
		return persistedSprite;
	}

	private void processSelected()
	{
		if (persistedSprite == null)
		{
			Widget selectedWidget = client.getSelectedWidget();
			if (selectedWidget == null)
			{
				persistedSprite = null;
				return;
			}

			if (cache.isShowUseItemCursorEnabled() && cache.isPersistItems() && selectedWidget.getItemId() > 0)
			{
				log.debug("Persisting item {}", selectedWidget.getItemId());
				persistedSprite = new ItemSprite(selectedWidget.getItemId());
			}
			else if (cache.isPersistSpells() &&
				selectedWidget.getParent() != null &&
				selectedWidget.getParent().getId() == InterfaceID.MagicSpellbook.SPELLLAYER)
			{
				for (SpellSprite spell : SpellSprite.values())
				{
					if (spell.getInterfaceID() == selectedWidget.getId())
					{
						log.debug("Persisting spell");
						persistedSprite = spell.getSprite();
						break;
					}
				}
			}
		}
	}
}
