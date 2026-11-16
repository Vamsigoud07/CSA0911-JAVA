package task03;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Task 03 - Alternative Concept:
 * Demonstrating the risk of No Resource Closing.
 *
 * Leaving streams open retains underlying OS file descriptors.
 * In long-running applications (such as servers or batch processors),
 * this leads to "java.io.IOException: Too many open files" and OS resource starvation.
 */
public class NoResourceCloseDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   ALTERNATIVE DEMO: UNCLOSED RESOURCE LEAK RISK  ");
        System.out.println("==================================================");

        String samplePath = "CSA09-192472021-Assignment3/Task03_Resource_Safety/input/store1_chennai.csv";
        File file = new File(samplePath);

        System.out.println("Simulating sequential unclosed streams without close()...");
        int openedCount = 0;
        try {
            for (int i = 1; i <= 50; i++) {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                // Intentionally NOT calling br.close() or fr.close()
                openedCount++;
            }
            System.out.println("Successfully opened " + openedCount + " file handles without explicit close.");
            System.out.println("Notice: Operating system file table slots remain occupied until JVM exit");
            System.out.println("or nondeterministic Garbage Collector finalization.");
        } catch (IOException e) {
            System.err.println("File descriptor exhaustion encountered: " + e.getMessage());
        }
    }
}
