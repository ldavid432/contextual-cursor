package com.github.ldavid432.contextualcursor.sprite;

import lombok.Value;

@Value
public class CacheSprite implements Sprite
{
	int id;
	String type = "cache";
	boolean isInverted = false;
}
