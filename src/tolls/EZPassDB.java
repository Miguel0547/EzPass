package tolls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The computer end of the EZ pass system.
 * <p>
 * Every time a car passes by a scanner at a Thruway Exit (either for
 * entry or exit) the scanner generates a string that contains the
 * vehicle's tag and the Exit the vehicle is at.  This information is
 * passed to the log() method that records the event.
 * </p>
 * <p>
 * The DB maintains two collections.  The first collection holds the
 * TollRecords for the trips that have not yet been completed (i.e.
 * the vehicle is still on the Thruway).  The second collection holds
 * the TollRecords for all completed trips.  Note that a vehicle can
 * only appear once in the first collection and may appear several times
 * in the second collection.
 * </p>
 *
 * @author RIT CS
 * @author Miguel Reyes
 */

public class EZPassDB implements TollDB {
    /**
     * Collection that holds the TollRecords for the trips that have not yet been completed (i.e.the vehicle is still
     * on the Thruway).
     */
    private HashMap<String, TollRecord> open = new HashMap<>();

    /**
     * Collection that holds the TollRecords for all completed trips.
     */
    private List<TollRecord> completed = new ArrayList<>();

    @Override
    public void log(String tag, String exit) {
        TollRecord ezPass = new TollRecord(tag);
        //if open contains the tag already than the vehicle is either exiting the thruway or re-entering
        //this condition below handles both scenarios
        if (open.containsKey(tag)) {
            if (open.get(tag).getExit() != null) {
                ezPass.setEntry(exit);
                System.out.println("LOG: Entering: " + ezPass);
            } else {
                ezPass.setEntry(open.get(tag).getEntry());
                ezPass.setExit(exit);
                System.out.println("LOG: Exiting: " + ezPass);
            }
        }
        //for new keys:value pairs
        else {
            ezPass.setEntry(exit);
            System.out.println("LOG: Entering: " + ezPass);
        }
        //ezpass objects are being added to open collection and updated as long as log is called by main method
        open.put(tag, ezPass);
        //adding opens values(completed TollRecord objects with tag and exit values) to the complete collection
        //this collection will have duplicates
        completed.add(open.get(tag));

    }

    @Override
    public List<TollRecord> openTrips() {
        List<TollRecord> notCompletedTrips = new ArrayList<>();
        for (TollRecord ezPass : open.values()) {
            //we want to add all the TollRecord objects whose exit field is null because that means there still
            //on the thruway and have not completed their trip
            if (ezPass.getExit() == null) {
                notCompletedTrips.add(ezPass);
            }
        }
        return notCompletedTrips;
    }

    @Override
    public List<TollRecord> charges() {
        List<TollRecord> completedTrips = new ArrayList<>();
        //Did not use open collection because open collection has final entry and exit fields associated with tags
        //Here we want all TollRecord's whose exit field is not null because that means they have completed their trip
        for (TollRecord ezPass : completed) {
            if (ezPass.getExit() != null) {
                completedTrips.add(ezPass);
            }
        }
        return completedTrips;
    }
}
