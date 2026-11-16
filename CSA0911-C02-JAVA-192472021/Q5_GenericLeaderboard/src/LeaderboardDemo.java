package src;

import java.util.Comparator;

/**
 * Q5: Complete the Code - Generic Sports Leaderboard Demo
 * Mandatory expected output:
 * [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class LeaderboardDemo {

    public static void main(String[] args) {
        // Comparator chaining:
        // 1. Points (descending)
        // 2. Goal Difference (descending)
        // 3. Team Name (alphabetical ascending)
        Comparator<Team> teamComparator = Comparator
            .comparingInt(Team::getPoints).reversed()
            .thenComparing(Comparator.comparingInt(Team::getGoalDifference).reversed())
            .thenComparing(Team::getName);

        Leaderboard<Team> leaderboard = new Leaderboard<>(teamComparator);

        leaderboard.add(new Team("Panthers", 9, 4));
        leaderboard.add(new Team("Cobras", 6, 1));
        leaderboard.add(new Team("Eagles", 9, 6));
        leaderboard.add(new Team("Bisons", 9, 4));

        // Mandatory expected output: [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
        System.out.println(leaderboard.ranking());
    }
}
