import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IndexManager {
    Scanner scan = new Scanner(System.in);
    private static File curFile = null; //currently open index file

    //CREATE METHOD
    public void createOrOverwriteIndexFile() {
        System.out.println("Enter the name for the index file for creation/overwrite (do not include extension): ");
        String fileName = scan.next();

        //ensure file has idx entension
        if (!fileName.endsWith(".idx")) {
            fileName += ".idx";
        }

        File file = new File(fileName);

        try {
            //notifying if file exists or not
            if (file.exists()) {
                System.out.println("File already exists, overwriting to empty");
            } else {
                System.out.println("Creating new file");
            }

            //creating file
            try (FileWriter writer = new FileWriter(file)) {
                //empty write, creating empty file
            }

            System.out.println("File " + file.getAbsolutePath() + " created successfully");
            curFile = file; //setting to currently open file
        } catch (IOException e) {
            System.err.println("Error occured with creation " + e.getMessage());
        }
        addHeader(curFile); //add file header
    }

    //OPEN METHOD
    public void openIndexFile() {
        System.out.println("Enter the name of the index file to open (extension optional): ");
        String fileName = scan.next();

        //add .idx extension if not there
        if (!fileName.endsWith(".idx")) {
            fileName += ".idx";
        }

        File file = new File(fileName);

        //check if file exists
        if (file.exists() && file.isFile()) {
            curFile = file; //setting to currently open file
            System.out.println("File " + file.getAbsolutePath() + " opened successfully");
        } else {
            System.err.println("Error: The file '" + fileName + "' does not exist.");
        }
    }

    //ADD HEADER METHOD (to be used within createOrOverwriteIndexFile)
    public void addHeader(File f) {
        try (FileOutputStream fos = new FileOutputStream(f)) {
            //create 512 byte block
            byte[] block = new byte[512];

            //magic number (8 bytes)
            byte[] magicNumber = "4337PRJ3".getBytes();
            System.arraycopy(magicNumber, 0, block, 0, magicNumber.length);

            //write root node block id 0 (8 bytes)
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(0); //root node 0
            System.arraycopy(buffer.array(), 0, block, 8, 8);

            //write next node block id 1 (8 bytes)
            buffer.clear();
            buffer.putLong(1); //next block 1
            System.arraycopy(buffer.array(), 0, block, 16, 8);

            //all remaining bytes unused initialized to 0 by default

            //write block to file
            fos.write(block);
        } catch (IOException e) {
            System.err.println("An error occurred while creating or writing to the file: " + e.getMessage());
        }
        printHeaderContents(curFile);
    }

    ///////////////////PRINT HEADER
     public static void printHeaderContents(File f) {

        // Check if the file exists
        if (!f.exists()) {
            System.err.println("Error: The file does not exist.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] header = new byte[512];

            // Read the first 512 bytes (header)
            int bytesRead = fis.read(header);
            if (bytesRead < 512) {
                System.err.println("Error: The file header is incomplete or corrupted.");
                return;
            }

            // Parse and display the header contents
            // Magic Number
            String magicNumber = new String(Arrays.copyOfRange(header, 0, 8)).trim();
            System.out.println("Magic Number: " + magicNumber);

            // Root Node Block ID
            long rootNodeId = ByteBuffer.wrap(Arrays.copyOfRange(header, 8, 16)).getLong();
            System.out.println("Root Node Block ID: " + rootNodeId);

            // Next Block ID
            long nextBlockId = ByteBuffer.wrap(Arrays.copyOfRange(header, 16, 24)).getLong();
            System.out.println("Next Block ID: " + nextBlockId);

            // Unused bytes
            System.out.println("Unused Bytes: " + Arrays.toString(Arrays.copyOfRange(header, 24, 512)));

        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}
