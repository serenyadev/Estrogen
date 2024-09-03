package dev.mayaqq.estrogen.client.cosmetics.models;

import de.javagl.obj.*;
import dev.mayaqq.estrogen.client.cosmetics.CosmeticModel;
import dev.mayaqq.estrogen.client.cosmetics.CosmeticModelBakery;
import dev.mayaqq.estrogen.client.cosmetics.DownloadedAsset;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ObjCosmeticModelBakery {

    public static CompletableFuture<GroupedBakedCosmeticModel> prepareAndBake(Obj obj, String urlPrefix, String name, Executor executor) {

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

        return CompletableFuture.supplyAsync(() -> CosmeticModelBakery.bakeObj(obj, name));
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
