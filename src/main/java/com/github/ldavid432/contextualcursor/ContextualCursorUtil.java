package com.github.ldavid432.contextualcursor;

import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

public class ContextualCursorUtil
{
	public static BufferedImage scaleImage(BufferedImage image, ContextualCursorPlugin plugin)
	{
		return scaleImage(image, plugin.getCursorScale(), plugin.isSmoothScalingEnabled());
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

		if (builder.length() != 0) {
			builder.insert(0, changelogLine("Contextual Cursor has been updated!", true, false));
			builder.replace(builder.length() - 4, builder.length(), "");

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

}
