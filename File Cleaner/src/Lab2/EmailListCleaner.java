package Lab2;

import java.io.*;
import java.util.*;

public class EmailListCleaner {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose input file format:");
        System.out.println("1. Text");
        System.out.println("2. CSV");
        int choice = scanner.nextInt();

        String inputFilePath;
        if (choice == 1) {
            inputFilePath = "prospects.txt"; // Set the path for text file
        } else if (choice == 2) {
            inputFilePath = "prospects.csv"; // Set the path for CSV file
        } else {
            System.out.println("Invalid choice.");
            scanner.close();
            return;
        }

        String outputFilePath = "prospects_clean.csv";
        System.out.println(" ");
        System.out.println("File Cleaner\n");
        System.out.println("Source File : "+inputFilePath);
        System.out.println("Cleaned File : "+outputFilePath);
        

        try {
            List<Prospect> prospects = readProspectsFromFile(inputFilePath, choice);
            validateProspects(prospects);
            cleanProspects(prospects);
            writeProspectsToFile(prospects, outputFilePath);
  
            System.out.println("Congratulations! Your file has been cleaned" );
        } catch (FileNotFoundException e) {
            System.err.println("Error: The input file was not found. Please make sure the file exists.");
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while processing the file.");
        } catch (NoSuchElementException | IllegalStateException e) {
            System.err.println("Error: Invalid input format. Please provide valid input.");
        } finally {
            scanner.close();
        }
    }

    private static List<Prospect> readProspectsFromFile(String filePath, int fileType) throws IOException {
        List<Prospect> prospects = new ArrayList<>();
        File inputFile = new File(filePath);

        if (!inputFile.exists()) {
            throw new FileNotFoundException("Error: The input file does not exist.");
        }

        if (!inputFile.canRead()) {
            throw new IOException("Error: Cannot read the input file. Please check file permissions.");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            if (fileType == 2) {
                reader.readLine(); // Skip header line for CSV
            }
            while ((line = reader.readLine()) != null) {
                String[] data;
                if (fileType == 2) {
                    data = line.split(",");  // Split CSV lines using comma
                } else {
                    data = line.split("\\s*,\\s*");
                }
                if (data.length != 3) {
                    System.err.println("Error: Invalid data format found. Skipping the line: " + line);
                    continue;
                }
                String firstName = data[0].trim();
                String lastName = data[1].trim();
                String email = data[2].trim();
                prospects.add(new Prospect(firstName, lastName, email));
            }
        }
        return prospects;
    }

    private static void validateProspects(List<Prospect> prospects) {
        Iterator<Prospect> iterator = prospects.iterator();
        while (iterator.hasNext()) {
            Prospect prospect = iterator.next();
            String email = prospect.getEmail();
            if (!Prospect.isValidEmailFormat(email)) {
                System.err.println("Invalid email format found: " + email + ". Removing the entry.");
                iterator.remove();
            }
        }

        Set<String> uniqueEmails = new HashSet<>();
        iterator = prospects.iterator();
        while (iterator.hasNext()) {
            Prospect prospect = iterator.next();
            String email = prospect.getEmail();
            if (!uniqueEmails.add(email)) {
                System.err.println("Duplicate email found: " + email + ". Removing the duplicate entry.");
                iterator.remove();
            }
        }
    }

    private static void cleanProspects(List<Prospect> prospects) {
        for (Prospect prospect : prospects) {
            prospect.cleanData();
        }
    }

    private static void writeProspectsToFile(List<Prospect> prospects, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("First Name,Last Name,Email\n");
            for (Prospect prospect : prospects) {
                writer.write(prospect.getFirstName() + "," + prospect.getLastName() + "," + prospect.getEmail() + "\n");
            }
        }
    }
}
