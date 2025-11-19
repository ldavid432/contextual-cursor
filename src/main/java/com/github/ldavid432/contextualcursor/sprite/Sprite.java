package com.github.ldavid432.contextualcursor.sprite;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;

public interface Sprite
{
	@Nullable
	BufferedImage getImage(Client client, SpriteManager spriteManager);
}
