package com.github.ldavid432.contextualcursor.provider;

import com.github.ldavid432.contextualcursor.config.CursorTheme;

public interface ProviderCallbacks
{
	void onThemeChange(CursorTheme theme);
	void onScaleChange(double cursorScale, double itemScale);
	void onScaleSmoothingChange(boolean cursorSmoothing, boolean itemSmoothing);
}
