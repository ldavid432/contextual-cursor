package io.hydrox.contextualcursor.serialization;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.buildGson;
import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.cursor.JsonCursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.google.gson.Gson;
import net.runelite.api.MenuEntry;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextualCursorDefinitionTest
{
	Gson gson = buildGson(new Gson());

	@Test
	public void test_ContextualCursorDefinition_With_Single_JsonCursor()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"attack_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"attack\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors() != null;
		assert definition.getCursors().size() == 1;
		assert definition.getCursors().get(0) instanceof JsonCursor;
		assert definition.getDefaultCursorSprite() != null;
		assert definition.getBackgroundCursorSprite() != null;
	}

	@Test
	public void test_ContextualCursorDefinition_With_Multiple_JsonCursors()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"attack_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"attack\", \"isInverted\": false}\n" +
			"    },\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"defend_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"defend\", \"isInverted\": false}\n" +
			"    },\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"cache\", \"archiveId\": 1, \"groupId\": 2, \"fileId\": 3},\n" +
			"      \"matcher\": {\"type\": \"isNpc\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors() != null;
		assert definition.getCursors().size() == 3;

		for (Cursor cursor : definition.getCursors())
		{
			assert cursor instanceof JsonCursor;
			assert cursor.getMatcher() != null;
		}
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_OptionMatcher()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"bank_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"option\", \"predicate\": \"startsWith\", \"value\": \"bank\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors().size() == 1;

		Cursor cursor = definition.getCursors().get(0);
		assert cursor instanceof JsonCursor;

		MenuEntryMatcher matcher = cursor.getMatcher();
		assert matcher != null;

		// Test the matcher works with a menu entry
		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("bank chest");

		assert matcher.matches(entry);
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_TargetMatcher()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"npc_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"target\", \"predicate\": \"contains\", \"value\": \"goblin\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;

		Cursor cursor = definition.getCursors().get(0);
		MenuEntryMatcher matcher = cursor.getMatcher();

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getTarget()).thenReturn("angry goblin");

		assert matcher.matches(entry);
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_CompositeMatchers()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"attack_npc_cursor\"},\n" +
			"      \"matcher\": {\n" +
			"        \"type\": \"composite\",\n" +
			"        \"operator\": \"AND\",\n" +
			"        \"children\": [\n" +
			"          {\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"attack\", \"isInverted\": false},\n" +
			"          {\"type\": \"isNpc\", \"isInverted\": false}\n" +
			"        ],\n" +
			"        \"isInverted\": false\n" +
			"      }\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors().size() == 1;

		Cursor cursor = definition.getCursors().get(0);
		MenuEntryMatcher matcher = cursor.getMatcher();

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("attack");
		Mockito.when(entry.getNpc()).thenReturn(Mockito.mock(net.runelite.api.NPC.class));

		assert matcher.matches(entry);
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_InvertedMatcher()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"non_npc_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"isNpc\", \"isInverted\": true}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;

		Cursor cursor = definition.getCursors().get(0);
		MenuEntryMatcher matcher = cursor.getMatcher();

		// Should match entries without an NPC (inverted)
		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getNpc()).thenReturn(null);

		assert matcher.matches(entry);
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_ResourceSprite()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"resource\", \"fileName\": \"custom_cursor\"},\n" +
			"      \"matcher\": {\"type\": \"isObject\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors().size() == 1;

		Cursor cursor = definition.getCursors().get(0);
		assert cursor.getSprite(null) != null;
	}

	@Test
	public void test_ContextualCursorDefinition_JsonCursor_With_CacheSprite()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\n" +
			"      \"sprite\": {\"type\": \"cache\", \"archiveId\": 10, \"groupId\": 20, \"fileId\": 30},\n" +
			"      \"matcher\": {\"type\": \"isPlayer\", \"isInverted\": false}\n" +
			"    }\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors().size() == 1;

		Cursor cursor = definition.getCursors().get(0);
		assert cursor instanceof JsonCursor;
	}

	@Test
	public void test_ContextualCursorDefinition_Empty_Cursors_List()
	{
		String json = "{\n" +
			"  \"cursors\": [],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors() != null;
		assert definition.getCursors().isEmpty();
	}

	@Test
	public void test_ContextualCursorDefinition_All_Matcher_Types_In_Cursors()
	{
		String json = "{\n" +
			"  \"cursors\": [\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c1\"}, \"matcher\": {\"type\": \"isNpc\", \"isInverted\": false}},\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c2\"}, \"matcher\": {\"type\": \"isPlayer\", \"isInverted\": false}},\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c3\"}, \"matcher\": {\"type\": \"isObject\", \"isInverted\": false}},\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c4\"}, \"matcher\": {\"type\": \"isGroundItem\", \"isInverted\": false}},\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c5\"}, \"matcher\": {\"type\": \"isMovement\", \"isInverted\": false}},\n" +
			"    {\"sprite\": {\"type\": \"resource\", \"fileName\": \"c6\"}, \"matcher\": {\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"test\", \"isInverted\": false}}\n" +
			"  ],\n" +
			"  \"defaultCursorSprite\": {\"type\": \"resource\", \"fileName\": \"generic\"},\n" +
			"  \"backgroundCursorSprite\": {\"type\": \"resource\", \"fileName\": \"blank\"}\n" +
			"}";

		ContextualCursorDefinition definition = gson.fromJson(json, ContextualCursorDefinition.class);

		assert definition != null;
		assert definition.getCursors().size() == 6;

		for (Cursor cursor : definition.getCursors())
		{
			assert cursor instanceof JsonCursor;
			assert cursor.getMatcher() != null;
		}
	}

}
