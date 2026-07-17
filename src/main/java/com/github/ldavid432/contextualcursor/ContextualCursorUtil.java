package com.github.ldavid432.contextualcursor;

import com.github.ldavid432.contextualcursor.cursor.ContextualCursorDefinition;
import com.github.ldavid432.contextualcursor.cursor.Cursor;
import com.github.ldavid432.contextualcursor.menuentry.MenuEntryMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher;
import com.github.ldavid432.contextualcursor.menuentry.matchers.CompositeMatcher.Operator;
import com.github.ldavid432.contextualcursor.menuentry.matchers.SimpleStringMatcher;
import com.github.ldavid432.contextualcursor.menuentry.predicates.StringPredicate;
import com.github.ldavid432.contextualcursor.serialization.adapters.CursorAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.MenuEntryMatcherAdapter;
import com.github.ldavid432.contextualcursor.serialization.adapters.SkipFieldDefaultsTypeAdapterFactory;
import com.github.ldavid432.contextualcursor.serialization.adapters.SpriteAdapter;
import com.github.ldavid432.contextualcursor.sprite.BaseSprite;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.google.gson.Gson;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class ContextualCursorUtil
{
	public static BufferedImage scaleImage(BufferedImage image, ContextualCursorPlugin plugin)
	{
		return scaleImage(image, plugin.getCursorScale(), plugin.isCursorSmoothScalingEnabled());
	}

	public static BufferedImage scaleImage(BufferedImage image, double scale, boolean isSmoothScaling)
	{
		if (scale == 1.0)
		{
			return image;
		}
		else
		{
			return ImageUtil.bufferedImageFromImage(
				image.getScaledInstance(
					(int) (image.getWidth() * scale),
					(int) (image.getHeight() * scale),
					isSmoothScaling ? Image.SCALE_SMOOTH : Image.SCALE_FAST
				)
			);
		}
	}

	public static Point scalePoint(Point point, double scale)
	{
		if (scale == 1.0)
		{
			return point;
		}
		else
		{
			return new Point((int) (point.getX() * scale), (int) (point.getY() * scale));
		}
	}

	public static BufferedImage flipImage(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage flipped = new BufferedImage(w, h, img.getType());
		Graphics2D g = flipped.createGraphics();

		g.drawImage(img, 0, h, w, -h, null);

		g.dispose();
		return flipped;
	}

	public static BufferedImage loadImage(String fileName)
	{
		return ImageUtil.loadImageResource(ContextualCursorPlugin.class, String.format("cursors/%s.png", fileName));
	}

	public static boolean mouseInsideBounds(Point mousePos, Client client)
	{
		return mousePos.getX() > 0 && mousePos.getX() <= client.getCanvasWidth() &&
			mousePos.getY() > 0 && mousePos.getY() <= client.getCanvasHeight();
	}

	public static void handleChangelog(ContextualCursorConfig config, ChatMessageManager chatMessageManager, Client client,
									   boolean isCustomCursorPluginEnabled)
	{
		if (config.getLastSeenVersion() >= ContextualCursorConfig.CURRENT_VERSION)
		{
			return;
		}

		StringBuilder builder = new StringBuilder();

		// Since last seen version wasn't in 1.0 checking for only it will trigger for everyone who installs the plugin.
		//  By only triggering this during startup while not logged in we can "better" attempt to determine if this is a previous install or not.
		//  Still not totally accurate but better than nothing.

		// 1.9.0
		if (config.getLastSeenVersion() < 1)
		{
			if (client.getGameState() != GameState.LOGGED_IN)
			{
				// Existing install (theoretically)
				builder
					.append(changelogLine("The plugin has a new maintainer (for a few months now). if you have any feedback please leave it on the *new* github"))
					.append(changelogLine("Cursor themes are now supported! Currently includes osrs and rs2 (default)"));
			}
			else if (client.getGameState() == GameState.LOGGED_IN)
			{
				// New install (theoretically)
				if (!isCustomCursorPluginEnabled)
				{
					// Ideally this would be true by default, but we don't want to suddenly enable it for everyone who's been using this plugin for years.
					// For now only enable by default for new users (if they don't already have a custom cursor)
					config.setCustomCursorEnabled(true);
				}
			}
		}

		// 1.10.0
		if (config.getLastSeenVersion() < 2 && client.getGameState() != GameState.LOGGED_IN)
		{
			// Existing install
			builder
				.append(changelogLine("New option to make the *default* cursor an overlay!"))
				.append(changelogLine("This allows for better default cursor scaling and fixes the washed-out colors."));
		}

		// 1.11.0
		if (config.getLastSeenVersion() < 3 && client.getGameState() != GameState.LOGGED_IN)
		{
			// Existing install
			builder
				.append(changelogLine("Items now show up in the cursor when used on another item"))
				.append(changelogLine("Selected items and spell cursors now stay visible until the item/spell is deselected"))
				.append(changelogLine("These new additions can be configured in the settings if desired (default on)"));
		}

		if (builder.length() != 0)
		{
			builder.insert(0, changelogLine("Contextual Cursor has been updated!", true, false));
			int lastNewlineIndex = builder.lastIndexOf("<br>");
			builder.replace(lastNewlineIndex, lastNewlineIndex + 4, "");


			chatMessageManager.queue(
				QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(builder.toString())
					.build()
			);
		}

		config.setLastSeenVersion(ContextualCursorConfig.CURRENT_VERSION);
	}

	private static String changelogLine(String text)
	{
		return changelogLine(text, true);
	}

	private static String changelogLine(String text, boolean showNewline)
	{
		return changelogLine(text, showNewline, true);
	}

	private static String changelogLine(String text, boolean showNewline, boolean showBullet)
	{
		return ColorUtil.wrapWithColorTag((showBullet ? "* " : "") + text + (showNewline ? "<br>" : ""), Color.RED);
	}

	public static Gson buildGson(Gson parent)
	{
		return parent.newBuilder()
			.registerTypeAdapterFactory(
				// Avoid deserializing some default values to reduce the file size
				SkipFieldDefaultsTypeAdapterFactory.builder()
					.add(BaseSprite.class, "isInverted", false)
					.add(SimpleStringMatcher.class, "predicate", StringPredicate.EQUALS)
					.add(CompositeMatcher.class, "operator", Operator.OR)
					.build()
			)
			.registerTypeAdapter(MenuEntryMatcher.class, new MenuEntryMatcherAdapter())
			.registerTypeAdapter(Cursor.class, new CursorAdapter())
			.registerTypeAdapter(Sprite.class, new SpriteAdapter())
			.create();
	}

	@Nullable
	public static ContextualCursorDefinition loadLocalCursorDefinition(Gson gson, String fileName)
	{
		try
		{
			String resourcePath = String.format("json/%s.json", fileName);
			InputStream inputStream = ContextualCursorPlugin.class.getResourceAsStream(resourcePath);

			if (inputStream == null)
			{
				log.error("Cursor definition file not found: {}", resourcePath);
				return null;
			}

			try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
			{
				return gson.fromJson(reader, ContextualCursorDefinition.class);
			}
		}
		catch (Exception e)
		{
			log.error("Failed to parse cursor definition JSON: {}", fileName, e);
			return null;
		}
	}

}
