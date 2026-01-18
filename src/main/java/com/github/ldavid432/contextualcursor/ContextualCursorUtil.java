package com.github.ldavid432.contextualcursor;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Image;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;

public class ContextualCursorUtil
{
	public static Image scaleImage(BufferedImage image, ContextualCursorPlugin plugin)
	{
		return scaleImage(image, plugin.getCursorScale(), plugin.isSmoothScalingEnabled());
	}

	private static Image scaleImage(BufferedImage image, double scale, boolean smoothScaling)
	{
		if (scale == 1.0)
		{
			return image;
		}
		else
		{
			return image.getScaledInstance(
				(int) (image.getWidth() * scale),
				(int) (image.getHeight() * scale),
				smoothScaling ? Image.SCALE_SMOOTH : Image.SCALE_FAST
			);
		}
	}

	public static Point scalePoint(Point point, double scale)
	{
		if (scale == 1.0)
		{
			return point;
		}
		else
		{
			return new Point((int) (point.getX() * scale), (int) (point.getY() * scale));
		}
	}
}
