## Features
*   HUD -- toggle with F4
    *   Mystics equipped -- shows a warning in the top right if you have perishable mystics (ie not fresh) in your inventory
    *   Day/Night cycle -- shows the current time left in the day/night
    *   Upcoming events -- Shows the next 5 upcoming events on screen
*   Inventory
    *   Fresh mystics have a blue dot; enchanted mystics have green dots corresponding to their tier
    *   Hovering over an item will show you its nonce and what color pants it will require for tier 3
*   Snooper
    *   Records mystic drops
    *   Records mystic enchants
    *   Data is sent to PitSplox and will eventually be made public
*   Chat pings
    *   In the sewers -- cat meows will play when a treasure spawns/is found so that way you don't miss it.
    *   When someone says your name in chat, there will be a sound.

## Building
1.  Add this to your systems' hosts file:
    ```
    192.99.194.128 export.mcpbot.bspk.rs
    ```
    This will fix an error where Gradle can't connect to MCPBot (which no longer exists)

2. Create a system environment variable JAVA_HOME_8 that points to your Java 8 JDK.
    Note that you may have to restart your computer after this step.

3. Run `./gradlew build` -- your compiled jar will appear in `build/libs`

## Contributing
If you have something you want to add, make a pull request!