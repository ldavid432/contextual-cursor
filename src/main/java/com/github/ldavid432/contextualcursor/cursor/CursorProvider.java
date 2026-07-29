package com.github.ldavid432.contextualcursor.cursor;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.github.ldavid432.contextualcursor.sprite.SpriteContext;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.BLANK_CURSOR_NAME;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.GENERIC_CURSOR_NAME;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.ClientUI;

@Slf4j
@Singleton
public class CursorProvider implements ProviderCallbacks
{
	@Delegate
	private ContextualCursorDefinition definition;

	@Inject
	private ClientUI clientUI;

	@Inject
	private SpriteContext spriteContext;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks() {
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
	};

	// TODO: Move awt cursor logic out?
	private java.awt.Cursor defaultCursor = null;

	@Getter
	@Setter
	private java.awt.Cursor lastCustomCursor = null;

	public void clearImages()
	{
		if (definition != null)
		{
			definition.clearImages();
		}
		defaultCursor = null;
	}

	public java.awt.Cursor getDefaultCursor()
	{
		if (defaultCursor == null)
		{
			defaultCursor = createGenericCursor();
		}
		return defaultCursor;
	}

	private java.awt.Cursor createGenericCursor()
	{
		BufferedImage icon = getDefaultCursorSprite().getImage(spriteContext);
		BufferedImage result = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = result.createGraphics();
		g.drawImage(icon, 0, 0, null);
		g.dispose();

		return Toolkit.getDefaultToolkit().createCustomCursor(
			result,
			new java.awt.Point(0, 0),
			GENERIC_CURSOR_NAME
		);
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

	private boolean isNotIgnoredCursor(java.awt.Cursor cursor)
	{
		return !cursor.getName().equals(BLANK_CURSOR_NAME) && !cursor.getName().equals(GENERIC_CURSOR_NAME);
	}

	public void checkLastCursor()
	{
		java.awt.Cursor currentCursor = clientUI.getCurrentCursor();

		if (isNotIgnoredCursor(currentCursor) && currentCursor.getType() == java.awt.Cursor.CUSTOM_CURSOR && lastCustomCursor != currentCursor)
		{
			log.debug("Setting last default cursor: {}", currentCursor.getName());
			lastCustomCursor = currentCursor;
		}
	}

}
