package org.zeroturnaround.jf;

import org.zeroturnaround.jf.packer.Packer;
import org.zeroturnaround.jf.packer.UncompressedDataPacker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        /**if (args.length == 3) {
            String command = args[0].toLowerCase(Locale.ENGLISH);
            Path p1 = Paths.get(args[1]);
            Path p2 = Paths.get(args[2]);
            Packer packer = new UncompressedDataPacker();
            switch (command) {
            case "pack":
                packer.pack(p1, p2);
                return;
            case "unpack":
                packer.unpack(p1, p2);
                return;
            }
        }
        System.out.println("Usage: java -jar jf-homework4.jar ...");
        System.out.println("  pack <inputDir> <outputArchive>");
        System.out.println("  unpack <inputArchive> <outputDir>");
         **/
        Packer packer = new UncompressedDataPacker();
        //Path p1 = Paths.get(System.getProperty("user.home") + "/Desktop/test");
        //Path p2 = Paths.get(System.getProperty("user.home") + "/Desktop/test2");
        //packer.pack(p1, p2);
        Path p3 = Paths.get(System.getProperty("user.home") + "/Desktop/test2");
        Path p4 = Paths.get(System.getProperty("user.home") + "/Desktop/test3");
        try {
            packer.unpack(p3, p4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
