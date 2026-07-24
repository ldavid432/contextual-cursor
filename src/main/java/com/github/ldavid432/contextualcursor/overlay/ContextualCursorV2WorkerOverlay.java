package com.github.ldavid432.contextualcursor.overlay;

import com.github.ldavid432.contextualcursor.cursor.CursorProvider;
import com.github.ldavid432.contextualcursor.menuentry.ContextualCursorState;
import io.hydrox.contextualcursor.ContextualCursorPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Objects;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.MenuEntry;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;

/**
 * V2 worker overlay - this must be separate in order for the tooltips to be rendered correctly in front of the mouse tooltips plugin
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ContextualCursorV2WorkerOverlay extends Overlay
{
	private final CursorProvider cursorProvider;
	private final StateProvider stateProvider;
	private final ContextualCursorPlugin plugin;
	private final TooltipManager tooltipManager;
	private final MenuEntryProvider menuEntryProvider;

	@Inject
	void init()
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		// run before draw overlay
		setPriority(1.1f);
		// Also allow on world map and welcome screen
		drawAfterInterface(InterfaceID.TOPLEVEL_DISPLAY);
		menuEntryProvider.setListener(((menuEntry, isIgnored) -> {
			debugTooltip(isIgnored, menuEntry);
			return null;
		}));
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

	public void shutdown()
	{
		menuEntryProvider.setListener(null);
		// TODO: Reset cursor?
	}

	private void debugTooltip(boolean isIgnored, MenuEntry entry)
	{
		if (entry == null || !plugin.isDebugTooltipEnabled())
		{
			return;
		}

		String ignored = null;
		if (isIgnored)
		{
			ignored = ColorUtil.wrapWithColorTag("Ignored", Color.RED);
		}

		String name = String.format("option=%s, type=%s", entry.getOption(), entry.getType());
		if (entry.getTarget() != null && !entry.getTarget().isBlank())
		{
			name += String.format(", target=%s", entry.getTarget());
		}

		String item = null;
		if (entry.isItemOp() || entry.getItemId() > 0)
		{
			item = String.format("Item: isItemOp=%s, id=%s", entry.isItemOp(), entry.getItemId());
		}

		String npc = null;
		if (entry.getNpc() != null)
		{
			npc = String.format("NPC: %s", entry.getNpc().getName());
		}

		String player = null;
		if (entry.getPlayer() != null)
		{
			player = String.format("Player: %s", entry.getPlayer().getName());
		}

		tooltipManager.addFront(
			new Tooltip(
				Stream.of("Contextual Cursor Debug:", ignored, name, item, npc, player)
					.filter(Objects::nonNull)
					.reduce((s1, s2) -> s1 + "<br>" + s2)
					.orElse("Contextual Cursor Debug:<br>" + name)
			)
		);
	}
}
