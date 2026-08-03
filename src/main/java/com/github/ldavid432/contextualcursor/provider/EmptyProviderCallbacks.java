package com.github.ldavid432.contextualcursor.provider;

import com.github.ldavid432.contextualcursor.config.CursorTheme;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmptyProviderCallbacks implements ProviderCallbacks
{

	@Override
	public void onThemeChange(CursorTheme theme)
	{
	}

	@Override
	public void onScaleChange(double cursorScale, double itemScale)
	{
	}

	@Override
	public void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing)
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
