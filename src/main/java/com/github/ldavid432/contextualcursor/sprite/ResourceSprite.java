package com.github.ldavid432.contextualcursor.sprite;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
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
	public BufferedImage getImage(Client client, SpriteManager spriteManager, CursorTheme theme, double scale, boolean isSmoothScaling)
	{
		return getImage(theme, scale, isSmoothScaling);
	}

	@Nullable
	public BufferedImage getImage(CursorTheme theme, double scale, boolean isSmoothScaling)
	{
		if (image == null)
		{
			image = theme.getImage(fileName);
			image = scaleImage(image, scale, isSmoothScaling);
		}
		return image;
	}

	@Nullable
	public BufferedImage getImage(ContextualCursorPlugin plugin)
	{
		return getImage(plugin.getCursorTheme(), plugin.getCursorScale(), plugin.isSmoothScalingEnabled());
	}

	@Override
	public void clearImage()
	{
		image = null;
	}

}
