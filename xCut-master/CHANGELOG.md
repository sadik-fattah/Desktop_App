# Changelog
All notable changes to this project will be documented in this file.

## [1.0.0] - 2018-06-08
### Added
- Basic features
- Protection against arp scanners
- Per-device poisoning
- poison all network

## [1.0.1] - 2018-06-09
### Fixed
- Interuption during startup causes undefined behavior

### Added
- Poison all network except the gateway

## [1.0.2] - 2018-06-18
### Added
- Dynamic version from changelog
- Using tcpkill to terminate all connections of target

### Changed
- Spinner busy indicator

### Fixed
- Arpspoofing both target and host
- better killing procedure to subprocesses

## [1.0.3] - 2018-07-03
### Added
- Re-Scanning Feature without closing the app

### Changed
- Improved UX

## [1.0.4] - 2018-07-21
### Added
- both ways arp poisoning
- INSTALL, UPDATE and UNINSTALL scripts
- Install from sources using one command
- `xx-update` command to update the application from command line
- `xx-uninstall` command to completely remove xCut

### Changed
- UI

### Fixed
- killing procedure -> using PIPE signal

## [1.0.5] - 2018-08-03
### Fixed
- killing procedure -> non-blocking kill

### Changed
- UI

## [1.0.6] - 2020-03-10
### Bugs
- Scanning devices

### Changed
- Remove net discover and add nmap
