<h1><img style="float:left;padding:10px;" src="https://user-images.githubusercontent.com/2979691/138060493-b0d38aa5-4c14-4974-8d5c-a2fddb64c846.png">
Contextual Cursor</h1>
Remember how RSHD had a custom mouse cursor when you hovered over stuff? Well it's (mostly) back!

![Install Count](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/contextual-cursor) ![Install Count](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/contextual-cursor)

### Missing Features vs RSHD (and other known issues):
* RL tooltips are partially underneath the cursor when they're displayed below the cursor
* Many things that should have contextual cursors don't. Feel free to make an issue suggesting them.
* Some menu options may have multiple functions in different places which currently result in mismatched cursors. Feel free to make an issue about any you find.
* Submenus have a few small unavoidable bugs that you will likely never notice
  * If you hover over multiple menu entries that create submenus that are close together (i.e. forestry backpack/basket) and don't actually trigger the opening of the 2nd one (but do hover over it) it may show the icon for the not open submenu
  * If you pass over a menu entry that opens a submenu and then proceed to the one next to it (closing the menu), then moving your cursor slightly out of the menu (without closing it) you may see the submenu icon pop up


### Configuration
- Ignores - Disables the contextual cursor from appear on certain targets
  - NPCs, Objects, Ground Items, Inventory Items and/or Spells
- Scale - Scale up or down the size of the contextual cursor
  - Scaling is supported but the quality of scaled cursors is not guaranteed
