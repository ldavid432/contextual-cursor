package com.github.ldavid432.contextualcursor.config;

import com.github.ldavid432.contextualcursor.ContextualCursorUtil;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.image.BufferedImage;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.util.ImageUtil;

@Getter
@AllArgsConstructor
public enum CursorTheme
{
	OSRS
		{
			@Nonnull
			@Override
			public BufferedImage getImage(String fileName)
			{
				String path = String.format("cursors/%s_osrs.png", fileName);
				if (ContextualCursorPlugin.class.getResource(path) != null)
				{
					return ImageUtil.loadImageResource(ContextualCursorPlugin.class, path);
				}
				return ContextualCursorUtil.loadImage(fileName);
			}

			@Override
			public String toString()
			{
				return "OldSchool";
			}
		},
	RS2
		{
			@Nonnull
			@Override
			public BufferedImage getImage(String fileName)
			{
				return ContextualCursorUtil.loadImage(fileName);
			}

			@Override
			public String toString()
			{
				return "RuneScape 2";
			}
		},
	;

	@Nonnull
	public abstract BufferedImage getImage(String fileName);

}
