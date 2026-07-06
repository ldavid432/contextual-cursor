package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.menuentry.field.MenuEntryField;
import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class StringFieldAdapter implements JsonSerializer<MenuEntryField<String>>, JsonDeserializer<MenuEntryField<String>>
{
	@Override
	public MenuEntryField<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		String field = jsonElement.getAsString();

		switch (field)
		{
			case "option":
				return StringField.OPTION;
			case "target":
				return StringField.TARGET;
			default:
				return null;
		}
	}

	@Override
	public JsonElement serialize(MenuEntryField<String> stringField, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return null;
	}
}
