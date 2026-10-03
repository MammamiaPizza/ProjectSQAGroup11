TARGETS: TarArchiveOutputStream.finish, .close, .putArchiveEntry, .closeArchiveEntry
ORACLES: finish() after putArchiveEntry should auto-close entry and write EOF record; close() same;
stream yields valid tar archive after termination
CASES: normal: put+close+finish; open entry at finish (put then finish); double close; close after
finish; put+close+close; data-written but entry open at finish; multiple entries with trailing open;
empty archive finish
RISKS: parent class ArchiveOutputStream unknown; auto-close logic unclear; must verify EOF record
presence; no guarantee on exception behavior for unclosed entries