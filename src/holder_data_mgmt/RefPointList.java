

package holder_data_mgmt;

/**
 *
 * @author bepstein
 * The list of reference points. This class mimics the original representation fo the holder coordinates data file.
 */
public class RefPointList {
    /* constructors */
    public RefPointList(RefPoint first_entry)
    {
        start_entry = new RefPointListEntry( first_entry );
    }
    
    /* public */
    
    public RefPoint getRefPoint(int index)
    {
        int i;
        RefPointListEntry curr_entry;

        curr_entry = start_entry;

        for (i = 0; i <= index; i++){
            curr_entry = curr_entry.getNextListEntry();
        }

        return( curr_entry.getRefPoint() );
            
    }
    
    /* private */
    private RefPointListEntry start_entry;
    
    private class RefPointListEntry{
        /* constructors */
        public RefPointListEntry(RefPoint rf){
            curr_rf = rf;
            prev_entry = null;
            next_entry = null;
        }
    
        public RefPointListEntry(RefPoint rf,
            RefPointListEntry prev,
            RefPointListEntry next)
        {
            curr_rf = rf;
            prev_entry = prev;
            next_entry = next;
        }
    
        /* public */
        public RefPoint getRefPoint()
        {
            return( curr_rf );
        }
    
        public RefPointListEntry getPrevListEntry()
        {
            return( prev_entry );
        }
    
        public RefPointListEntry getNextListEntry()
        {
            return( next_entry );
        }
    
        /* private */
        private RefPoint curr_rf;
        private RefPointListEntry prev_entry;
        private RefPointListEntry next_entry;
    }
}

