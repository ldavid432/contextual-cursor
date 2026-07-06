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
				String widgetOption = object.get("option").getAsString();
				String widgetFromTarget = object.get("fromTarget").getAsString();

				if (widgetOption != null && widgetFromTarget != null)
				{
					matcher = MenuEntryMatchers.isWidgetTarget(widgetOption, widgetFromTarget);
					break;
				}

				matcher = MenuEntryMatchers.isWidgetTarget();
				break;
		}

		boolean isInverted = object.get("isInverted").getAsBoolean();

		if (isInverted)
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
/*

interface MenuEntryField<T> extends Function<MenuEntry, T>
{
}

class MenuEntryFields
{
	private static final Map<String, MenuEntryField<?>> map = Map.ofEntries(
		Map.entry("option", MenuEntry::getOption),
		Map.entry("target", MenuEntry::getTarget),
		Map.entry("npc", MenuEntry::getNpc),
		Map.entry("player", MenuEntry::getPlayer)
	);

	@Nullable
	public static MenuEntryField<?> get(String name)
	{
		return map.getOrDefault(name, null);
	}
}

interface MenuEntryPredicate<T> extends Predicate<T>
{

}

@AllArgsConstructor(staticName = "of")
class MenuEntryPredicateImpl<T> implements MenuEntryPredicate<T>
{
	T value;
	BiFunction<T, T, Boolean> predicate;

	@Override
	public boolean test(T other)
	{
		return predicate.apply(value, other);
	}
}

class MenuEntryPredicates
{
	private static BiFunction<?, ?, Boolean> string(BiFunction<String, String, Boolean> predicate) throws Exception
	{
		return (s1, s2) -> {
			requireType(s1);
			requireType(s2);
			requireType(predicate);
			return predicate.apply(s1, s2);
		};
	}

	private static final Map<String, BiFunction<?, ?, Boolean>> map = Map.ofEntries(
		Map.entry("equals", Object::equals),
		Map.entry("startsWith", string(String::startsWith)),
		Map.entry("endsWith", string(String::endsWith)),
		Map.entry("contains", string(String::contains))
	);

	// TODO: Improve casting error handling
	@Nullable
	public static <T> BiFunction<T, T, Boolean> get(String name)
	{
		try
		{
			//noinspection unchecked
			return (BiFunction<T, T, Boolean>) map.getOrDefault(name, null);
		}
		catch (ClassCastException e)
		{
			return null;
		}
	}
}
*/
