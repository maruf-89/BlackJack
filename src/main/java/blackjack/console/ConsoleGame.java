package blackjack.console;

import blackjack.game.BlackjackGame;

import java.util.Scanner;


public class ConsoleGame {


    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);


        BlackjackGame game = new BlackjackGame();


        System.out.println("Player:");
        System.out.println(
                game.getState().getPlayer()
        );


        System.out.println("Dealer:");
        System.out.println(
                game.getState().getDealer()
        );


        while(
                game.getState()
                        .getStatus()
                        .name()
                        .equals("ACTIVE")
        ) {


            System.out.println("Hit or Stand?");


            String input = scanner.nextLine();


            if(input.equalsIgnoreCase("hit")) {

                game.hit();


                System.out.println("Player:");

                System.out.println(
                        game.getState().getPlayer()
                );

            }


            if(input.equalsIgnoreCase("stand")) {

                game.stand();

            }

        }


        System.out.println("Dealer:");

        System.out.println(
                game.getState().getDealer()
        );


        System.out.println("Result:");

        System.out.println(
                game.getState().getResult()
        );


        scanner.close();

    }

}