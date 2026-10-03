TARGETS: ZipArchiveEntry.setUnixMode, getUnixMode, isUnixSymlink; UnixStat file-type mode constants.  
ORACLES: isUnixSymlink must be false when mode contains more than the symlink file-type flag.  
ORACLES: getUnixMode/setUnixMode and external attributes provide observable mode-state expectations.  
CASES: Set Unix symlink mode alone; expect isUnixSymlink true.  
CASES: Set symlink mode OR another file-type flag; expect isUnixSymlink false (trigger behavior).  
CASES: Set regular-file/directory modes; expect isUnixSymlink false.  
CASES: Verify Unix-mode round trip and platform/external-attribute effects where exposed.  
RISKS: Exact UnixStat constant names/values and intended masking semantics are not fully shown.