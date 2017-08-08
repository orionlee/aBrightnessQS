# Brightness toggle Quick Settings 
- requires Nougat (Android 7)
- add a brightness toggle tile to quick settings
- useful when the quick settings is in compact mode (without the brightness sliders)

## TODOs
- [x] toggle through brightness steps basics: 
  - [x] use predefined / hardcoded steps
  - [x] UI to configure the steps
    - [x] UI prototype 
    - [x] Implement persistence
- [x] Use percentage consistent with system default brightness slider UI
  - [x] standalone conversion with unit test
  - [x] unit test refactor: https://github.com/junit-team/junit4/wiki/Parameterized-tests , https://github.com/junit-team/junit4/wiki/Exception-testing
  - [x] integrate with TileService
- [x] Improve responsiveness by implementing background service to monitor brightness 
  - [x] change Tile service to Active Tile
  - [x] barebone service 
  - [x] auto start on boot
  - [x] start service upon re-installation as well.
- [x] Use a linear Percentage Converter for now (Motorola devices)
  - [x] show percentage on tile UI
- [ ] Different devices have differing brightness slider implementation. Consider adapt percentage to differing devices. E.g., 
     - a slider as perceived brightness, which has a non-linear relationship with  underlying system brightness value, found in ASUS tables
     - a slider that has a simple linear relationship to underlying system brightness, found in Motorola phones.
- [x] Misc. UI Polish
  - [x] settings error reporting (for invalid level string)
  - [x] show current percentage at MainActivity UI
- [x] UI to request WRITE_SETTINGS permission
- [ ] Misc. code cleanup refactoring changes
  - [x] review context usage for memory leak (no-op)
- [x] Experiment to try to increase snappiness of brightness change upon click
  - v0.1.9: base (try to update icon if applicable)
  - v0.1.10 : do not update tile at all
  - v0.1.11 : update only state (no UI changes)
  - v0.1.12 : update tile icon, but not state
  - v0.1.14 : update icon with AsyncTask (that only update UI)
  - v0.1.15 : an empty tile (still calling `tile.updateTile()`)
  - Conclusion: 
    - *any* tile update, even an empty one, could stall UI intermittently
    - using AsyncTask brings no noticeable change
    - Reasons
      - by stalling, it means that after clicking the tile, occasionally
        it does not accept further clicks for a few seconds, making cycling
        through brightness a tad unpleasant.
      - the reason seems to be when there is a tile update, Android would occasionally
        stop listening to tile (calling `#onStopListening()`). A few seconds later,
        it would start listening again (calling `#onStartListening()`). 
      - In the interval between stop and start, clicking on tile would be fruitless
      - For now, either live will the intermittent stalling, or do not update UI at all
        - One possible enhancement is to use `META_DATA_ACTIVE_TILE` 
- [x] Debug build use a different name. See https://stackoverflow.com/a/41301021
- [x] Get a release build (requires a certificate)
- [ ] Refactor some codes with closures, which requires using Java 8 (jack toolchain)
- (probably not desirable) drawing percentage over the tile icon?
- (Not possible) NoUse SVG for launch icon
