package dev.mayaqq.estrogen.client.cosmetics.models;

import de.javagl.obj.*;
import dev.mayaqq.estrogen.client.cosmetics.BakedCosmeticModel;
import dev.mayaqq.estrogen.client.cosmetics.CosmeticModel;
import dev.mayaqq.estrogen.client.cosmetics.CosmeticModelBakery;
import dev.mayaqq.estrogen.client.cosmetics.DownloadedAsset;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.io.FileUtils;
import org.joml.Vector3f;

import java.io.*;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ObjCosmeticModelBakery {

    public static CompletableFuture<BakedCosmeticModel> prepareAndBake(Obj obj, String urlPrefix, Executor executor) {

        // TODO: mtls
//        List<String> mtlFiles = obj.getMtlFileNames();
//        CompletableFuture<List<Mtl>> mtlsFuture = null;
//
//        for (String mtlFile : mtlFiles) {
//            String fileLoc = urlPrefix + "/" + mtlFile;
//            MtlDownlaod downlaod = new MtlDownlaod(fileLoc);
//            if (mtlsFuture == null) {
//                mtlsFuture = downlaod.load();
//            } else {
//                mtlsFuture = mtlsFuture.thenCombineAsync(downlaod.load(), (mtls, mtls2) -> {
//                    mtls.addAll(mtls2);
//                    return mtls;
//                }, executor);
//            }
//        }

        return null; //CompletableFuture.completedFuture()
    }

    public static GroupedCosmeticModel bake(Obj obj) {
        Map<String, TransformableMesh> meshes = new Object2ObjectArrayMap<>();
        IntList data = new IntArrayList();

        for (int i = 0; i < obj.getNumGroups(); i++) {
            ObjGroup group = obj.getGroup(i);

            for (int j = 0; j < group.getNumFaces(); j++) {
                ObjFace face = group.getFace(i);
                for (int k = 0; k < face.getNumVertices(); k++) {
                    FloatTuple pos = obj.getVertex(face.getVertexIndex(k));
                    FloatTuple uv = obj.getTexCoord(face.getTexCoordIndex(k));
                    FloatTuple normal = obj.getNormal(face.getNormalIndex(k));

                    data.add(Float.floatToRawIntBits(pos.getX()));
                    data.add(Float.floatToRawIntBits(pos.getY()));
                    data.add(Float.floatToRawIntBits(pos.getZ()));
                    data.add(Float.floatToRawIntBits(uv.getX()));
                    data.add(Float.floatToRawIntBits(1 - uv.getY()));
                    data.add(CosmeticModelBakery.packNormal(normal.getX(), normal.getY(), normal.getZ()));

                }
            }
            meshes.put(group.getName(), new TransformableMesh(data.toIntArray(), data.size() / CosmeticModelBakery.STRIDE, null));
            data.clear();
        }
        return new GroupedCosmeticModel(meshes, new Vector3f(), new Vector3f());
    }



    public static class MtlDownlaod {

        public final File file;
        public final String url;

        public MtlDownlaod(String url) {
            this.url = DownloadedAsset.getUrlHash(url);
            this.file = CosmeticModel.CACHE.resolve(this.url).toFile();
        }

        public CompletableFuture<List<Mtl>> load() {
            if(file != null && file.isFile()) {
                try(FileInputStream stream = FileUtils.openInputStream(file)) {
                    return readMtls(stream).map(CompletableFuture::completedFuture)
                        .orElseGet(() -> CompletableFuture.completedFuture(List.of()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return DownloadedAsset.runDownload(
                    url,
                    file,
                    this::readMtls
                ).thenApplyAsync(Optional::get);
            }
        }

        private Optional<List<Mtl>> readMtls(InputStream stream) {
            try {
                return Optional.of(MtlReader.read(stream));
            } catch (Exception ex) {
                return Optional.empty();
            }
        }
    }
}
