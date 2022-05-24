package tolls;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Run the EZ pass simulation.  This program reads a series of simulated
 * EZ pass scan inputs from a data file and logs that information with an
 * EZPassDB object.  After all of the scans have been processed, two reports
 * are printed.  The first lists all the vehicles that are still on the
 * Thruway.  The report lists the vehicles in sorted order based on the
 * exit number where they entered the Thruway.  The second report lists
 * billing information for each vehicle that completed a trip on the
 * Thruway.  This report is sorted in order by tag and then the exit
 * number where the vehicle entered the Thruway.
 *
 * @author RIT CS
 * @author Miguel Reyes
 */
public class ThruwaySimulator {
    /**
     * The EZPass database used in this simulation
     */
    private static TollDB ezCPU = new EZPassDB();

    /**
     * For printing floating point values in dollar/cents format, e.g.:
     * System.out.println(moneyFormat.format(10.5));  // $10.50
     */
    private static DecimalFormat moneyFormat = new DecimalFormat("$0.00");

    /**
     * Print out a report listing the vehicles that are still on the
     * Thruway.  The vehicles on the listing will be printed in
     * order based on the exit where they entered the Thruway.
     */
    private static void printIncompleteList() {
        //openTrips is an Arraylist that contains cars that have not completed their trip
        List<TollRecord> ezPass = ezCPU.openTrips();
        //sort list in ascending order by the Exit number where they entered the Thruway,
        //and alphabetically by tag if they have the same Exit number.
        ezPass.sort(new EntryComparator());
        //Report header
        System.out.println("\nINCOMPLETE TRIPS:");
        System.out.println("================");

        System.out.println(ezPass.size() + " vehicles are still on the road:");

        //Only need to print output if list isn't empty
        if (ezPass.size() > 0) {
            //prints all vehicle objects still on road by their tag.
            for (TollRecord pass : ezPass) {
                System.out.println("\t" + pass.getTag());
            }
            //sorts list in ascending order by entry booth and then tags in descending order
            ezPass.sort(new IncompleteTripComparator());
            //trackers used to only print "Exit {ENTRY}-"{INTERCHANGE}":" whenever it's a new Entry value
            String tracker = ezPass.get(0).getEntry();
            //Need to print the first Entry/Interchange because the initial tracker above will not pass the condition
            //that assigns a new value to tracker2 which results in skipping the first Entry/Interchange
            System.out.println("\nExit " + ezPass.get(0).getEntry() + "-\""
                    + TollSchedule.getInterchange(ezPass.get(0).getEntry()) + "\":");
            for (TollRecord tollRecord : ezPass) {
                String tracker2 = tollRecord.getEntry();
                if (!tracker.equals(tracker2)) {
                    System.out.println("Exit " + tollRecord.getEntry() + "-\""
                            + TollSchedule.getInterchange(tollRecord.getEntry()) + "\":");
                    //updates tracker - allows for comparing old Entry value to new Entry value
                    tracker = tollRecord.getEntry();
                }
                System.out.println("\t" + tollRecord.getTag());
            }
        }
        //Used for correct formatting to match output provided(specifically out-2.txt)
        else {
            System.out.println();
        }

    }

    /**
     * Print out a billing report for the vehicles that completed trips
     * on the Thruway.  The report lists the trips first by vehicle tag
     * and then by the exit where the vehicle entered the Thruway.  The
     * toll that was charged for each trip plus the total toll due is
     * printed for each unique vehicle.
     */
    private static void printBills() {
        List<TollRecord> ezPass = ezCPU.charges();
        //To sort in ascending order by tag and then entry booth in ascending order
        ezPass.sort(new CompleteTripComparator());
        // Report header
        System.out.println("\nBILLING INFORMATION:");
        System.out.println("===================");
        //total due in fares per Tag
        double totalDueEachTag = 0;
        //summation of all fares
        double totalDue = 0;
        //Only need to print output if list isn't empty
        if (ezPass.size() > 0) {
            //trackers used for the same exact reason explained in printIncompleteList()
            // The difference is we're now comparing tags
            String tracker = ezPass.get(0).getTag();
            System.out.println("Tag: " + ezPass.get(0).getTag());
            for (TollRecord tollRecord : ezPass) {
                String tracker2 = tollRecord.getTag();
                if (!tracker.equals(tracker2)) {
                    //we only want to print the fairs per each tag when we're at a new tag value right before the new
                    // tag because the fair value is associated with the old tag value.
                    System.out.println("\tTotal due: " + moneyFormat.format(totalDueEachTag) + "\n");
                    //after printing reset to 0 to account for new tags total fair value
                    totalDueEachTag = 0;
                    System.out.println("Tag: " + tollRecord.getTag());
                    tracker = tollRecord.getTag();
                }
                //prints "From {ENTRY}-"{ENTRY_INTERCHANGE}" to {EXIT}-"{EXIT_INTERCHANGE}", Toll: ${FARE}"
                System.out.println("\tFrom " + tollRecord.getEntry() + "-\""
                        + TollSchedule.getInterchange(tollRecord.getEntry()) + "\" to " + tollRecord.getExit()
                        + "-\"" + TollSchedule.getInterchange(tollRecord.getExit()) + "\", Toll: "
                        + moneyFormat.format(tollRecord.getToll()));
                totalDueEachTag += tollRecord.getToll();
                totalDue += tollRecord.getToll();
            }
            //Accounts for last Tags total fair because once we get to the last tag we never check the condition to
            // print out totalDueEachTag
            System.out.println("\tTotal due: " + moneyFormat.format(totalDueEachTag) + "\n");
        }
        System.out.print("Total Due: " + moneyFormat.format(totalDue));

    }

    /**
     * Main entry point for the simulation.
     *
     * @param args command line arguments (name of data file)
     */
    public static void main(String args[]) {
        // Handle command line arguments
        if (args.length != 1) {
            System.err.println("Usage:  java ThruwaySimulator data-file");
            System.exit(1);
        }

        // Attempt to open the data file and process the input.
        // Data records are of the form TTTTTT EE where TTTTTT is
        // the tag and EE is the exit number.  Any malformed input,
        // or I/O error will cause this program to terminate.
        try {
            Scanner in = new Scanner(new File(args[0]));

            while (in.hasNext()) {
                // Read records from the file, extract the appropriate information
                // and then log the information with the EZPassDB object.
                String tag = in.next();
                String exit = in.next();
                ezCPU.log(tag, exit);
            }
            // Print the report listing the vehicles still on the Thruway
            printIncompleteList();
            // Print the billing information
            printBills();
        } catch (Exception e) {
            System.err.println("ThruwaySimulator:  " + e.getMessage());
            System.exit(1);
        }
    }
}
