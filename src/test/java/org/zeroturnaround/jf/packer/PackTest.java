package org.zeroturnaround.jf.packer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ee.ut.jf2016.packer.PackerTestUtil;

public class PackTest {

  @Rule
  public LoggingRule loggingRule = new LoggingRule();

  private final Packer packer = new LoggingPacker(new UncompressedDataPacker());

  private Path tempDir;

  private Path inputDir;

  private Path outputArchive;

  @Before
  public void init() throws IOException {
    tempDir = Files.createTempDirectory("jf-hw-packer");
    inputDir = Files.createDirectory(tempDir.resolve("src"));
    outputArchive = tempDir.resolve("archive");
  }

  @After
  public void destroy() throws IOException {
    FileUtils.forceDelete(tempDir.toFile());
  }

  // Failures

  @Test(expected = Exception.class)
  public void testNullDir() throws IOException {
    packer.pack(null, outputArchive);
  }

  @Test(expected = Exception.class)
  public void testNullArchive() throws IOException {
    packer.pack(inputDir, null);
  }

  @Test(expected = Exception.class)
  public void testMissingDir() throws IOException {
    packer.pack(tempDir.resolve("dir"), outputArchive);
  }

  @Test(expected = Exception.class)
  public void testFileInsteadOfDir() throws IOException {
    packer.pack(Files.createFile(tempDir.resolve("foo")), outputArchive);
  }

  @Test(expected = Exception.class)
  public void testDirInsteadOfFile() throws IOException {
    packer.pack(tempDir, Files.createDirectory(outputArchive));
  }

  // Success

  @Test
  public void testEmptyDir() throws IOException {
    packAndVerify();
  }

  @Test
  public void testSingleFile() throws IOException {
    Files.write(inputDir.resolve("foo"), "bar".getBytes(StandardCharsets.UTF_8));
    packAndVerify();
  }

  @Test
  public void testSourceExistsAfterPack() throws IOException {
    byte[] data = "bar".getBytes(StandardCharsets.UTF_8);
    Path src = Files.write(inputDir.resolve("foo"), data);
    packAndVerify();
    Assert.assertTrue("Source file '" + src + "' is not found.", Files.exists(src));
    Assert.assertTrue("Source file '" + src + "' has changed.", Arrays.equals(data, Files.readAllBytes(src)));
  }

  @Test
  public void testSingleFileInSubDir() throws IOException {
    Files.write(Files.createDirectory(inputDir.resolve("sub")).resolve("foo"), "123".getBytes(StandardCharsets.UTF_8));
    packAndVerify();
  }

  @Test
  public void testTwoSmallFiles() throws IOException {
    Files.write(inputDir.resolve("foo"), "bar".getBytes(StandardCharsets.UTF_8));
    Files.write(Files.createDirectory(inputDir.resolve("sub")).resolve("hello"), "abcdef".getBytes(StandardCharsets.UTF_8));
    packAndVerify();
  }

  @Test
  public void testTwoBigFilesWithSimpleSizes() throws IOException {
    RandomFile.create(inputDir.resolve("random1"), 4L * FileUtils.ONE_MB);
    RandomFile.create(inputDir.resolve("random2"), 4L * FileUtils.ONE_MB);
    packAndVerify();
  }

  @Test
  public void testTwoBigFilesWithCustomSizes() throws IOException {
    RandomFile.create(inputDir.resolve("random1"), 10L * FileUtils.ONE_MB + 53243);
    RandomFile.create(inputDir.resolve("random2"), 7L * FileUtils.ONE_MB + 3412);
    packAndVerify();
  }

  @Test
  public void testBigFileWithSimpleSize() throws IOException {
    RandomFile.create(inputDir.resolve("random"), 100L * FileUtils.ONE_MB);
    packAndVerify();
  }

  @Test
  public void testBiggerFileWithCustomSize() throws IOException {
    RandomFile.create(inputDir.resolve("random"), 10L * FileUtils.ONE_MB + 123);
    packAndVerify();
  }

  private void packAndVerify() throws IOException {
    packer.pack(inputDir, outputArchive);
    PackerTestUtil.verifyPack(inputDir, outputArchive);
  }

}
