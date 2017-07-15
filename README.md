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
- (WIP) Use percentage consistent with system default brightness slider UI
  - [x] standalone conversion with unit test
  - unit test refactor: https://github.com/junit-team/junit4/wiki/Parameterized-tests , https://github.com/junit-team/junit4/wiki/Exception-testing
  - integrate with TileService
- [x] Debug build use a different name. See https://stackoverflow.com/a/41301021
- [x] Get a relase build (requires a certificate)
- UI to request WRITE_SETTINGS permission
- drawing percentage over the tile icon?
- Refactor some codes with closures, which requires using Java 8 (jack toolchain)
- (Not possible) NoUse SVG for launch icon
