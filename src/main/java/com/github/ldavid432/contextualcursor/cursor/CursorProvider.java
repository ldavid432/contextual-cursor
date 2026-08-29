package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import javax.inject.Singleton;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;

@Slf4j
@Singleton
public class CursorProvider implements ProviderCallbacks
{
	@Setter
	@Delegate
	private ContextualCursorDefinition definition;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks()
	{
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			if (definition != null)
			{
				definition.updateScale(cursorScale);
			}
		}
	};

	public Sprite getSprite(MenuEntry menuEntry)
	{
		for (Cursor cursor : getCursors())
		{
			if (cursor.getMatcher().matches(menuEntry))
			{
				return cursor.getSprite();
			}
		}
		return null;
	}

}
