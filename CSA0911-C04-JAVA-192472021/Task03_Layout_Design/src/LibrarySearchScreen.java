package task03;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Campus Library Search Screen.
 * Demonstrates:
 * - Nested container layout hierarchy:
 *   JFrame (BorderLayout)
 *     ├── NORTH: NorthContainer (BorderLayout)
 *     │     ├── NORTH: TitlePanel (FlowLayout)
 *     │     └── SOUTH: SearchPanel (FlowLayout)
 *     ├── CENTER: ResultsPanel (BorderLayout) -> JScrollPane(JTable) [EXPANDS]
 *     └── SOUTH: SouthContainer (BorderLayout)
 *           ├── NORTH: ButtonPanel (FlowLayout)
 *           └── SOUTH: StatusPanel (FlowLayout)
 * - Resizing robustness: Center results area expands to fill all extra space,
 *   while headers, search controls, and bottom buttons maintain natural dimensions.
 */
public class LibrarySearchScreen extends JFrame {
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton prevButton;
    private JButton nextButton;
    private JButton reserveButton;
    private JLabel statusLabel;

    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> filteredBooks = new ArrayList<>();

    // Pagination support
    private int currentPage = 0;
    private static final int PAGE_SIZE = 6;

    public LibrarySearchScreen() {
        initSampleData();
        initUI();
        performSearch();
    }

    private void initSampleData() {
        allBooks.add(new Book("978-0134685991", "Effective Java", "Joshua Bloch", "Computer Science", 2018, true));
        allBooks.add(new Book("978-0262033848", "Introduction to Algorithms", "CLRS", "Computer Science", 2009, true));
        allBooks.add(new Book("978-0132350884", "Clean Code", "Robert C. Martin", "Computer Science", 2008, false));
        allBooks.add(new Book("978-0321356680", "Design Patterns: Elements of Reusable OO Software", "GoF", "Computer Science", 1994, true));
        allBooks.add(new Book("978-1491950357", "Designing Data-Intensive Applications", "Martin Kleppmann", "Computer Science", 2017, true));
        allBooks.add(new Book("978-0073380445", "Microelectronic Circuits", "Sedra & Smith", "Electrical", 2014, true));
        allBooks.add(new Book("978-0131850842", "Signals and Systems", "Alan V. Oppenheim", "Electrical", 1996, false));
        allBooks.add(new Book("978-0134092577", "Electric Circuits", "James W. Nilsson", "Electrical", 2018, true));
        allBooks.add(new Book("978-0321878960", "Calculus: Early Transcendentals", "James Stewart", "Mathematics", 2015, true));
        allBooks.add(new Book("978-0073383095", "Discrete Mathematics and Its Applications", "Kenneth Rosen", "Mathematics", 2011, true));
        allBooks.add(new Book("978-0134444062", "University Physics with Modern Physics", "Young & Freedman", "Physics", 2019, true));
        allBooks.add(new Book("978-0471320005", "Fundamentals of Physics", "Halliday & Resnick", "Physics", 2013, false));
        allBooks.add(new Book("978-0140449136", "The Odyssey", "Homer", "Literature", 2003, true));
        allBooks.add(new Book("978-0451524935", "1984", "George Orwell", "Literature", 1961, true));
        allBooks.add(new Book("978-0743273565", "The Great Gatsby", "F. Scott Fitzgerald", "Literature", 1925, true));
    }

