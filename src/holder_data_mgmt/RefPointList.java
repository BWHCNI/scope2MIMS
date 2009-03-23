package holder_data_mgmt;

/**
 *
 * @author bepstein
 * The list of reference points. This class mimics the original representation fo the holder coordinates data file.
 */
public class RefPointList {
        /* private */
    private RefPointListEntry start_entry;
    private int num_of_ref_points = 0; /* total ref points in list */

    private final int SEQ_MAX_ENR = 2000; /* maximum number of points */

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

        public void setPrevListEntry(RefPointListEntry entry)
        {
            prev_entry = entry;
        }

        public RefPointListEntry getNextListEntry()
        {
            return( next_entry );
        }

        public void setNextListEntry(RefPointListEntry entry)
        {
            next_entry = entry;
        }

        /* private */
        private RefPoint curr_rf;
        private RefPointListEntry prev_entry;
        private RefPointListEntry next_entry;

    }

    /* constructors */
    public RefPointList(RefPoint first_entry)
    {
        start_entry = new RefPointListEntry( first_entry );
        num_of_ref_points = 1;
    }

    public RefPointList()
    {
        start_entry = null;
        num_of_ref_points = 0;
    }
    
    /* public */
    
    public RefPoint getRefPoint(int index)
    {
        int i;
        RefPointListEntry curr_entry;

        /* Returning null if index out of range */
        if ( (index < 0 ) && (index >= num_of_ref_points) )
            return( null );

        curr_entry = start_entry;

        for (i = 0; i < index; i++){
            curr_entry = curr_entry.getNextListEntry();
        }

        return( curr_entry.getRefPoint() );
            
    }

    public int getNumRefPoints()
    {
        return( num_of_ref_points );
    }

    public void addRefPoint(RefPoint rf)
    {
        RefPointListEntry curr_entry;
        RefPointListEntry new_entry;

        if (start_entry != null)
        {
            curr_entry = start_entry;
            new_entry = new RefPointListEntry( rf );

            while (curr_entry.getNextListEntry() != null)
            {
                curr_entry = curr_entry.getNextListEntry();
            }

            new_entry.setPrevListEntry(curr_entry);
            curr_entry.setNextListEntry(new_entry);
        } else {
            start_entry = new RefPointListEntry( rf );;
        }

        num_of_ref_points++;
    }

    public void removeRefPoint(int index)
    {
        RefPointListEntry point_entry, prev_point, next_point;
        int i;

        /* Checking to see the point is in range */
        if ( (index < 0 ) && (index >= num_of_ref_points) )
            return;

        /* Handling the single entry removal*/
        if ( num_of_ref_points == 1 )
        {
            start_entry = null;
            num_of_ref_points = 0;
            return;
        }

        /* Handling removal of the first entry */
        if (index == 0)
        {
            start_entry = start_entry.getNextListEntry();
            start_entry.setPrevListEntry( null );
            num_of_ref_points--;
            return;
        }

        /* Handling removal of the last entry */
        if ( index == num_of_ref_points - 1 )
        {
            point_entry = start_entry;

            for (i = 0; i < num_of_ref_points - 1; i++)
                point_entry = point_entry.getNextListEntry();

            point_entry.setNextListEntry(null);
            num_of_ref_points--;
            return;
        }

        /* Handling removal of one of the points in the middle */
        point_entry = start_entry;

        for (i = 0; i < index; i++)
            point_entry = point_entry.getNextListEntry();

        prev_point = point_entry.getPrevListEntry();
        next_point = point_entry.getNextListEntry();
        prev_point.setNextListEntry( next_point );
        next_point.setPrevListEntry( prev_point );
        num_of_ref_points--;
    }
    
    public void removeAllRefPoints()
    {
        num_of_ref_points = 0;
        start_entry = null;
    }

}

