<h1><img style="float:left;padding:10px;" src="https://user-images.githubusercontent.com/2979691/138060493-b0d38aa5-4c14-4974-8d5c-a2fddb64c846.png">
Contextual Cursor</h1>
Remember how RSHD had a custom mouse cursor when you hovered over stuff? Well it's (mostly) back!

![Install Count](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/contextual-cursor) ![Install Count](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/contextual-cursor)

### Configuration
- Ignores - Disables the contextual cursor from appear on certain targets
    - NPCs, Objects, Ground Items, Inventory Items and/or Spells
- Scale - Scale up or down the size of the contextual cursor
    - While scaling is supported, the quality of scaled cursors is not guaranteed. Whole numbers work best to preserve the quality
      - The scaling of the default cursor is also limited unless `Use overlay for default cursor` is turned on
    - There is also a smooth scaling option which may help for non-whole numbers
    - Item sprites also have a separate scale and smooth scaling configuration since they're a bit awkward at full size
- Theme - Styles the contextual cursor frame/background
    - RuneScape 2 (default)
    - OldSchool (custom sprites provided by [Mark7625](https://github.com/Mark7625) that match the osrs UI)
- Default cursor
  - Override the default mouse cursor (if custom cursor plugin is OFF) to match your theme
  - Use overlay for default cursor - replaces the default cursor with a RuneLite overlay
    - The main benefits of this are: Better scaling (size isn't limited by the OS) and improved color support 
    (Java **significantly** whitewashes the default cursor colors)
    - The only real drawback is that the overlay is slightly slower than the actual cursor speed, so it will lag behind slightly. 
    **Typically, this is not noticeable.**
    - Also note: The overlay cursor will not be visible on the RL side panel or when you are logged out (will default to system cursor).
- Misc
  - Hide background - hides the contextual cursor background, so you'll see your regular mouse cursor plus a floating sprite on hover
  - Use Item - shows an item in the cursor when using two items on each other
  - Persist Items - When an item is selected for use always show the cursor (even if you aren't hovering another item)
  - Persist Spells - When a spell is selected for manual cast always show the cursor (even if you aren't hovering a target)

### Missing Features vs RSHD (and other known issues):
* RL tooltips are partially underneath the cursor when they're displayed below the cursor
* Many things that should have contextual cursors don't. **Feel free to make an issue suggesting them.**
* Some menu options may have overlapping functions in different places which currently result in mismatched cursors. **Feel free to make an issue about any you find.**
* Submenus have a few small unavoidable bugs that you will likely never notice
  * If you hover over multiple menu entries that create submenus that are close together (i.e. forestry backpack/basket) and don't actually trigger the opening of the 2nd one (but do hover over it) it may show the icon for the not open submenu
  * If you pass over a menu entry that opens a submenu and then proceed to the one next to it (closing the menu), then moving your cursor slightly out of the menu (without closing it) you may see the submenu icon pop up
