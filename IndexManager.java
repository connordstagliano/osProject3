import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
                ////////////TODO CREATE FILE HEADER//////////////////////////////////////////////////////
            }

            System.out.println("File " + file.getAbsolutePath() + " created successfully");
            curFile = file; //setting to currently open file
        } catch (IOException e) {
            System.err.println("Error occured with creation " + e.getMessage());
        }
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
}
