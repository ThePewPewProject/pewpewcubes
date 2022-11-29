package de.kleiner3.lasertag.dummy;

import de.kleiner3.lasertag.resource.WebResourceManager;
import de.kleiner3.lasertag.util.Tuple;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DummyResourceManager extends WebResourceManager {

    @Override
    public List<Tuple<Identifier, Resource>> getWebSite(Identifier ignored) {
        var list = new LinkedList<Tuple<Identifier, Resource>>();

        var baseDir = Path.of(System.getProperty("user.dir"),"src", "main", "resources", "assets", "lasertag", "web");


        try {
            var stream = Files.walk(baseDir);

            var paths = stream.filter(Files::isRegularFile).collect(Collectors.toList());

            for (var path : paths) {
                var idPath = path.toString().replace('\\', '/').split("lasertag/web/")[1];

                list.add(new Tuple<>(new Identifier(idPath), new Resource("lasertag", new Resource.InputSupplier<InputStream>() {
                    @Override
                    public InputStream get() throws IOException {
                        return new FileInputStream(path.toString());
                    }
                })));
            }

            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
