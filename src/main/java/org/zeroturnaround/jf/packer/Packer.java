package org.zeroturnaround.jf.packer;

import java.io.IOException;
import java.nio.file.Path;

public interface Packer {

  void pack(Path inputDir, Path outputArchive) throws IOException;

  void unpack(Path inputArchive, Path outputDir) throws IOException;

}
