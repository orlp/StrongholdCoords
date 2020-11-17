# StrongholdCoords

An application for finding out the exact coordinates of end
portal frames in Minecraft seeds.

Usage: `StrongholdCoords <mcversion> <seedfile> [<num_strongholds>]`

Seedfile must be a file of seeds, one per line. If not specified the
number of strongholds will be 3 - the inner ring of strongholds.

The output is on stdout, one line per per portal frame, in the format

    <seed>,<portal_x>,<portal_z>
    
    
Is only possible due to the hard work of KaptainWutax who implemented
the stronghold generation code: https://github.com/KaptainWutax/FeatureUtils.
