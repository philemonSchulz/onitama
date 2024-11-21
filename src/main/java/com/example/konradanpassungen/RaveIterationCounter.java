package com.example.konradanpassungen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RaveIterationCounter {

    public static void main(String[] args) {
        String filePath = "mctsPlayouts.txt"; // Change this to your file's path
        processFile(filePath);
    }

    public static void processFile(String filePath) {
        double sum = 0; // To store the sum of extracted numbers
        int count = 0; // To count the numbers extracted

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // Print the line for debugging

                if (line.contains("-")) {
                    // Print and reset on encountering a '-'
                    if (count > 0) {
                        double average = sum / count;
                        System.out.printf("Average RAVE Iterations: %.5f%n", average);
                    } else {
                        System.out.println("No RAVE Iterations found for this section.");
                    }

                    // Reset variables
                    sum = 0;
                    count = 0;
                }

                // Extract "Avg. RAVE Iterations" value if present
                if (line.contains("Avg. RAVE Iterations:")) {
                    String[] parts = line.split(","); // Split by commas
                    for (String part : parts) {
                        if (part.contains("Avg. RAVE Iterations:")) {
                            try {
                                // Extract the number after "Avg. RAVE Iterations:"
                                String numberStr = part.split("Avg. RAVE Iterations:")[1].trim();
                                double number = Double.parseDouble(numberStr);
                                sum += number;
                                count++;
                            } catch (NumberFormatException e) {
                                System.err.println("Failed to parse number in part: " + part);
                            }
                        }
                    }
                }
            }

            // Final average calculation
            if (count > 0) {
                double average = sum / count;
                System.out.printf("Final Average RAVE Iterations: %.5f%n", average);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
