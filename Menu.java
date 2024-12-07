import java.util.Scanner;

public class Menu {

    //QUIT METHOD
    public void quit(){
        System.out.println("Exiting program");
        System.exit(0);
    }

    //DRIVER MENU METHOD
    public static void main(String[] args) {
        Menu menu = new Menu();
        IndexManager idxMan = new IndexManager();
        Scanner scan = new Scanner(System.in);
        String menuInput = "";

        while (menuInput != "QUIT"){
            System.out.println("\nCREATE\nOPEN\nINSERT\nSEARCH\nLOAD\nPRINT\nEXTRACT\nQUIT\n\nEnter choice: ");
            menuInput = scan.nextLine().toUpperCase();

            switch (menuInput) {
                case "CREATE":                       
                    idxMan.createOrOverwriteIndexFile();
                    break;

                case "OPEN":                           
                    idxMan.openIndexFile();
                    break;

                case "INSERT":                       
                    idxMan.insertIntoBTree();
                    break;

                case "SEARCH":                          
                    idxMan.searchCall();
                    break;

                case "LOAD":
                    System.out.println("loading");
                    break;

                case "PRINT":
                    idxMan.printContents();
                    break;

                case "EXTRACT":
                    System.out.println("extracting");
                    break;

                case "QUIT":
                    scan.close();
                    menu.quit();
                    break;

                default:
                    System.out.println("Invalid choice, please select one of the menu options");
            }
        }
    }
}
