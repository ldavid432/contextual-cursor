package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.sprite.CacheSprite;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpriteAdapter implements JsonDeserializer<Sprite>
{

	@Override
	public Sprite deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		JsonObject object = jsonElement.getAsJsonObject();
		String spriteType = object.get("type").getAsString();

		switch (spriteType)
		{
			case "resource":
				return context.deserialize(object, ResourceSprite.class);
			case "cache":
				return context.deserialize(object, CacheSprite.class);
			default:
				log.error("Invalid sprite type");
		}

		return null;
	}
}
