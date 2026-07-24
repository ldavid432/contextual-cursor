package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedItemProvider
{
	private final Client client;
	private final ContextualCursorPlugin plugin;

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

			// TODO: Separate these two booleans, theoretically we should grab the sprite from the plugin when isShowUseItemCursorEnabled is on? - if other item sprites are added then isPersistItems grab find those too
			if (plugin.isShowUseItemCursorEnabled() && plugin.isPersistItems() && selectedWidget.getItemId() > 0)
			{
				log.debug("Persisting item {}", selectedWidget.getItemId());
				// TODO: Item sprite cache
				persistedSprite = itemSprite().id(selectedWidget.getItemId()).build();
			}
			else if (plugin.isPersistSpells() &&
				selectedWidget.getParent() != null &&
				selectedWidget.getParent().getId() == InterfaceID.MagicSpellbook.SPELLLAYER)
			{
				log.debug("Persisting spell");
				// (Theoretically) The instant a spell is selected the sprite should be the spell sprite so we can just use getSpriteToDraw
				//  Otherwise we would have to add logic to match the widget sprite ID to the spell which isn't ideal since spells have multiple sprites with different resolutions
				persistedSprite = plugin.getSpriteToDraw();
			}
		}
	}
}
