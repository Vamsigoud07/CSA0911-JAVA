package src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Generic Leaderboard managing ranking of items of type T.
 *
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 *
 * @param <T> the type of entity ranked on the leaderboard
 */
public class Leaderboard<T> {

    private final List<T> entries;
    private final Comparator<? super T> rankingComparator;

    /**
     * Constructs a Leaderboard with a custom Comparator.
     * @param rankingComparator the comparator defining ranking order
     */
    public Leaderboard(Comparator<? super T> rankingComparator) {
        this.entries = new ArrayList<>();
        this.rankingComparator = rankingComparator;
    }

    /**
     * Constructs a Leaderboard using natural ordering (T must implement Comparable).
     */
    @SuppressWarnings("unchecked")
    public Leaderboard() {
        this.entries = new ArrayList<>();
        this.rankingComparator = (a, b) -> ((Comparable<? super T>) a).compareTo(b);
    }

    /**
     * Adds an item to the leaderboard.
     * @param item the item to add
     */
    public void add(T item) {
        if (item != null) {
            entries.add(item);
        }
    }

    /**
     * Adds multiple items to the leaderboard using wildcard <? extends T>.
     * @param items collection of items
     */
    public void addAll(Collection<? extends T> items) {
        if (items != null) {
            entries.addAll(items);
        }
    }

    /**
     * Returns a sorted copy of the entries based on the comparator,
     * guaranteeing the original list remains completely unmodified.
     *
     * @return a newly allocated List<T> in ranked order
     */
    public List<T> ranking() {
        List<T> rankedCopy = new ArrayList<>(entries);
        rankedCopy.sort(rankingComparator);
        return rankedCopy;
    }

    /**
     * Returns the unmodifiable view of original entries to verify non-mutation.
     */
    public List<T> getOriginalEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int size() {
        return entries.size();
    }
}
