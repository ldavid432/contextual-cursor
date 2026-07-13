package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatchers;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.OptionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.TargetMatcher;
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
		JsonObject object = jsonElement.getAsJsonObject();

		String matcherType = object.get("type").getAsString();
		if (matcherType == null) return null;

		MenuEntryMatcher matcher = null;

		switch (matcherType)
		{
			case "composite":
				matcher = context.deserialize(object, CompositeMatcher.class);
				break;
			case "option":
			case "target":
				Type simpleType;
				switch (matcherType)
				{
					case "option":
						simpleType = OptionMatcher.class;
						break;
					case "target":
						simpleType = TargetMatcher.class;
						break;
					default:
						throw new JsonParseException("Impossible");
				}

				matcher = context.deserialize(object, simpleType);
				break;
			case "isNpc":
				matcher = MenuEntryMatchers.isNpc();
				break;
			case "isObject":
				matcher = MenuEntryMatchers.isObject();
				break;
			case "isGroundItem":
				matcher = MenuEntryMatchers.isGroundItem();
				break;
			case "isMovement":
				matcher = MenuEntryMatchers.isMovement();
				break;
			case "isCancel":
				matcher = MenuEntryMatchers.isCancel();
				break;
			case "isInterface":
				matcher = MenuEntryMatchers.isInterface();
				break;
			case "isSpell":
				matcher = MenuEntryMatchers.isSpell();
				break;
			case "isPlayer":
				matcher = MenuEntryMatchers.isPlayer();
				break;
			case "isWidgetTarget":
				JsonElement widgetOption = object.get("option");
				JsonElement widgetFromTarget = object.get("fromTarget");

				if (widgetOption != null && widgetFromTarget != null)
				{
					matcher = MenuEntryMatchers.isWidgetTarget(widgetOption.getAsString(), widgetFromTarget.getAsString());
					break;
				}

				matcher = MenuEntryMatchers.isWidgetTarget();
				break;
		}

		JsonElement isInverted = object.get("isInverted");

		if (isInverted != null && isInverted.getAsBoolean())
		{
			matcher = new NotMatcher(matcher);
		}

		return matcher;
	}

	@Override
	public JsonElement serialize(MenuEntryMatcher menuEntryMatcher, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return null;
	}
}
