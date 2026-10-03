TARGETS: chooseInitialCenters, getNearestCluster, getFarthestPoint,
EmptyClusterStrategy:LARGEST_VARIANCE/LARGEST_POINTS_NUMBER/FARTHEST_POINT
ORACLES: compare distances from point to cluster centers; check assigned cluster membership; verify
no empty cluster after strategy
ORACLES: centers must be distinct points from input; each point assigned to nearest center
CASES: normal: scattered points with varied distances; boundary: all points identical (distance=0),
only 1 point, k=1, k=n
CASES: boundary: points extremely close but not identical (sub-normal distance); very large number
of points vs small k
CASES: error: empty point set, null input, k<=0, k>points.size()
RISKS: no access to fixed-version code; bug likely in small-distance tie-breaking or center
selection leading to AssertionError