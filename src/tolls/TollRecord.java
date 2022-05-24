package tolls;

/**
 * A class that can be used to create objects that record trips made
 * by vehicles on the thruway.  A toll record records the tag of the
 * vehicle and the exits at which the vehicle entered and left the
 * thruway.  tolls.TollRecord objects can compute the toll that must
 * be paid by the driver of the vehicle.  The toll is $1.25 for every
 * exit the driver passes on the Thruway (not counting the exit at
 * which they entered).
 *
 * @author RIT CS
 * @author Miguel Reyes
 */
public class TollRecord implements Comparable<TollRecord> {
    /**
     * Unique identification for the vehicle
     */
    private String tag;

    /**
     * Entry point
     */
    private String entry;

    /**
     * Exit point
     */
    private String exit;

    /**
     * Create a new tolls.TollRecord given the tag of the vehicle.
     *
     * @param tag a unique identifier for the vehicle that this toll
     *            record represents.
     */
    public TollRecord(String tag) {
        this.tag = tag;
        this.entry = this.exit = null;
    }

    /**
     * Return the vehicle tag associated with this tolls.TollRecord.
     *
     * @return the vehcile tag associated with this tolls.TollRecord.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * Return the exit at which this vehicle entered the thruway.
     * If the entry point has not been set, null will be be returned.
     *
     * @return the exit number where this vehicle entered the thruway,
     * or null if the entry point has not been recorded.
     */
    public String getEntry() {
        return this.entry;
    }

    /**
     * Record the exit number at which this vehicle entered the
     * thruway.  If the exit number is invalid a tolls.TollRecordException
     * will be thrown that contains the message "Invalid entry point".
     *
     * @param entry the exit at which the vehicle entered the thruway.
     * @throws TollRecordException if the exit number is invalid.
     */
    public void setEntry(String entry) throws TollRecordException {
        if (!TollSchedule.isValid(entry)) {
            throw new TollRecordException("Invalid entry point: " + entry);
        }
        this.entry = entry;
    }

    /**
     * Return the exit at which this vehicle left the thruway.
     * If the exit point has not been set, a -1 will be be returned.
     *
     * @return the exit number where this vehicle left the thruway,
     * or null if the exit point has not been recorded.
     */
    public String getExit() {
        return this.exit;
    }

    /**
     * Record the exit number at which this vehicle left the
     * thruway.  If the exit number is invalid a tolls.TollRecordException
     * will be thrown that contains the message "Invalid exit point".
     *
     * @param exit the exit at which the vehicle left the thruway.
     * @throws TollRecordException if the exit number is invalid or the
     *                             exit number equals the entry number.
     */
    public void setExit(String exit) throws TollRecordException {
        if (!TollSchedule.isValid(exit)) {
            throw new TollRecordException("Invalid exit point: " + exit);
        } else if (this.entry.equals(exit)) {
            throw new TollRecordException("Enter and exit booths can't be the same!");
        }
        this.exit = exit;
    }

    /**
     * Compute the toll paid by this vehicle.  If the toll cannot
     * be computed because either one of the ends points has not been
     * recorded a tolls.TollRecordException will be thrown that contains the
     * message "Incomplete toll record".
     *
     * @return the toll to be paid by this vehicle.
     * @throws TollRecordException if the toll cannot be computed.
     */
    public double getToll() throws TollRecordException {
        if (this.exit == null || this.entry == null) {
            throw new TollRecordException("Incomplete toll record");
        }
        return TollSchedule.getFare(this.entry, this.exit);
    }

    /**
     * Two tolls.TollRecord objects are equal if they have the same tag,
     * entry and exit.
     *
     * @param o the object to compare to
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof TollRecord) {
            //noinspection PatternVariableCanBeUsed
            TollRecord oTollRecord = (TollRecord) o;
            //equals for open trip vehicles
            if (this.exit == null) {
                result = this.tag.equals(oTollRecord.tag) &&
                        this.entry.equals(oTollRecord.entry);
            }
            //equals for completed trip vehicles
            else {
                result = this.tag.equals(oTollRecord.tag) &&
                        this.entry.equals(oTollRecord.entry) &&
                        this.exit.equals(oTollRecord.exit);
            }
        }
        return result;
    }

    /**
     * Returns a string representation of this object.  The format
     * of the string returned by this method is given below:
     * <blockquote>
     * <pre>Vehicle ID: XXXXXX, Entry=XX, Exit =XX</pre>
     * </blockquote>
     *
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        if (this.exit != null) {
            return "Vehicle ID: " + this.tag + ", Entry=" + this.entry + ", Exit=" + this.exit;
        } else {
            return "Vehicle ID: " + this.tag + ", Entry=" + this.entry + ", Exit=" + "UNKNOWN";
        }
    }

    /**
     * Returns a hash code value for the object.
     * (The main reason this is here is that Java expects
     * hashCode() to be redefined if equals() is
     * redefined. One must make sure that
     * a.equals(b) ->  a.hashCode() == b.hashCode().)
     *
     * @return a hash code for this object.
     */
    public int hashCode() {
        if (this.exit == null)
            return this.tag.hashCode() + this.entry.hashCode();
        else {
            return this.tag.hashCode() + this.entry.hashCode() + this.exit.hashCode();
        }

    }

    /**
     * The natural order comparison for TollRecords is descending by
     * tag name.
     *
     * @param o the other tolls.TollRecord
     * @return <0 if tag is greater than other's tag, 0 if equal,
     * >0; otherwise
     */
    @Override
    public int compareTo(TollRecord o) {
        return this.tag.compareTo(o.getTag()); //String has compareTo method that compares String lexicographically
    }


}



