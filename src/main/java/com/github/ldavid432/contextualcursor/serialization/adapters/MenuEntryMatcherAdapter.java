package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleStringMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.StaticMatcher;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MenuEntryMatcherAdapter implements JsonSerializer<MenuEntryMatcher>, JsonDeserializer<MenuEntryMatcher>
{
	@Override
	public MenuEntryMatcher deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		MenuEntryMatcher matcher = null;

		if (jsonElement.isJsonObject())
		{
			JsonObject object = jsonElement.getAsJsonObject();

			matcher = deserializeObject(object, context);
		}
		else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString())
		{
			matcher = context.deserialize(jsonElement, StaticMatcher.class);
		}

		return matcher;
	}

	private MenuEntryMatcher deserializeObject(JsonObject object, JsonDeserializationContext context)
	{
		String matcherType = object.get("type").getAsString();
		if (matcherType == null)
		{
			return null;
		}

		MenuEntryMatcher matcher = null;
		switch (matcherType)
		{
			case "not":
				matcher = context.deserialize(object, NotMatcher.class);
				break;
			case "composite":
				matcher = context.deserialize(object, CompositeMatcher.class);
				break;
			case "string":
				matcher = context.deserialize(object, SimpleStringMatcher.class);
				break;
		}

		return matcher;
	}

	@Override
	public JsonElement serialize(MenuEntryMatcher menuEntryMatcher, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return jsonSerializationContext.serialize(menuEntryMatcher);
	}
}
