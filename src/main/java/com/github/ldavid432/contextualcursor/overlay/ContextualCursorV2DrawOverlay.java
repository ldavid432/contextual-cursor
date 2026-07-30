package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.ContextualCursorState;
import static com.github.ldavid432.contextualcursor.ContextualCursorUtil.scalePoint;
import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.provider.EmptyProviderCallbacks;
import com.github.ldavid432.contextualcursor.provider.ProviderCallbacks;
import com.github.ldavid432.contextualcursor.sprite.Sprite;
import com.github.ldavid432.contextualcursor.sprite.SpriteContext;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import static io.hydrox.contextualcursor.ContextualCursorWorkerOverlay.BLANK_CURSOR_NAME;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
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
public class ContextualCursorV2DrawOverlay extends Overlay implements ProviderCallbacks
{
	//The pointer sticks out to the left slightly, so this makes sure it's point to the correct spot
	private static final Point POINTER_OFFSET = new Point(-5, 0);
	//The centre of the circle (biased bottom right since it's an even size), for use with sprites
	private static final Point CENTRAL_POINT = new Point(16, 18);

	private final Client client;
	private final ContextualCursorPlugin plugin;
	private final ClientUI clientUi;
	private final SpriteContext spriteContext;
	private final CursorProvider cursorProvider;

	private Point scaledCenterPoint = CENTRAL_POINT;
	private Point cursorOffset = POINTER_OFFSET;

	@Delegate
	private final ProviderCallbacks callbacks = new EmptyProviderCallbacks() {
		@Override
		public void onScaleChange(double cursorScale, double itemScale)
		{
			updateScale();
		}
	};

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
		if (!plugin.isOverlayV2())
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
				(clientUi.getCurrentCursor().getName().equals(BLANK_CURSOR_NAME) && nextState.getCursor() != null && !nextState.isExternalCursor());

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

		BufferedImage cursorImage = spriteImageOrNull(currentState.getCursorSprite());
		if (cursorImage != null)
		{
			graphics.drawImage(cursorImage, mousePos.getX(), mousePos.getY(), null);
		}

		BufferedImage backgroundImage = spriteImageOrNull(currentState.getCursorBackground());
		if (backgroundImage != null)
		{
			graphics.drawImage(backgroundImage, mousePos.getX() + cursorOffset.getX(), mousePos.getY() + cursorOffset.getY(), null);
		}

		BufferedImage foregroundImage = spriteImageOrNull(currentState.getCursorForeground());
		if (foregroundImage != null)
		{
			final int spriteX = cursorOffset.getX() + scaledCenterPoint.getX() - foregroundImage.getWidth(null) / 2;
			final int spriteY = cursorOffset.getY() + scaledCenterPoint.getY() - foregroundImage.getHeight(null) / 2;
			graphics.drawImage(foregroundImage, mousePos.getX() + spriteX, mousePos.getY() + spriteY, null);
		}
	}

	private BufferedImage spriteImageOrNull(Sprite sprite)
	{
		return sprite != null ? sprite.getImage(spriteContext) : null;
	}

	// TODO: Move to CursorProvider?
	public void updateScale()
	{
		scaledCenterPoint = scalePoint(CENTRAL_POINT, plugin.getCursorScale());
		cursorOffset = scalePoint(POINTER_OFFSET, plugin.getCursorScale());
	}

}
