package com.github.ldavid432.contextualcursor.config;

import java.awt.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScaleMethod
{
	Fast(Image.SCALE_FAST),
	Smooth(Image.SCALE_SMOOTH),
	;

	private final int flag;
}
