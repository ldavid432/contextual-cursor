package com.github.ldavid432.contextualcursor.sprite;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scaleImage;
import com.github.ldavid432.contextualcursor.config.CursorTheme;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

@SuperBuilder
@EqualsAndHashCode(of = "fileName", callSuper = false)
public class ResourceSprite extends BaseSprite
{
	@Nonnull
	private final String fileName;

	@Override
	protected BufferedImage getBaseImage(Client client, SpriteManager spriteManager, CursorTheme theme)
	{
		return theme.getImage(fileName);
	}

	// These are used for the default cursors

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

}
