### 1.11

* TODO: Replace reflection with methodhandles

* Use direct proxies instead of ssl factories for multiple IP-addresses
* Remove local address check for multiple IP-addresses
* Fix parsing of local IP-addresses
* Fix address rotating for contacting the Mojang API
* Optimize issue template
* Use Instant for timestamps
* Migrate SLF4J logging (Fixes #177)
* Use Gson's TypeAdapter for more type safety
* Add support for IPv6 proxies
* Shared configuration implementation for easier maintained code
* Use Gson for json parsing, because it's supported on all platforms and removes code duplicates
* Clean up project code
* Drop support for deprecated AuthMe API
* Remove legacy database migration code
* Drop support for RoyalAuth, because it doesn't seem to be supported anymore
* Clean up client-server encryption -> use only one cipher per connection, simplify code

### 1.10

* Prevent authentication proxies
* Drop database importer
* More logging by default
* Add support for HTTP proxies
* Set the fake offline UUID on lowest priority (-> as soon as possible)
* Remove bungee chatcolor for Bukkit to support KCauldron
* Minor cleanup using inspections + Https
* Increase hook delay to let ProtocolLib inject the listener
* Drop support for old AuthMe API + Add support for new AuthMe API
* Remove eBean util usage to make it compatible with 1.12
* Do not try to hook into a plugin if auth plugin hook is already set using the FastLogin API
* Automatically register accounts if they are not in the auth plugin database but in the FastLogin database
* Update BungeeAuth dependency and use the new API. Please update your plugin if you still use the old one.
* Remove deprecated API methods from the last version
* Finally update the IP column on every login
* No duplicate session login
* Fix timestamp parsing in newer versions of SQLite
* Fix Spigot console command invocation sends result to in game players

### 1.9

* Added second attempt login -> cracked login
* Added cracked whitelist (switch-mode -> switching to online-mode from offlinemode)
* Added configuration to disable auto logins for 2Factor authentication
* Added missing add-premium-other message
* Upgrade to Java 8 -> Minimize file size
* Refactored/Cleaned up a lot of code
* [API] Deprecated platform specific auth-plugin. Please use AuthPlugin< platform specific player type >
* [API] Deprecated bukkit's password generator. Please use PasswordGenerator< platform specific player type >
* Fix ProtocolSupport autoRegister
* Fix update username in FastLogin database after nameChange
* Fix logging exceptions on encryption enabling
* Fix compatibility with older ProtocolLib versions (for 1.7) because of the missing getMethodAcccessorOrNull method
* Fix correct cracked permission for bukkit
* A try to fix SQLite timestamp parsing
* Drop support for LoginSecurity 1.X since 2.X seems to be stable
* Remove the nasty UltraAuth fakeplayer workaround by using a new api method. You should UltraAuth if you have it

### 1.8

* Added autoIn importer
* Added BFA importer
* Added ElDziAuth importer
* Fix third-party not premium player detection
* Fix ProtocolSupport BungeeCord
* Fix duplicate logins for BungeeAuth users

### 1.7.1

* Fix BungeeCord autoRegister (Fixes #46)
* Fix ProtocolSupport auto-register

### 1.7

* Added support for making requests to Mojang from different IPv4 addresses
* Added us.mcapi.com as third-party APIs to workaround rate-limits
* Fixed NPE in BungeeCord on cracked session
* Fixed skin applies if premium uuid is deactivated
* Fix player entry is not saved if namechangecheck is enabled
* Fix skin applies for third-party plugins
* Switch to mcapi.ca for uuid lookups
* Fix BungeeCord not setting an premium uuid
* Fix setting skin on Cauldron
* Fix saving on name change

### 1.6.2

* Fixed support for new LoginSecurity version

### 1.6.1

* Fix message typo in BungeeCord which created a NPE if premium-warning is activated

### 1.6

* Add a warning message if the user tries to invoke the premium command
* Added missing translation if the server isn't fully started
* Removed ProtocolLib as required dependency. You can use ProtocolSupport or BungeeCord as alternative
* Reduce the number of worker threads from 5 to 3 in ProtocolLib
* Process packets in ProtocolLib async/non-blocking -> better performance
* Fixed missing translation in commands
* Fixed cracked command not working on BungeeCord
* Fix error if forward skins is disabled

### 1.5.2

* Fixed BungeeCord force logins if there is a lobby server
* Removed cache expire in BungeeCord
* Applies skin earlier to make it visible for other plugins listening on login events

### 1.5.1

* Fixed BungeeCord support by correctly saving the proxy ids

### 1.5

* Added localization
* Fixed NPE on premium name check if it's pure cracked player
* Fixed NPE in BungeeCord on cracked login for existing players
* Fixed saving of existing cracked players

### 1.4

* Added Bungee setAuthPlugin method
* Added nameChangeCheck
* Multiple BungeeCord support

### 1.3.1

* Prevent thread create violation in BungeeCord

### 1.3

* Added support for AuthMe 3.X
* Fixed premium logins if the server is not fully started
* Added other command argument to /premium and /cracked
* Added support for LogIt
* Fixed 1.7 Minecraft support by removing guava 11+ only features -> Cauldron support
* Fixed BungeeCord support in Cauldron

### 1.2.1

* Fix premium status change notification message on BungeeCord

### 1.2

* Fix race condition in BungeeCord
* Fix dead lock in xAuth
* Added API methods for plugins to set their own password generator
* Added API methods for plugins to set their own auth plugin hook => Added support for AdvancedLogin

### 1.1

* Make the configuration options also work under BungeeCord (premiumUUID, forwardSkin)
* Catch configuration loading exception if it's not spigot build
* Fix config loading for older Spigot builds

### 1.0

* Massive refactor to handle errors on force actions safely
* force Methods now runs async too
* force methods now returns a boolean to reflect if the method was successful
* isRegistered method should now throw an exception if the plugin was unable to query the requested data

### 0.8

* Fixed BungeeCord support for the Bukkit module
* Added database storage to save the premium state
* Fix logical error on /premium (Thanks to @NorbiPeti)
* Fixed issues with host lookup from hosts file (Thanks to @NorbiPeti)
* Remove handshake listener because it creates errors on some systems

### 0.7

* Added BungeeAuth support
* Added /premium [player] command with optional player parameter
* Added a check if the player is already on the premium list
* Added a forwardSkin config option
* Added premium UUID support
* Updated to the newest changes of Spigot
* Removes the need of an Bukkit auth plugin if you use a bungeecord one
* Optimize performance and thread-safety
* Fixed BungeeCord support
* Changed config option auto-login to auto-register to clarify the usage

### 0.6

* Fixed 1.9 bugs
* Added UltraAuth support

### 0.5

* Added cracked command
* Added auto-login - See config
* Added config
* Added isRegistered API method
* Added forceRegister API method

* Fixed CrazyLogin player data restore -> Fixes memory leaks with this plugin
* Fixed premium name check to ProtocolSupport
* Improved permissions management

### 0.4

* Added forward premium skin
* Added plugin support for ProtocolSupport

### 0.3.2

* Run packet readers in a different thread (separated from the Netty I/O Thread)
  -> Improves performance
* Fixed Plugin disable if the server is in online mode but have to be in offline mode

### 0.3.1

* Improved BungeeCord security

### 0.3

* Added BungeeCord support
* Decrease timeout checks in order to fail faster on connection problems
* Code style improvements

### 0.2.4

* Fixed NPE on invalid sessions
* Improved security by generating a randomized serverId
* Removed /premium [player] because it's safer for premium players who join without registration

### 0.2.3

* Remove useless AuthMe force-login code
* Send a kick message to the client instead of just "Disconnect"
* Reformat source code
* Fix thread safety for fake start packets (Bukkit.getOfflinePlayer doesn't look like to be thread-safe)
* Added more documentation

### 0.2.2

* Compile project with Java 7 :(

### 0.2.1

* A couple of security fixes (premium players cannot longer steal the account of a cracked account)
* Added a /premium command to mark you as premium player

### 0.2

* Added support for CrazyLogin and LoginSecurity
* Now minecraft version independent
* Added debug logging
* Code clean up
* More state validation
* Added better error handling

### 0.1

* First release
