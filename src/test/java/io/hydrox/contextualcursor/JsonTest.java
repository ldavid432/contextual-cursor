package io.hydrox.contextualcursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.OptionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import com.github.ldavid432.contextualcursor.serialization.adapters.MenuEntryMatcherAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.SpriteAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.StringPredicateAdapter;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.runelite.api.MenuEntry;
import org.junit.Test;
import org.mockito.Mockito;

public class JsonTest
{
	Gson gson = new GsonBuilder()
		.registerTypeAdapter(MenuEntryMatcher.class, new MenuEntryMatcherAdapter())
		//.registerTypeAdapter(StringPredicate.class, new StringPredicateAdapter())
		.registerTypeAdapter(new TypeToken<ValuePredicate<String>>() {}.getType(), new StringPredicateAdapter())
		//.registerTypeAdapter(StringField.class, new StringPredicateAdapter())
		//.registerTypeAdapter(new TypeToken<MenuEntryField<String>>() {}.getType(), new StringPredicateAdapter())
		.registerTypeAdapter(Sprite.class, new SpriteAdapter())
		.create();

	@Test
	public void test()
	{
		OptionMatcher match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"startsWith\", \"value\": \"bank\"}", OptionMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("bank");

		assert match.matches(entry);

	}

	static <T> Type typeOf()
	{
		return new TypeToken<T>() {}.getType();
	}
}
