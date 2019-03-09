package org.zeroturnaround.jf.packer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RandomFile {

  private static final Logger log = LoggerFactory.getLogger(RandomFile.class);

  static Path create(Path file, long size) throws IOException {
    Random random = new Random();
    byte[] buffer = new byte[4096];
    long fullChunks = size / buffer.length;
    int lastSize = (int) (size % buffer.length);
    log.debug("Generating random file at {}", file);
    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
      for (long i = 0; i < fullChunks; i++) {
        random.nextBytes(buffer);
        out.write(buffer);
      }
      random.nextBytes(buffer);
      out.write(buffer, 0, lastSize);
    }
    log.debug("Wrote {} bytes to {}", size, file);
    return file;
  }

}
