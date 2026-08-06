package com.github.ldavid432.contextualcursor.sprite;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;

@Getter
@Singleton
@AllArgsConstructor(onConstructor_ = @Inject)
public class SpriteContext
{
	private final Client client;
	private final SpriteManager spriteManager;
	private final ItemManager itemManager;
	private final ContextualCursorCache cache;
}
