package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorCache;
import com.github.ldavid432.contextualcursor.ContextualCursorState;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.EMPTY_POINT;
import com.github.ldavid432.contextualcursor.cursor.OffsetCursor;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.github.ldavid432.contextualcursor.sprite.SpriteContext;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.BLANK_CURSOR_NAME;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.GENERIC_CURSOR_NAME;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * V2 render overlay
 */
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ContextualCursorV2DrawOverlay extends Overlay
{
	private final Client client;
	private final ContextualCursorPlugin plugin;
	private final ClientUI clientUi;
	private final SpriteContext spriteContext;
	private final ContextualCursorCache cache;

	@Inject
	void init()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(1f);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!cache.isOverlayV2())
		{
			return null;
		}

		final ContextualCursorState nextState = plugin.getNextState();
		ContextualCursorState currentState = plugin.getCurrentState();

		if (nextState != null)
		{
			if (!nextState.equals(currentState))
			{
				log.debug("Changing state from \n  {} \n  to {}", currentState, nextState);
			}
			ContextualCursorState previousState = plugin.getPreviousState();
			boolean isCursorChange = ((previousState != null ? previousState.getCursor() : null) != nextState.getCursor()) ||
				// Another plugin has saved and restored our blank cursor, messing up our state. We need to restore it here
				(clientUi.getCurrentCursor().getName().equals(BLANK_CURSOR_NAME) && nextState.getCursor() != null &&
					!nextState.getCursor().getName().equals(BLANK_CURSOR_NAME) && nextState.getCursor().getName().equals(GENERIC_CURSOR_NAME));

			plugin.setPreviousState(currentState);
			currentState = nextState;
			plugin.setCurrentState(nextState);

			if (isCursorChange)
			{
				log.debug("Changing cursor from {} to {}",
					((previousState != null && previousState.getCursor() != null) ? previousState.getCursor().getName() : null),
					(nextState.getCursor() != null ? nextState.getCursor().getName() : null)
				);
				onCursorChange(currentState.getCursor());
			}
		}

		if (currentState != null && graphics != null)
		{
			onRender(currentState, graphics);
		}

		return null;
	}

	private void onCursorChange(Cursor newCursor)
	{
		if (newCursor == null)
		{
			clientUi.resetCursor();
		}
		else
		{
			clientUi.setCursor(newCursor);
		}
	}

	private void onRender(ContextualCursorState currentState, Graphics2D graphics)
	{
		final Point mousePos = client.getMouseCanvasPosition();

		OffsetCursor background = currentState.getCursorBackground();
		if (background != null)
		{
			BufferedImage backgroundImage = spriteImageOrNull(currentState.getCursorBackground().getSprite());
			if (backgroundImage != null)
			{
				graphics.drawImage(backgroundImage, mousePos.getX() + background.getOffset().getX(), mousePos.getY() + background.getOffset().getY(), null);
			}
		}

		OffsetCursor foreground = currentState.getCursorForeground();
		if (foreground != null)
		{
			BufferedImage foregroundImage = spriteImageOrNull(foreground.getSprite());
			if (foregroundImage != null)
			{
				Point backgroundOffset = background != null ? background.getOffset() : EMPTY_POINT;
				final int spriteX = backgroundOffset.getX() + foreground.getOffset().getX() - foregroundImage.getWidth(null) / 2;
				final int spriteY = backgroundOffset.getY() + foreground.getOffset().getY() - foregroundImage.getHeight(null) / 2;
				graphics.drawImage(foregroundImage, mousePos.getX() + spriteX, mousePos.getY() + spriteY, null);
			}
		}
	}

	@Nullable
	private BufferedImage spriteImageOrNull(Sprite sprite)
	{
		return sprite != null ? sprite.getImage(spriteContext) : null;
	}

}