    private void initUI() {
        setTitle("SIMATS Campus Library - Catalog Search System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(720, 480);
        setMinimumSize(new Dimension(460, 320));
        setLocationRelativeTo(null);

        // Main Frame Layout: BorderLayout
        setLayout(new BorderLayout(5, 5));

        // 1. NORTH CONTAINER (Holds Title and Search Controls)
        JPanel northContainer = new JPanel(new BorderLayout());

        // 1a. Title Panel (FlowLayout)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(25, 60, 110));
        JLabel titleLabel = new JLabel("SIMATS CAMPUS LIBRARY SEARCH PORTAL");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        northContainer.add(titlePanel, BorderLayout.NORTH);

        // 1b. Search Panel (FlowLayout)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        searchPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        searchPanel.add(new JLabel("Category:"));
        String[] categories = {"All Categories", "Computer Science", "Electrical", "Mathematics", "Physics", "Literature"};
        categoryCombo = new JComboBox<>(categories);
        searchPanel.add(categoryCombo);

        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(30, 120, 60));
        searchButton.setForeground(Color.WHITE);
        searchPanel.add(searchButton);

        northContainer.add(searchPanel, BorderLayout.SOUTH);
        add(northContainer, BorderLayout.NORTH);

        // 2. CENTER CONTAINER (Results Area that expands with window resize)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String[] columns = {"ISBN", "Title", "Author", "Category", "Year", "Availability"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(24);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        resultsTable.getTableHeader().setBackground(new Color(230, 235, 245));

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. SOUTH CONTAINER (Navigation Buttons and Status Bar)
        JPanel southContainer = new JPanel(new BorderLayout());

        // 3a. Button Panel (FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        prevButton = new JButton("<< Previous");
        nextButton = new JButton("Next >>");
        reserveButton = new JButton("Reserve Selected Book");
        reserveButton.setBackground(new Color(180, 80, 30));
        reserveButton.setForeground(Color.WHITE);

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(reserveButton);
        southContainer.add(buttonPanel, BorderLayout.NORTH);

        // 3b. Status Panel (FlowLayout)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        statusPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("Ready. Enter search terms above.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusPanel.add(statusLabel);
        southContainer.add(statusPanel, BorderLayout.SOUTH);

        add(southContainer, BorderLayout.SOUTH);

        // Event Listeners
        searchButton.addActionListener(e -> {
            currentPage = 0;
            performSearch();
        });

        searchField.addActionListener(e -> {
            currentPage = 0;
            performSearch();
        });

        categoryCombo.addActionListener(e -> {
            currentPage = 0;
            performSearch();
        });

        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                renderTablePage();
            }
        });

        nextButton.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) filteredBooks.size() / PAGE_SIZE) - 1;
            if (currentPage < maxPage) {
                currentPage++;
                renderTablePage();
            }
        });

        reserveButton.addActionListener(e -> handleReserve());
    }

    public void performSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedCategory = (String) categoryCombo.getSelectedItem();

        filteredBooks.clear();
        for (Book b : allBooks) {
            boolean matchesCategory = "All Categories".equals(selectedCategory) ||
                                      b.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesKeyword = keyword.isEmpty() ||
                                     b.getTitle().toLowerCase().contains(keyword) ||
                                     b.getAuthor().toLowerCase().contains(keyword) ||
                                     b.getIsbn().toLowerCase().contains(keyword);

            if (matchesCategory && matchesKeyword) {
                filteredBooks.add(b);
            }
        }

        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int total = filteredBooks.size();
        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / PAGE_SIZE);

        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, total);

        for (int i = start; i < end; i++) {
            Book b = filteredBooks.get(i);
            tableModel.addRow(new Object[]{
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getYear(),
                    b.isAvailable() ? "AVAILABLE" : "RESERVED"
            });
        }

        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);

        statusLabel.setText(String.format("Showing %d - %d of %d matches | Page %d of %d",
                total == 0 ? 0 : start + 1, end, total, currentPage + 1, totalPages));
    }

    public void handleReserve() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book from the table to reserve.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int globalIndex = currentPage * PAGE_SIZE + selectedRow;
        if (globalIndex < filteredBooks.size()) {
            Book selectedBook = filteredBooks.get(globalIndex);
            if (!selectedBook.isAvailable()) {
                JOptionPane.showMessageDialog(this,
                        "'" + selectedBook.getTitle() + "' is currently checked out / reserved.",
                        "Reservation Unavailable", JOptionPane.WARNING_MESSAGE);
                statusLabel.setText("Reservation Failed: Book already checked out.");
            } else {
                selectedBook.setAvailable(false);
                renderTablePage();
                statusLabel.setText("Successfully reserved: " + selectedBook.getTitle());
                JOptionPane.showMessageDialog(this,
                        "Reservation Confirmed!\n" +
                        "Book: " + selectedBook.getTitle() + "\n" +
                        "ISBN: " + selectedBook.getIsbn() + "\n" +
                        "Please collect from Circulation Desk within 24 hours.",
                        "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Accessors for automated testing
    public JTextField getSearchField() { return searchField; }
    public JComboBox<String> getCategoryCombo() { return categoryCombo; }
    public JButton getSearchButton() { return searchButton; }
    public JTable getResultsTable() { return resultsTable; }
    public JButton getPrevButton() { return prevButton; }
    public JButton getNextButton() { return nextButton; }
    public JButton getReserveButton() { return reserveButton; }
    public JLabel getStatusLabel() { return statusLabel; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
