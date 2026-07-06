package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.JsonCursor;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CursorAdapter implements JsonSerializer<Cursor>, JsonDeserializer<Cursor>
{
	@Override
	public Cursor deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException
	{
		// For now, only JsonCursor is supported for JSON deserialization
		// ItemCursor and SpellCursor are Java-only implementations
		return context.deserialize(jsonElement, JsonCursor.class);
	}

	@Override
	public JsonElement serialize(Cursor cursor, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return null;
	}
}
