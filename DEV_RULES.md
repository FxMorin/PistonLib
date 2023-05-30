# The Dev Rules
## A list of all the things not to do, in order for pistons to work perfectly

### Block Dupes
#### Not following these rules will result in piston dupe bugs when the config option `pistonPushingCacheFix` is `false`, and possibly also when `tntDupingFix` is `false` using coral fans!
- Don't make **any** world changes within `onRemove()`
- Don't spawn entities within `neighborChanged()`
- Blocks that drop as items when set to air, and can be set to air during `updateShape`  

All if these can be done if you first check to make sure `PistonLibConfig.pistonPushingCacheFix` is `true`  
Most people with `pistonPushingCacheFix` set to `false` probably want the duping behavior. So these rules are optional!

### Config Options
- Don't modify the config values directly! Use the Getters & Setters provided by the config manager. I will flame you hard if you don't follow this one
- Don't change config options anywhere within `MinecraftServer.tick()`
- If using a custom `Observer` in `@ConfigValue`, make sure `onChange()` does not infinitely recurse using `parsedValue.setValue(value)`, try using `Parser.modify()` when possible instead

### Common Issues
Move this to a FAQ md later

#### All my pistons are immovable!
This means you didn't build DataGen, and the pistons are not within the `pistonlib:pistons` block tag list.  
Run the `Data generation` task

#### All the pistons are missing textures!
This means that either you didn't build DataGen, and the pistons are not within the `pistonlib:pistons` block tag list.
Or that you didn't register your pistons using DataGen  
Run the `Data generation` task