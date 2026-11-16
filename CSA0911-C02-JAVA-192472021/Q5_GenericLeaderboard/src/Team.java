package src;

import java.util.Objects;

/**
 * Team model representing a sports team on the leaderboard.
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Team implements Comparable<Team> {
    private final String name;
    private final int points;
    private final int goalDifference;

    public Team(String name, int points, int goalDifference) {
        this.name = name;
        this.points = points;
        this.goalDifference = goalDifference;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getGoalDifference() {
        return goalDifference;
    }

    @Override
    public int compareTo(Team other) {
        // Natural ascending ordering: Points (asc), GoalDiff (asc), Name (asc)
        int p = Integer.compare(this.points, other.points);
        if (p != 0) return p;
        int gd = Integer.compare(this.goalDifference, other.goalDifference);
        if (gd != 0) return gd;
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return points == team.points &&
               goalDifference == team.goalDifference &&
               Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, points, goalDifference);
    }

    @Override
    public String toString() {
        return name + "(" + points + "," + goalDifference + ")";
    }
}
