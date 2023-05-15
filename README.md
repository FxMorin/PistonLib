## PistonLib
### A mod/library that rewrites the piston system while keeping the vanilla mechanics and feel

**This mod/library is a work in progress!**  
I don't have much time to work on this, so any time your willing to give to help is great.

### TODO
- Make more of the API public by moving it into the API directory
- Major Cleanup
- JavaDoc
- WIKI
- Blocks that can be placed on slime but break when the slime moves should not break if the block below has the `fused` sticky property
- Dual-sided piston
- Quarter Slime blocks
- Integrate GameTest **MAJOR**

### In Progress
- /pistonlib command to push/pull blocks with commands, override piston behavior for any block, and change config options in-game
- Piston Crushing, json recipe system that allows item entities to be crushed by pistons to turn into other blocks. Also entity crushing API

### Current Piston Features
- Configurable piston speed, per piston
- Configurable push limit, per piston
- Movable block entities
- Configurable sticky types
- Unlimited custom Sticky Groups (Slime, Honey, etc...)
- Individual sticky behaviour per block sides
- Pull-only piston logic
- Long piston's & Piston arms
- Large Quasi API, on all axis and any distance
- Piston behavior API, with new behaviors
- Piston stickiness API, with tons of new sticky types & sticky groups
- Piston Ticking API (Allow blocks to tick while being moved)
- Piston Merging/UnMerging API (Check the playlist xD)
- Tons of piston fixes for vanilla piston bugs. Which can all be toggled
- Piston Optimizations for Rendering and Computation
- Decoupled piston code. Piston wand to push blocks without pistons
- Indirect Sticky API

### Other Features
- Auto Crafting Table using piston merging
- Half Slime/Honey/Powered/Redstone Lamp/Obsidian block
- Glue Block (Strong sticky)
- Togglable sticky block
- Sticky chain (chainstone)
- Axis-Locked blocks (only movable on one axis)
- Move counting block (power level based on amount moved)
- All sided observer
- Slippery blocks (block fall when not attached to any solid blocks)
- Obsidian Slabs & Stairs
- Config system to toggle individual features
- Blocks can weight more than 1 block when pushed. Piston can only push 12 blocks in weight.

### Blocks that need textures
- axis_locked_block
- move_counting_block
- quasi_block
- weight_block
- most pistons
  
### Mods to make/update using the API
- [chains-link](https://www.curseforge.com/minecraft/mc-mods/chains-link)
- [More Pistons](https://www.curseforge.com/minecraft/mc-mods/more-pistons-jiraiyah-version)
- Player launcher pistons
- Colored Slime blocks & Honey Blocks

---

You can find a small amount of development progress in [this youtube playlist](https://www.youtube.com/embed/videoseries?list=PL3J0JOfWvCsvQNJqxBwXQnWM3b0sjXxAo)
[![PistonLib Development Playlist](https://img.youtube.com/vi/eukvh4gyeW0/0.jpg)](https://www.youtube.com/embed/videoseries?list=PL3J0JOfWvCsvQNJqxBwXQnWM3b0sjXxAo)

---

<a href="https://client.kinetichosting.net/aff.php?aff=42"><img alt="Kinetic Hosting" src="https://media.discordapp.net/attachments/1058184491476197427/1058799080672854126/FX.png"></a>
