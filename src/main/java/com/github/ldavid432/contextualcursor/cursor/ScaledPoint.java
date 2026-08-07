package com.github.ldavid432.contextualcursor.cursor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScaledPoint {
	private final int x;
	private final int y;

	private transient int scaledX;
	private transient int scaledY;

	public int getX()
	{
		return scaledX;
	}

	public int getY()
	{
		return scaledY;
	}
	
	public void updateScale(double scale)
	{
		scaledX = (int) (x * scale);
		scaledY = (int) (y * scale);
	}
}
