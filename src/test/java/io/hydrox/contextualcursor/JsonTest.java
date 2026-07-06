package io.hydrox.contextualcursor;

import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.buildGson;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.field.StringField;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.NotMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.OptionMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleValueMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.TargetMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import com.google.gson.Gson;
import java.util.Objects;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import org.junit.Test;
import org.mockito.Mockito;

public class JsonTest
{
	Gson gson = buildGson(new Gson());

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
	public void test_OptionMatcher_WithContainsPredicate()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"contains\", \"value\": \"buy\"}", OptionMatcher.class);

		assert match != null;
		assert match instanceof OptionMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "buy");

		assert match.getPredicate() != null;
		assert match.getPredicate() == StringPredicate.CONTAINS;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("buy-10");

		assert match.matches(entry);

	}

	@Test
	public void test_OptionMatcher_WithEqualsPredicate()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"walk here\"}", OptionMatcher.class);

		assert match != null;
		assert match instanceof OptionMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "walk here");

		assert match.getPredicate() != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("walk here");

		assert match.matches(entry);

	}

	@Test
	public void test_TargetMatcher()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"target\", \"predicate\": \"endsWith\", \"value\": \"goblin\"}", TargetMatcher.class);

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

	@Test
	public void test_TargetMatcher_WithStartsWithPredicate()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"target\", \"predicate\": \"startsWith\", \"value\": \"runite\"}", TargetMatcher.class);

		assert match != null;
		assert match instanceof TargetMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "runite");

		assert match.getPredicate() != null;
		assert match.getPredicate() == StringPredicate.STARTS_WITH;

		assert match.getField() != null;
		assert match.getField() == StringField.TARGET;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getTarget()).thenReturn("runite golem");

		assert match.matches(entry);

	}

	@Test
	public void test_TargetMatcher_WithContainsPredicate()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"target\", \"predicate\": \"contains\", \"value\": \"half\"}", TargetMatcher.class);

		assert match != null;
		assert match instanceof TargetMatcher;

		assert match.getValue() != null;
		assert Objects.equals(match.getValue(), "half");

		assert match.getPredicate() != null;
		assert match.getPredicate() == StringPredicate.CONTAINS;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getTarget()).thenReturn("tooth half key");

		assert match.matches(entry);

	}

	@Test
	public void test_OptionMatcher_DoesNotMatch()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"option\", \"predicate\": \"startsWith\", \"value\": \"bank\"}", OptionMatcher.class);

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("drop");

		assert !match.matches(entry);

	}

	@Test
	public void test_TargetMatcher_DoesNotMatch()
	{
		SimpleValueMatcher<String> match = gson.fromJson("{\"type\": \"target\", \"predicate\": \"endsWith\", \"value\": \"goblin\"}", TargetMatcher.class);

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getTarget()).thenReturn("nex");

		assert !match.matches(entry);

	}

	@Test
	public void test_IsNpcMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isNpc\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getNpc()).thenReturn(Mockito.mock(NPC.class)); // Any non-null NPC

		assert match.matches(entry);

	}

	@Test
	public void test_IsPlayerMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isPlayer\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getPlayer()).thenReturn(Mockito.mock(Player.class)); // Any non-null Player

		assert match.matches(entry);

	}

	@Test
	public void test_IsObjectMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isObject\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.GAME_OBJECT_FIRST_OPTION);

		assert match.matches(entry);

	}

	@Test
	public void test_IsGroundItemMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isGroundItem\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.GROUND_ITEM_FIRST_OPTION);

		assert match.matches(entry);

	}

	@Test
	public void test_IsMovementMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isMovement\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.WALK);

		assert match.matches(entry);

	}

	@Test
	public void test_IsCancelMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isCancel\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.CANCEL);

		assert match.matches(entry);

	}

	@Test
	public void test_IsInterfaceMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isInterface\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.CC_OP);

		assert match.matches(entry);

	}

	@Test
	public void test_IsSpellMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isSpell\", \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.WIDGET_TARGET);
		Mockito.when(entry.getOption()).thenReturn("cast");

		assert match.matches(entry);

	}

	@Test
	public void test_InvertedMatcher()
	{
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"isMovement\", \"isInverted\": true}", MenuEntryMatcher.class);

		assert match != null;
		assert match instanceof NotMatcher;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getType()).thenReturn(MenuAction.CANCEL);

		assert match.matches(entry); // Should be true because CANCEL is NOT a movement action

	}

	@Test
	public void test_CompositeMatcher_OR()
	{
		// Create a composite matcher with OR operator
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"composite\", \"operator\": \"OR\", \"children\": [{\"type\": \"isNpc\", \"isInverted\": false}, {\"type\": \"isPlayer\", \"isInverted\": false}], \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;
		assert match instanceof CompositeMatcher;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getPlayer()).thenReturn(Mockito.mock(Player.class));
		Mockito.when(entry.getNpc()).thenReturn(null);

		assert match.matches(entry); // Should match because it has a player

	}

	@Test
	public void test_CompositeMatcher_AND()
	{
		// Create a composite matcher with AND operator
		MenuEntryMatcher match = gson.fromJson("{\"type\": \"composite\", \"operator\": \"AND\", \"children\": [{\"type\": \"option\", \"predicate\": \"equals\", \"value\": \"attack\", \"isInverted\": false}, {\"type\": \"isNpc\", \"isInverted\": false}], \"isInverted\": false}", MenuEntryMatcher.class);

		assert match != null;
		assert match instanceof CompositeMatcher;

		MenuEntry entry = Mockito.mock(MenuEntry.class);
		Mockito.when(entry.getOption()).thenReturn("attack");
		Mockito.when(entry.getNpc()).thenReturn(Mockito.mock(NPC.class));

		assert match.matches(entry); // Should match because both conditions are true

	}

}
