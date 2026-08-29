package com.github.ldavid432.contextualcursor.sprite;

import lombok.Value;

@Value
public class ItemSprite implements Sprite
{
	int id;
	int quantity = 1;
	String type = "item";
	boolean isInverted = false;
}
