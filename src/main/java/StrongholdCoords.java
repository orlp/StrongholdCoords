import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;

import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.featureutils.structure.generator.StrongholdGenerator;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.lcg.rand.JRand;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.util.BlockBox;
import kaptainwutax.featureutils.structure.generator.piece.stronghold.PortalRoom;
import kaptainwutax.seedutils.util.math.Vec3i;
import kaptainwutax.featureutils.structure.generator.piece.StructurePiece;
import kaptainwutax.seedutils.util.Direction.*;


class StrongholdCoords {
    public static BlockBox getPortalBB(PortalRoom room) {
        Vec3i mins = applyVecTransform(room, new Vec3i(4,0,9));
        Vec3i maxes = applyVecTransform(room, new Vec3i(6,0,11));
        return new BlockBox(mins, maxes);
    }

    protected static int applyXTransform(StructurePiece<?> piece, int x, int z) {
        if (piece.getFacing() == null) return x;

        switch (piece.getFacing()) {
            case NORTH:
            case SOUTH:
                return piece.getBoundingBox().minX + x;
            case WEST:
                return piece.getBoundingBox().maxX - z;
            case EAST:
                return piece.getBoundingBox().minX + z;
            default:
                return x;
        }
    }

    protected static int applyZTransform(StructurePiece<?> piece, int x, int z) {
        if (piece.getFacing() == null) return z;

        switch (piece.getFacing()) {
            case NORTH:
                return piece.getBoundingBox().maxZ - z;
            case SOUTH:
                return piece.getBoundingBox().minZ + z;
            case WEST:
            case EAST:
                return piece.getBoundingBox().minZ + x;
            default:
                return z;
        }
    }

    protected static Vec3i applyVecTransform(StructurePiece<?> piece, Vec3i vector) {
        int x = vector.getX(), z = vector.getZ();
        return new Vec3i(applyXTransform(piece, x, z), 0, applyZTransform(piece, x, z));
    }




    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: <mcversion> <seedfile> [<num_strongholds>]\n\nSeedfile must be a file of seeds, one per line.");
            return;
        }

        MCVersion version = MCVersion.fromString(args[0]);
        if (version == null) {
            System.out.println(String.format("Unknown version \"%s\".", args[0]));
            return;
        }

        int count = 3;
        if (args.length >= 3) {
            count = Integer.parseInt(args[2]);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(args[1]));  
            String line = null;  
            while ((line = br.readLine()) != null) {  
                line = line.trim();
                if (line.length() == 0) continue;

                long seed = Long.parseLong(line);
                OverworldBiomeSource bs = new OverworldBiomeSource(version, seed);
                Stronghold stronghold = new Stronghold(version);

                CPos[] starts = stronghold.getStarts(bs, count, new JRand(0L));
                for (int i = 0; i < starts.length; ++i) {
                    StrongholdGenerator gen = new StrongholdGenerator(version);
                    gen.generate(seed, starts[i].getX(), starts[i].getZ(), new ChunkRand(), piece -> {
                        if (piece instanceof PortalRoom) {
                            PortalRoom pr = (PortalRoom) piece;
                            Vec3i portal_center = applyVecTransform(pr, new Vec3i(5, 0, 10));
                            int x = portal_center.getX();
                            int z = portal_center.getZ();
                            System.out.println(String.format("%d,%d,%d", seed, x, z));
                            return false;
                        }

                        return true;
                    });
                }
            }
        } catch(IOException e) {
            System.out.println(String.format("Error while reading file \"%s\".", args[1]));
            e.printStackTrace();
            return;
        }
    }
}
