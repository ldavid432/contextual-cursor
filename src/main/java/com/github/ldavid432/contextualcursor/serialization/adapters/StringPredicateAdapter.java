package com.github.ldavid432.contextualcursor.serialization.adapters;

import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import static com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate.asStringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class StringPredicateAdapter implements JsonSerializer<ValuePredicate<String>>, JsonDeserializer<ValuePredicate<String>>
{
	@Override
	public ValuePredicate<String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
	{
		switch (jsonElement.getAsString())
		{
			case "equals":
				return asStringPredicate(ValuePredicate.equals());
			case "startsWith":
				return StringPredicate.STARTS_WITH;
			case "endsWidth":
				return StringPredicate.ENDS_WITH;
			case "contains":
				return StringPredicate.CONTAINS;
		}

		return asStringPredicate(ValuePredicate.equals());
	}

	@Override
	public JsonElement serialize(ValuePredicate<String> predicate, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return null;
	}
}
