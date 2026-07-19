package io.hydrox.contextualcursor.serialization;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.buildGson;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.loadLocalCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.JsonCursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleStringMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import com.github.ldavid432.contextualcursor.sprite.CacheSprite;
import com.github.ldavid432.contextualcursor.sprite.ResourceSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.hydrox.contextualcursor.ContextualCursor;
import java.util.List;
import org.junit.Test;

public class ContextualCursorDefinitionContentMatchTest
{
	Gson gson = buildGson(new GsonBuilder().setPrettyPrinting().create());

	/**
	 * Loop through every ContextualCursor enum and verify the JSON cursor at the same index
	 * has identical content (matcher type, sprite type, structure, etc.)
	 */
	@Test
	public void test_EveryEnumCursor_HasIdenticalJsonContent()
	{
		ContextualCursorDefinition definition = loadLocalCursorDefinition(gson, "local-cursors");

		ContextualCursor[] enumCursors = ContextualCursor.values();
		List<? extends Cursor> jsonCursors = definition.getCursors();

		assert jsonCursors.size() == enumCursors.length :
			String.format("Cursor count mismatch: JSON has %d, enum has %d",
				jsonCursors.size(), enumCursors.length);

		for (int i = 0; i < enumCursors.length; i++)
		{
			ContextualCursor enumCursor = enumCursors[i];
			Cursor jsonCursor = jsonCursors.get(i);

			String cursorName = enumCursor.name();

			// Verify matcher content is identical
			verifyMatcherContent(i, cursorName, enumCursor.getMatcher(), jsonCursor.getMatcher());

			// Verify sprite content is identical
			verifySpritContent(i, cursorName, enumCursor.getSprite(null), jsonCursor.getSprite(null));
		}
	}

	/**
	 * Verify matcher content by checking class type and internal structure
	 */
	private void verifyMatcherContent(int index, String cursorName,
	                                  MenuEntryMatcher enumMatcher, MenuEntryMatcher jsonMatcher)
	{
		assert enumMatcher != null : String.format(
			"Enum Matcher is null for JSON=%s",
			jsonMatcher.getClass().getSimpleName()
		);

		assert jsonMatcher != null : String.format(
			"JSON Matcher is null for ENUM=%s",
			enumMatcher.getClass().getSimpleName()
		);

		assert enumMatcher.getClass() == jsonMatcher.getClass() :
			String.format("[%d] %s: Matcher class mismatch. Enum=%s, JSON=%s",
				index, cursorName, enumMatcher.getClass().getSimpleName(),
				jsonMatcher.getClass().getSimpleName());

		// If it's a SimpleValueMatcher, compare internals
		if (enumMatcher instanceof SimpleStringMatcher)
		{
			verifySimpleValueMatcherContent(index, cursorName,
				(SimpleStringMatcher) enumMatcher,
				(SimpleStringMatcher) jsonMatcher);
		}

		// If it's a CompositeMatcher, compare internals
		if (enumMatcher instanceof CompositeMatcher)
		{
			verifyCompositeMatcherContent(index, cursorName,
				(CompositeMatcher) enumMatcher,
				(CompositeMatcher) jsonMatcher);
		}

		// If it's a NotMatcher, compare the wrapped matcher
		if (enumMatcher instanceof NotMatcher)
		{
			verifyNotMatcherContent(index, cursorName,
				(NotMatcher) enumMatcher,
				(NotMatcher) jsonMatcher);
		}
	}

	/**
	 * Verify SimpleValueMatcher (OptionMatcher, TargetMatcher) content
	 */
	private void verifySimpleValueMatcherContent(int index, String cursorName,
	                                             SimpleStringMatcher enumMatcher,
	                                             SimpleStringMatcher jsonMatcher)
	{
		// Compare value
		Object enumValue = enumMatcher.getValue();
		Object jsonValue = jsonMatcher.getValue();

		assert equals(enumValue, jsonValue) :
			String.format("[%d] %s: SimpleValueMatcher value mismatch. Enum=%s, JSON=%s",
				index, cursorName, enumValue, jsonValue);

		// Compare field
		// Note: MenuEntryField needs getter or you'll need to add @VisibleForTesting getter
		// For now, we can compare by checking if they're the same instance (if singletons)

		// Compare predicate
		Object enumPredicate = enumMatcher.getPredicate();
		Object jsonPredicate = jsonMatcher.getPredicate();

		assert enumPredicate.getClass() == jsonPredicate.getClass() :
			String.format("[%d] %s: SimpleValueMatcher predicate class mismatch. Enum=%s, JSON=%s",
				index, cursorName, enumPredicate.getClass().getSimpleName(),
				jsonPredicate.getClass().getSimpleName());

		// Compare predicate instances (StringPredicate instances should be identical)
		if (enumPredicate instanceof StringPredicate && jsonPredicate instanceof StringPredicate)
		{
			assert enumPredicate == jsonPredicate :
				String.format("[%d] %s: StringPredicate mismatch. Enum=%s, JSON=%s",
					index, cursorName, enumPredicate, jsonPredicate);
		}
	}

