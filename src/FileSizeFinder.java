import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.io.File;
import java.lang.Math;
import java.math.RoundingMode;
import java.math.BigDecimal;

public class FileSizeFinder {
    static Scanner input = new Scanner(System.in);

    public static long getFolderSize(File folder) {
        long length = 0;
       
        // ListFiles() is used to list the
        // contents of the given folder
        File[] files = folder.listFiles();
 
        int count = files.length;
 
        // Loop for traversing the directory
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }
    
    // Math.round() cant round to nearest hundreds... ty StackOverflow
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Sort
    public static Map<String, Double> sortByValue(Map<String, Double> hm) {
        return hm.entrySet().stream()
                        .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public static void main(String[] args) {
        String[] directories;
        File file, currentFolder;
        double sizeRounded;

        System.out.print("Enter path: ");
        String path = input.nextLine();
        System.out.print("Enter measurement type. (GB, MB, KB): ");
        String measurement = input.nextLine();

        while (!(measurement.equalsIgnoreCase("GB") || measurement.equalsIgnoreCase("MB") || measurement.equalsIgnoreCase("KB"))) {
            System.out.println("That is not a supported measurment type.");
            System.out.print("Please input a supported type. (GB, MB, KB): ");
            measurement = input.nextLine();
        }
        input.close();
 
        
        file = new File(path);
        directories = file.list();
        
        // Since a GB is 1024x a MB, and a MB is 1024x a KB,
        // this determines how many times to multiply the original size that the getFolderSize() method grabs,
        // which is in KB
        // int powerNum = switch (measurement.toUpperCase()) {
        //     case "GB" -> 3;
        //     case "MB" -> 2;
        //     case "KB" -> 1;
        //     default -> 0;
        // };
        int powerNum;
        if (measurement.toUpperCase().equals("GB")) {
            powerNum = 3;
        } else if (measurement.toUpperCase().equals("MB")) {
            powerNum = 2;
        } else if (measurement.toUpperCase().equals("KB")){
            powerNum = 1;
        } else {
            powerNum = 0;
        }
        
        // Puts the folder names and their size into a map
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < directories.length; i++) {  
            currentFolder = new File(path + "/" + directories[i]);
            sizeRounded = round(getFolderSize(currentFolder) / (Math.pow(1024, powerNum)), 2);
            map.put(directories[i], sizeRounded);
        }

        Map<String, Double> mapSorted = sortByValue(map);
        
        for (Map.Entry<String, Double> entry : mapSorted.entrySet()) {
            String gameName = entry.getKey();
            Double gameSize = entry.getValue();

            System.out.printf("%s : %.2f %s %n", gameName, gameSize, measurement.toUpperCase());
        }
    }
}
