# Brightness toggle Quick Settings 
- requires Nougat (Android 7)
- add a brightness toggle tile to quick settings
- useful when the quick settings is in compact mode (without the brightness sliders)

## TODOS
- toggle through brightness steps: 
  - [x] use predefined / hardcoded steps
  - UI to configure the steps
    - [x] UI prototype 
    - Implement persistence
- [x] Use percentage consistent with system default brightness slider UI
  - [x] standalone conversion with unit test
  - [x] unit test refactor: https://github.com/junit-team/junit4/wiki/Parameterized-tests , https://github.com/junit-team/junit4/wiki/Exception-testing
  - [x] integrate with TileService
- Try to increase snappiness of brightness change upon click
  - v0.1.9: base (try to update icon if applicable)
  - v0.1.10 : do not update tile at all
  - v0.1.11 : update only state (no UI changes)
  - v0.1.12 : update tile icon, but not state
  - v0.1.14 : update icon with AsyncTask (that only update UI)
  - v0.1.15 : an empty tile (still calling `tile.updateTile()`)
  - Conclusion: 
    - *any* tileupdate, even an empty one, could stall UI intermittently
      - by stalling, it means that after clicking the tile, occasionally
        it does not accept further clicks for a few seconds, making cycling
        through brightness a tad unpleasant.
      - the reason seems to be when tere is a tile update, Android would occasionally
        stop listening to tile (calling `#onStopListening()`). A few seconds later,
        it would start listening again (calling `#onStartListening()`)
    - using AsyncTask brings no noticable change
- [x] Debug build use a different name. See https://stackoverflow.com/a/41301021
- [x] Get a relase build (requires a certificate)
- UI to request WRITE_SETTINGS permission
- drawing percentage over the tile icon?
- Refactor some codes with closures, which requires using Java 8 (jack toolchain)
- (Not possible) NoUse SVG for launch icon
