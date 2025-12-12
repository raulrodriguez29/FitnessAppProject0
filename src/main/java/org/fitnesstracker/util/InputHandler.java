package org.fitnesstracker.util;

import java.util.Scanner;

public class InputHandler {
    private static final Scanner scanner = new Scanner(System.in);

    public static Integer getIntInput(String prompt){
        while(true){
            System.out.println(prompt);
            try{
                return Integer.parseInt(scanner.nextLine());
            }catch(NumberFormatException e){
                System.out.println("Invalid Input. Please enter a number.");
            }
        }
    }

    public static String getStringInput(String prompt){
        while(true){
            System.out.println(prompt);
            try{
                return scanner.nextLine();
            }catch(RuntimeException e){
                System.out.println("Invalid Input. Please enter a String.");
            }
        }
    }
}
