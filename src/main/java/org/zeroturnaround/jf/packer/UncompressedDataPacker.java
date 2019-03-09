package org.zeroturnaround.jf.packer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static java.lang.Math.toIntExact;

public class UncompressedDataPacker implements Packer {

    private static final Logger log = LoggerFactory.getLogger(UncompressedDataPacker.class);

    @Override
    public void pack(Path inputDir, Path outputArchive) throws IOException {
        if (!Files.isDirectory(inputDir)) {
            log.error("Input path is not a directory!");
            throw new IOException();
        }
        if (outputArchive == null) {
            log.error("Input path or output path cannot be null!");
            throw new NullPointerException();
        }
        try (OutputStream out = Files.newOutputStream(outputArchive)) {
            out.write((byte) 42);
        }

        log.info("Packing {} into {}", inputDir, outputArchive);
        try (Stream<Path> s = Files.walk(inputDir)) {
            s.filter(Files::isRegularFile)
                    .forEachOrdered(f -> writeToDestination(f, outputArchive, inputDir));
        }
    }

    private void writeToDestination(Path fileInputPath, Path outputFilePath, Path originalFolderPath) {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(fileInputPath));
             OutputStream out = new BufferedOutputStream(Files.newOutputStream(outputFilePath, StandardOpenOption.APPEND))) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(out)) {
                dataOutputStream.writeUTF(FilenameUtils.separatorsToUnix(originalFolderPath.relativize(fileInputPath).toString()));
                dataOutputStream.writeLong(fileInputPath.toFile().length());
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public void unpack(Path inputArchive, Path outputDir) throws IOException {
        if (inputArchive == null || outputDir == null) {
            log.error("Input path or output path cannot be null!");
            throw new NullPointerException();
        }
        if (!Files.isRegularFile(inputArchive)) {
            log.error("Input path is not a file!");
            throw new IOException();
        }

        log.info("Unpacking {} into {}", inputArchive, outputDir);
        Files.createDirectories(outputDir);
        try (InputStream in = new BufferedInputStream(Files.newInputStream(inputArchive))) {
            try (DataInputStream dataInputStream = new DataInputStream(in)) {
                int result = dataInputStream.readByte();
                if (result == 42) {
                    System.out.println("result is 42");
                    while (dataInputStream.available() > 0) {
                        String relativeFilePath = dataInputStream.readUTF();
                        Path filePath = Paths.get(FilenameUtils.separatorsToUnix(outputDir.resolve(relativeFilePath).normalize().toString()));
                        Long fileLength = dataInputStream.readLong();
                        writeAFile(filePath, fileLength, dataInputStream);
                    }
                } else {
                    throw new IOException("Wrong archive type!");
                }
            }
        }
    }

    private void writeAFile(Path filePath, Long fileLength, DataInputStream dataInputStream) throws IOException {
        if (Files.exists(filePath)) {
            log.info("filepath exists");
            FileUtils.cleanDirectory(filePath.toFile());
        } else {
            log.info("filepath doesnt exist!");
            Files.createDirectories(filePath.getParent());
        }
        Files.createFile(filePath);
        try (OutputStream outputStream = new BufferedOutputStream((Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)))) {
            byte[] buffer = new byte[4096];
            int bytesLeft = toIntExact(fileLength);
            while (bytesLeft > 0) {
                int read = dataInputStream.read(buffer, 0, Math.min(bytesLeft, buffer.length));
                if (read == -1) {
                    log.error("Unexpected end of data");
                    throw new EOFException("Unexcpected end of date");
                }
                outputStream.write(buffer, 0, read);
                bytesLeft -= read;
            }
        }
    }
}
