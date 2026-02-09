package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

@EqualsAndHashCode(of = "fileName")
@RequiredArgsConstructor
public class ResourceSprite implements Sprite
{
	@Nonnull
	private final String fileName;

	@Nullable
	private BufferedImage image;

	@Getter
	private final boolean isFullCursor;

	public ResourceSprite(@Nonnull String fileName)
	{
		this.fileName = fileName;
		this.isFullCursor = false;
	}

	@Nullable
	@Override
	public BufferedImage getImage(Client client, SpriteManager spriteManager, CursorTheme theme)
	{
		return getImage(theme);
	}

	@Nullable
	public BufferedImage getImage(CursorTheme theme)
	{
		if (image == null)
		{
			image = theme.getImage(fileName);
		}
		return image;
	}

	@Override
	public void clearImage()
	{
		image = null;
	}

}
