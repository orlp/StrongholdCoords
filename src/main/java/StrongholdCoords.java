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
import kaptainwutax.seedutils.util.BlockBox;
import kaptainwutax.featureutils.structure.generator.piece.stronghold.PortalRoom;


class StrongholdCoords {
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
                    gen.generate(seed, starts[i].getX(), starts[i].getZ());

                    for (Stronghold.Piece piece : gen.pieceList) {
                        if (piece instanceof PortalRoom) {
                            PortalRoom pr = (PortalRoom) piece;
                            BlockBox bb = pr.getBoundingBox();
                            int x = (bb.minX + bb.maxX) / 2;
                            int z = (bb.minZ + bb.maxZ) / 2;
                            System.out.println(String.format("%d,%d,%d", seed, x, z));
                        }
                    }
                }
            }
        } catch(IOException e) {
            System.out.println(String.format("Error while reading file \"%s\".", args[1]));
            e.printStackTrace();
            return;
        }
    }
}