	/**
	 * Verify CompositeMatcher content
	 */
	private void verifyCompositeMatcherContent(int index, String cursorName,
	                                           CompositeMatcher enumMatcher,
	                                           CompositeMatcher jsonMatcher)
	{
		// Compare operator
		CompositeMatcher.Operator enumOperator = enumMatcher.getOperator();
		CompositeMatcher.Operator jsonOperator = jsonMatcher.getOperator();

		assert enumOperator == jsonOperator :
			String.format("[%d] %s: CompositeMatcher operator mismatch. Enum=%s, JSON=%s",
				index, cursorName, enumOperator, jsonOperator);

		// Compare children count
		List<MenuEntryMatcher> enumChildren = enumMatcher.getChildren();
		List<MenuEntryMatcher> jsonChildren = jsonMatcher.getChildren();

		assert enumChildren.size() == jsonChildren.size() :
			String.format("[%d] %s: CompositeMatcher children count mismatch. Enum=%d, JSON=%d, \nENUM=%s, \nJSON=%s",
				index, cursorName, enumChildren.size(), jsonChildren.size(), enumChildren, jsonChildren);

		// Recursively verify each child matcher
		for (int i = 0; i < enumChildren.size(); i++)
		{
			verifyMatcherContent(index, cursorName + ".child[" + i + "]",
				enumChildren.get(i), jsonChildren.get(i));
		}
	}

	/**
	 * Verify NotMatcher content
	 */
	private void verifyNotMatcherContent(int index, String cursorName,
	                                     NotMatcher enumMatcher,
	                                     NotMatcher jsonMatcher)
	{
		MenuEntryMatcher enumParent = enumMatcher.getParent();
		MenuEntryMatcher jsonParent = jsonMatcher.getParent();

		assert enumParent != null && jsonParent != null :
			String.format("[%d] %s: NotMatcher has null parent", index, cursorName);

		verifyMatcherContent(index, cursorName + ".parent", enumParent, jsonParent);
	}

	/**
	 * Verify Sprite content by comparing class type and properties
	 */
	private void verifySpritContent(int index, String cursorName,
	                                Sprite enumSprite, Sprite jsonSprite)
	{
		assert enumSprite.getClass() == jsonSprite.getClass() :
			String.format("[%d] %s: Sprite class mismatch. Enum=%s, JSON=%s",
				index, cursorName, enumSprite.getClass().getSimpleName(),
				jsonSprite.getClass().getSimpleName());

		// If it's a ResourceSprite, compare fileName
		if (enumSprite instanceof ResourceSprite)
		{
			ResourceSprite enumResourceSprite = (ResourceSprite) enumSprite;
			ResourceSprite jsonResourceSprite = (ResourceSprite) jsonSprite;

			assert equals(enumResourceSprite.getFileName(), jsonResourceSprite.getFileName()) :
				String.format("[%d] %s: ResourceSprite fileName mismatch. Enum=%s, JSON=%s",
					index, cursorName,
					enumResourceSprite.getFileName(),
					jsonResourceSprite.getFileName());
		}

		// If it's a CacheSprite, compare id
		if (enumSprite instanceof CacheSprite)
		{
			CacheSprite enumCacheSprite = (CacheSprite) enumSprite;
			CacheSprite jsonCacheSprite = (CacheSprite) jsonSprite;

			assert enumCacheSprite.getId() == jsonCacheSprite.getId() :
				String.format("[%d] %s: CacheSprite id mismatch. Enum=%d, JSON=%d",
					index, cursorName,
					enumCacheSprite.getId(),
					jsonCacheSprite.getId());
		}
	}

	/**
	 * Verify default and background sprites are identical
	 */
	@Test
	public void test_DefaultAndBackgroundSprites_AreIdentical()
	{
		ContextualCursorDefinition definition = loadLocalCursorDefinition(gson, "local-cursors");

		Sprite defaultSprite = definition.getDefaultCursorSprite();
		Sprite backgroundSprite = definition.getBackgroundCursorSprite();

		assert defaultSprite != null : "Default cursor sprite is null";
		assert backgroundSprite != null : "Background cursor sprite is null";

		assert defaultSprite instanceof ResourceSprite :
			String.format("Default sprite should be ResourceSprite but is %s",
				defaultSprite.getClass().getSimpleName());
		assert backgroundSprite instanceof ResourceSprite :
			String.format("Background sprite should be ResourceSprite but is %s",
				backgroundSprite.getClass().getSimpleName());

		ResourceSprite defaultResourceSprite = (ResourceSprite) defaultSprite;
		ResourceSprite backgroundResourceSprite = (ResourceSprite) backgroundSprite;

		assert "generic".equals(defaultResourceSprite.getFileName()) :
			String.format("Default sprite fileName should be 'generic' but is '%s'",
				defaultResourceSprite.getFileName());
		assert "blank".equals(backgroundResourceSprite.getFileName()) :
			String.format("Background sprite fileName should be 'blank' but is '%s'",
				backgroundResourceSprite.getFileName());
	}

	/**
	 * Verify all cursors are JsonCursor instances
	 */
	@Test
	public void test_AllCursors_AreJsonCursorType()
	{
		ContextualCursorDefinition definition = loadLocalCursorDefinition(gson, "local-cursors");

		for (int i = 0; i < definition.getCursors().size(); i++)
		{
			Cursor cursor = definition.getCursors().get(i);
			assert cursor instanceof JsonCursor :
				String.format("[%d] Cursor is not JsonCursor but %s",
					i, cursor.getClass().getSimpleName());
		}
	}

	/**
	 * Helper method to safely compare objects
	 */
	private boolean equals(Object a, Object b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}

}
