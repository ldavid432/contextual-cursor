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
	OSRS("OldSchool")
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
				return super.getImage(fileName);
			}
		},
	RS2("RuneScape 2"),
	// TODO: Minimal theme with no background
	;

	private final String displayText;

	@Nonnull
	public BufferedImage getImage(String fileName)
	{
		return ContextualCursorUtil.loadImage(fileName);
	}

	@Override
	public String toString()
	{
		return displayText;
	}
}
