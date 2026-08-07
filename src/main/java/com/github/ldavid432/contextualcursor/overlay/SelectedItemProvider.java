package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.ItemSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import static com.github.ldavid432.contextualcursor.sprite.Sprite.itemSprite;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.hydrox.contextualcursor.SpellSprite;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedItemProvider implements ProviderCallbacks
{
	private final Client client;
	private final ContextualCursorCache cache;

	private Sprite persistedSprite = null;
	private final LoadingCache<Integer, ItemSprite> itemSpriteCache = CacheBuilder
		.newBuilder()
		.maximumSize(16)
		.build(
			new CacheLoader<>()
			{
				@Override
				public ItemSprite load(@Nonnull Integer integer)
				{
					return itemSprite().id(integer).build();
				}
			}
		);

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks()
	{
		@Override
		public void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing)
		{
			itemSpriteCache.invalidateAll();
			if (persistedSprite != null)
			{
				persistedSprite.clearImage();
			}
		}

		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			itemSpriteCache.invalidateAll();
			if (persistedSprite != null)
			{
				persistedSprite.clearImage();
			}
		}
	};

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
				try
				{
					log.debug("Persisting item {}", selectedWidget.getItemId());
					persistedSprite = itemSpriteCache.get(selectedWidget.getItemId());
				}
				catch (ExecutionException e)
				{
					log.error("Error persisting item", e);
				}
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
