package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.menuentry.ContextualCursorState;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

/**
 * V2 worker overlay - this must be separate in order for the tooltips to be rendered correctly in front of the mouse tooltips plugin
 */
public class ContextualCursorV2WorkerOverlay extends Overlay
{
	@Inject
	private CursorProvider cursorProvider;
	@Inject
	private StateProvider stateProvider;
	@Inject
	private ContextualCursorPlugin plugin;
	@Inject
	private TooltipManager tooltipManager;

	@Inject
	ContextualCursorV2WorkerOverlay(CursorProvider cursorProvider, StateProvider stateProvider, ContextualCursorPlugin plugin,
									TooltipManager tooltipManager)
	{
		this.cursorProvider = cursorProvider;
		this.stateProvider = stateProvider;
		this.plugin = plugin;
		this.tooltipManager = tooltipManager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		// run before draw overlay
		setPriority(1.1f);
		// Also allow on world map and welcome screen
		drawAfterInterface(InterfaceID.TOPLEVEL_DISPLAY);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isOverlayV2() || plugin.isAltPressed())
		{
			return null;
		}

		// TODO: Confirm if this placement is ok
		cursorProvider.checkLastCursor();

		ContextualCursorState state = stateProvider.getState(plugin.getPreviousState());
		plugin.setNextState(state);

		// Tooltips must be rendered in OverlayLayer.ABOVE_WIDGETS in order to be before the mouse tooltips plugin
		//  while graphics need to be ALWAYS_ON_TOP to be above the right click menu
		if (state != null && state.getTooltip() != null)
		{
			tooltipManager.addFront(state.getTooltip());
		}

		return null;
	}
}
