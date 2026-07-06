package io.hydrox.contextualcursor;

import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.matchers.OptionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleValueMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.TargetMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import com.github.ldavid432.contextualcursor.menuentry.predicates.ValuePredicate;
import com.github.ldavid432.contextualcursor.serialization.adapters.MenuEntryMatcherAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.SpriteAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.StringPredicateAdapter;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Objects;
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
	public void test_OptionMatcher()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"startsWith\", \"value\": \"bank\"}", OptionMatcher.class);

		assert match != null;
		assert match instanceof OptionMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "bank");

		assert match.getPredicate() != null;
		assert match.getPredicate() instanceof StringPredicate;
		assert match.getPredicate() == StringPredicate.STARTS_WITH;

		assert match.getField() != null;
		assert match.getField() instanceof StringField;
		assert match.getField() == StringField.OPTION;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("bank chest");

		assert match.matches(entry);

	}

	@Test
	public void test_TargetMatcher()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"endsWith\", \"value\": \"goblin\"}", TargetMatcher.class);

		assert match != null;
		assert match instanceof TargetMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "goblin");

		assert match.getPredicate() != null;
		assert match.getPredicate() instanceof StringPredicate;
		assert match.getPredicate() == StringPredicate.ENDS_WITH;

		assert match.getField() != null;
		assert match.getField() instanceof StringField;
		assert match.getField() == StringField.TARGET;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getTarget()).thenReturn("green goblin");

		assert match.matches(entry);

	}

	
}
