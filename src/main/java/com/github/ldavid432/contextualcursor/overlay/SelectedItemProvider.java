package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import io.hydrox.contextualcursor.SpellSprite;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
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
	private final LoadingCache<Integer, Sprite> itemSpriteCache = CacheBuilder
		.newBuilder()
		.maximumSize(16)
		.build(
			new CacheLoader<>()
			{
				@Override
				public Sprite load(@Nonnull Integer integer)
				{
					return itemSprite().id(integer).build();
				}
			}
		);

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

			if (plugin.isShowUseItemCursorEnabled() && plugin.isPersistItems() && selectedWidget.getItemId() > 0)
			{
				try
				{
					log.debug("Persisting item {}", selectedWidget.getItemId());
					persistedSprite = itemSpriteCache.get(selectedWidget.getItemId());
				} catch (ExecutionException e)
				{
					log.error("Error persisting item", e);
				}
			}
			else if (plugin.isPersistSpells() &&
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
