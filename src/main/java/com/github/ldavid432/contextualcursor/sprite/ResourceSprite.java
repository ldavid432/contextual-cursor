package com.github.ldavid432.contextualcursor.sprite;

import javax.annotation.Nonnull;
import lombok.Value;

@Value
public class ResourceSprite implements Sprite
{
	@Nonnull
	String fileName;
	String type = "resource";
	boolean isInverted = false;
}
