Java Fundamentals - I/O 
===========

Description
----------

A Java program which packs and unpacks files without compression. This is an old school assignment with Evosuite Automatic Test Generation Suite added for TU Delft course on Software Testing & Reverse Engineering.

The project has a set of unit tests which have to pass with limited memory (run `./mvnw clean test` from command line).

Running Evosuite
----------
Maven plugin is added, all you have to do is run:  
* `mvn -DmemoryInMB=2000 -Dcores=2 evosuite:generate evosuite:export  test`  
* After that you should find tests under `test/org/zeroturnaround/jf`

Original Requirements for This Program
----------

1. Support both relative and absolute paths.
2. Support directories recursively.
3. Create missing parent directories automatically.
4. Only pack files (i.e. an input directory tree consisting of only empty folders is packed as the minimum archive consisting of just the Archive Type as there are no File Chunks).
5. Use `DataInputSteam` and `DataOutputStream`.
6. Support big files that don’t fit into memory at once.
7. Buffer data for better performance.
8. Close all resources properly.
9. Use the newer java.nio.file API.

File Format of the Archive
----------

* Whole Archive = Archive Type + File Chunk(1) + File Chunk(2) + … + File Chunk(n)
* Archive Type = 42 (1 fixed byte)
* File Chunk = File Path + File Length + File Contents
* File Path – bytes of a String of a relative path in the archive separated by / characters (use `readUTF()`/`writeUTF()` methods)
* File Length – 8 bytes showing how many bytes does the File Contents take (big endian long)
* File Contents – actual file in the archive

Using Eclipse
-------------

If you are an Eclipse user then you can import the project to your workspace. *File* - *Import* - *Existing Maven Projects*.

To run the project in Eclipse you should ..... oh wait! You are already familiar with your IDE and I'm not supposed to explain this!