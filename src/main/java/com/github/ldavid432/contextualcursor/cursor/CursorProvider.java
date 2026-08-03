package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import javax.inject.Singleton;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;

@Slf4j
@Singleton
public class CursorProvider implements ProviderCallbacks
{
	@Delegate
	private ContextualCursorDefinition definition;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks()
	{
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			clearImages();
		}

		@Override
		public void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing)
		{
			clearImages();
		}

		@Override
		public void onThemeChange(CursorTheme theme)
		{
			clearImages();
		}

		@Override
		public void onShutdown()
		{
			clearImages();
		}
	};

	public void clearImages()
	{
		if (definition != null)
		{
			definition.clearImages();
		}
	}

	public void setDefinition(ContextualCursorDefinition definition)
	{
		if (this.definition != null)
		{
			clearImages();
		}
		this.definition = definition;
	}

	public Sprite getSprite(MenuEntry menuEntry)
	{
		for (Cursor cursor : getCursors())
		{
			if (cursor.getMatcher().matches(menuEntry))
			{
				return cursor.getSprite(menuEntry);
			}
		}
		return null;
	}

}
