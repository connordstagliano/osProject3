import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class IndexManager {
    Scanner scan = new Scanner(System.in);
    private static File curFile = null; //currently open index file
    private static final int BLOCK_SIZE = 512;
    private static final int MAX_KEYS = 19;
    private static final int MAX_CHILDREN = 20;


    //////////////////////////////////////////////CREATE METHOD
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
                System.out.print("File already exists, overwrite? (yes/no): ");
                scan = new Scanner(System.in); //clear scanner
                String response = scan.nextLine();
                if (!response.equalsIgnoreCase("yes")) {
                    System.out.println("Creation cancelled");
                    return;
                }
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

    //////////////////////////////////////////////////OPEN METHOD
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
    }

    ////////////////////////////////////////////////////////PRINT METHODs
    public void printContents() {
        File file = curFile;
        if(file == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }

        if (!file.exists()) {
            System.err.println("Error: File does not exist");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            System.out.println("\nHEADER\n");
            printHeader(raf);

            System.out.println("\nINDEX");
            long fileLength = raf.length();
            int blockNumber = 1; //header is "block 0"
            while (BLOCK_SIZE * blockNumber <= fileLength) {
                System.out.println("\n\tBlock " + blockNumber + ":");
                printBlock(raf, blockNumber);
                blockNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void printHeader(RandomAccessFile raf) throws IOException {
        byte[] header = new byte[BLOCK_SIZE];
        raf.seek(0);
        raf.readFully(header);
        ByteBuffer buffer = ByteBuffer.wrap(header);

        //read header
        byte[] magicNumberBytes = new byte[8];
        buffer.get(magicNumberBytes);

        long rootBlockId = buffer.getLong();
        long nextBlockId = buffer.getLong();

        System.out.println("\tRoot Block ID: " + rootBlockId);
        System.out.println("\tNext Block ID: " + nextBlockId);
    }

    private static void printBlock(RandomAccessFile raf, int blockNumber) throws IOException {
        long offset = (long) (blockNumber - 1) * BLOCK_SIZE;
        raf.seek(offset);
    
        byte[] block = new byte[BLOCK_SIZE];
        raf.readFully(block);
        ByteBuffer buffer = ByteBuffer.wrap(block);
    
        //read block fields
        long blockId = buffer.getLong();
        long parentBlockId = buffer.getLong();
        long numKeyValuePairs = buffer.getLong();
    
        System.out.println("\tBlock ID: " + blockId);
        //System.out.println("Parent Block ID: " + parentBlockId);
        //System.out.println("Number of Key/Value Pairs: " + numKeyValuePairs);
    
        //read keys
        long[] keys = new long[19];
        for (int i = 0; i < 19; i++) {
            keys[i] = buffer.getLong();
        }
    
        //read values
        long[] values = new long[19];
        for (int i = 0; i < 19; i++) {
            values[i] = buffer.getLong();
        }
    
        //read child pointers
        long[] childPointers = new long[20];
        for (int i = 0; i < 20; i++) {
            childPointers[i] = buffer.getLong();
        }
    
        //print key, value pairs
        System.out.println("\tKey/Value Pairs:");
        for (int i = 0; i < 19; i++) {
            if (keys[i] != 0) { //print non zero keys
                System.out.println("\tKey: " + keys[i] + ", Value: " + values[i]);
            }
        }
    }

    ////////////////////////////////////////////////////////////////INSERT METHODs
    public void insertIntoBTree() {
        File file = curFile;
        if(file == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }

        if (!file.exists()) {
            System.err.println("Error: Index file does not exist");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long rootBlockId = readRootBlockId(raf);
            long nextBlockId = readNextBlockId(raf);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the key (unsigned integer): ");
            long key = Long.parseUnsignedLong(reader.readLine());
            System.out.print("Enter the value (unsigned integer): ");
            long value = Long.parseUnsignedLong(reader.readLine());

            if (rootBlockId == 0) {
                //if tree is empty
                createNewRoot(raf, key, value, nextBlockId);
            } else {
                if (!insertIntoNode(raf, rootBlockId, key, value)) {
                    System.out.println("Error: Key already exists in the tree");
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error during insertion: " + e.getMessage());
        }
    }

    private static long readRootBlockId(RandomAccessFile raf) throws IOException {
        raf.seek(8); //root block offset 8 in header
        return raf.readLong();
    }

    private static long readNextBlockId(RandomAccessFile raf) throws IOException {
        raf.seek(16); //next block offset 16 in header
        return raf.readLong();
    }

    private static void createNewRoot(RandomAccessFile raf, long key, long value, long nextBlockId) throws IOException {
        byte[] newBlock = new byte[BLOCK_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(newBlock);

        //fill new root block
        buffer.putLong(nextBlockId); //cur block id
        buffer.putLong(0); //parent block id (0 for parent of root)
        buffer.putLong(1); //number of key, value pairs
        buffer.putLong(key); //first key
        for (int i = 1; i < MAX_KEYS; i++) buffer.putLong(0); //remaining keys
        buffer.putLong(value); //first value
        for (int i = 1; i < MAX_KEYS; i++) buffer.putLong(0); //remaining values
        for (int i = 0; i < MAX_CHILDREN; i++) buffer.putLong(0); //child pointers

        //write new root to file
        raf.seek((nextBlockId - 1) * BLOCK_SIZE);
        raf.write(newBlock);

        //update header
        raf.seek(8); 
        raf.writeLong(nextBlockId);
        raf.seek(16); 
        raf.writeLong(nextBlockId + 1);

        System.out.println("Inserted key " + key + " with value " + value + " as new root");
    }

    private static boolean insertIntoNode(RandomAccessFile raf, long blockId, long key, long value) throws IOException {
        byte[] block = readBlock(raf, blockId);
        ByteBuffer buffer = ByteBuffer.wrap(block);

        //parse block
        buffer.position(16); //skip block and parent ids
        int numPairs = (int) buffer.getLong();
        long[] keys = new long[MAX_KEYS];
        long[] values = new long[MAX_KEYS];
        long[] children = new long[MAX_CHILDREN];

        for (int i = 0; i < MAX_KEYS; i++) keys[i] = buffer.getLong();
        for (int i = 0; i < MAX_KEYS; i++) values[i] = buffer.getLong();
        for (int i = 0; i < MAX_CHILDREN; i++) children[i] = buffer.getLong();

        //check if duplicate key
        for (int i = 0; i < numPairs; i++) {
            if (keys[i] == key) {
                System.out.println("Duplicate key, did not insert");
                return false; //key already in tree
            }
        }

        //find correct child pointer
        int childIndex = 0;
        while (childIndex < numPairs && key > keys[childIndex]) {
            childIndex++;
        }

        if (children[childIndex] == 0) {
            //insert into leaf
            insertKeyValueIntoLeaf(raf, blockId, key, value, keys, values, numPairs);
        } else {
            //go to child
            return insertIntoNode(raf, children[childIndex], key, value);
        }

        return true;
    }

    private static void insertKeyValueIntoLeaf(RandomAccessFile raf, long blockId, long key, long value,
                                               long[] keys, long[] values, int numPairs) throws IOException {
       //insert in sorted order in leaf
        int pos = numPairs;
        while (pos > 0 && keys[pos - 1] > key) {
            keys[pos] = keys[pos - 1];
            values[pos] = values[pos - 1];
            pos--;
        }
        keys[pos] = key;
        values[pos] = value;

        //update block
        byte[] block = new byte[BLOCK_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(block);

        buffer.putLong(blockId); //block id
        buffer.putLong(0); //parent block id
        buffer.putLong(numPairs + 1); //udpate num of key value pairs

        for (long k : keys) buffer.putLong(k);
        for (long v : values) buffer.putLong(v);
        for (int i = 0; i < MAX_CHILDREN; i++) buffer.putLong(0); 

        raf.seek((blockId - 1) * BLOCK_SIZE);
        raf.write(block);

        System.out.println("Inserted key " + key + " with value " + value + " into block " + blockId);
    }

    private static byte[] readBlock(RandomAccessFile raf, long blockId) throws IOException {
        byte[] block = new byte[BLOCK_SIZE];
        raf.seek((blockId - 1) * BLOCK_SIZE);
        raf.readFully(block);
        return block;
    }

    /////////////////////////////////////////////SEARCH METHODS
    public void searchCall(){
        File file = curFile;
        if(file == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }
        System.out.println("Enter search key: ");
        long searchKey = scan.nextLong();
        searchKeyInIndexFile(file, searchKey);
    }
    
    public void searchKeyInIndexFile(File file, long searchKey) {
    
        if (!file.exists()) {
            System.err.println("Error: File does not exist.");
            return;
        }
    
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            //find root block id
            byte[] header = new byte[512];
            raf.seek(0);
            raf.readFully(header);
            ByteBuffer headerBuffer = ByteBuffer.wrap(header);
    
            headerBuffer.position(8); 
            long rootBlockId = headerBuffer.getLong();
            if (rootBlockId == 0) {
                System.out.println("The tree is empty.");
                return;
            }
    
            //search tree starting at root
            if (searchKeyInBlock(raf, rootBlockId, searchKey)) {
                return;
            }
    
            System.out.println("Key not found.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    
    private static boolean searchKeyInBlock(RandomAccessFile raf, long blockId, long searchKey) throws IOException {
        long offset = (blockId - 1) * 512; //block offset calculation
        raf.seek(offset);
    
        byte[] block = new byte[512];
        raf.readFully(block);
        ByteBuffer buffer = ByteBuffer.wrap(block);
    
        //read block fields
        buffer.getLong(); //skip blokc ids
        buffer.getLong(); 
        long numKeyValuePairs = buffer.getLong();

        //read keys
        long[] keys = new long[19];
        for (int i = 0; i < 19; i++) {
            keys[i] = buffer.getLong();
        }
    
        //read values
        long[] values = new long[19];
        for (int i = 0; i < 19; i++) {
            values[i] = buffer.getLong();
        }
    
        //read children pointers
        long[] childPointers = new long[20];
        for (int i = 0; i < 20; i++) {
            childPointers[i] = buffer.getLong();
        }
    
        //search for the key
        for (int i = 0; i < numKeyValuePairs; i++) {
            if (keys[i] == searchKey) {
                System.out.println("Key: " + keys[i] + ", Value: " + values[i]);
                return true;
            }
    
            if (searchKey < keys[i] && childPointers[i] != 0) {
                //if searchkey less, go to left child
                return searchKeyInBlock(raf, childPointers[i], searchKey);
            }
        }
    
        //if search key greater, check last child pointer
        if (childPointers[(int) numKeyValuePairs] != 0) {
            return searchKeyInBlock(raf, childPointers[(int) numKeyValuePairs], searchKey);
        }
    
        return false;
    }

    /////////////////////////////////////EXTRACT METHODs
    public void exportIndexToFile() {
        File indexFile = curFile;
        if(indexFile == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }

        if (!indexFile.exists()) {
            System.err.println("Error: Index file does not exist");
            return;
        }

        Scanner scan2 = new Scanner(System.in); //wasnt working when trying to use the scanner defined at top of class

        //output file name prompt
        System.out.print("Enter the name of the file to export to: ");
        String exportFileName = scan2.nextLine();
        File exportFile = new File(exportFileName);
        //here is input validation for overwrrite/////////////////////////////
        if (exportFile.exists()) {
            System.out.print("File already exists, overwrite? (yes/no): ");
            String response = scan2.nextLine();
            if (!response.equalsIgnoreCase("yes")) {
                System.out.println("Export canceled");
                return;
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(indexFile, "r");
             BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile))) {

            //read header for root id
            byte[] header = new byte[512];
            raf.seek(0);
            raf.readFully(header);
            ByteBuffer headerBuffer = ByteBuffer.wrap(header);

            headerBuffer.position(8); 
            long rootBlockId = headerBuffer.getLong();

            if (rootBlockId == 0) {
                System.out.println("The tree is empty");
                return;
            }

            //traverse and write pairs
            exportNodeToWriter(raf, rootBlockId, writer);

            System.out.println("Key value pairs successfully exported to " + exportFileName);
        } catch (IOException e) {
            System.err.println("Error exporting index: " + e.getMessage());
        }
    }

    private static void exportNodeToWriter(RandomAccessFile raf, long blockId, BufferedWriter writer) throws IOException {
        //calculate offset
        long offset = (blockId - 1) * 512;
        raf.seek(offset);

        //read block
        byte[] block = new byte[512];
        raf.readFully(block);
        ByteBuffer buffer = ByteBuffer.wrap(block);

        //read block fields
        long currentBlockId = buffer.getLong(); // Block ID
        buffer.getLong(); // Parent Block ID (not used here)
        long numKeyValuePairs = buffer.getLong();

        //read keys
        long[] keys = new long[19];
        for (int i = 0; i < 19; i++) {
            keys[i] = buffer.getLong();
        }

        //read values
        long[] values = new long[19];
        for (int i = 0; i < 19; i++) {
            values[i] = buffer.getLong();
        }

        //read child pointers
        long[] childPointers = new long[20];
        for (int i = 0; i < 20; i++) {
            childPointers[i] = buffer.getLong();
        }

        //write pairs to file
        for (int i = 0; i < numKeyValuePairs; i++) {
            writer.write(keys[i] + "," + values[i]);
            writer.newLine();
        }

        //recursive call to export child nodes
        for (int i = 0; i <= numKeyValuePairs; i++) {
            if (childPointers[i] != 0) {
                exportNodeToWriter(raf, childPointers[i], writer);
            }
        }
    }

    //////////////////////////////////LOAD METHODs
    public void load() {
        if(curFile == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }

        System.out.println("Enter file name to load from: ");
        scan = new Scanner(System.in); //clear scanner
        String fileName = scan.nextLine();
        File file = new File(fileName);

        if (!file.exists()) {
            System.err.println("Error: File does not exist.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");

                if (parts.length != 2) {
                    System.err.println("Skipping invalid line " + lineNumber + ": " + line);
                    continue;
                }

                try {
                    long key = Long.parseUnsignedLong(parts[0].trim());
                    long value = Long.parseUnsignedLong(parts[1].trim());

                    //call insertion method (key value parameter version)
                    insertIntoBTree(key, value);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number on line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    //overwritten insertion method
    public void insertIntoBTree(long key, long value) {
        File file = curFile;
        if(file == null) {
            System.out.println("Null file, cannot perform operation");
            return;
        }

        if (!file.exists()) {
            System.err.println("Error: Index file does not exist");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long rootBlockId = readRootBlockId(raf);
            long nextBlockId = readNextBlockId(raf);
            
            if (rootBlockId == 0) {
                //if tree is empty
                createNewRoot(raf, key, value, nextBlockId);
            } else {
                if (!insertIntoNode(raf, rootBlockId, key, value)) {
                    System.out.println("Error: Key already exists in the tree");
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error during insertion: " + e.getMessage());
        }
    }
}