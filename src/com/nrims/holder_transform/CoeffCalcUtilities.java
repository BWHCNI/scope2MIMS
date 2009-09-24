/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nrims.holder_transform;

import com.nrims.holder_data_mgmt.*;

/**
 * This is a utility class for calculation logic
 * used in calculating coefficients for coordinate translation.
 * @author bepstein
 */

public class CoeffCalcUtilities {

    /* private classes and variables */
    private class list2 {
        private list2  first, last, next, prev;
        private Object item ;
        private int num_objects;

        /* Constructors */
        public list2()
        {
            num_objects = 0;
            first = null;
            last = null;
            prev = null;
            next = null;
        }

        public list2(Object obj)
        {
            num_objects = 1;
            item = obj;
            first = this;
            last = this;
            prev = null;
            next = null;
        }

        /* public method */
        public void setPrev(list2 previous)
        {
            prev = previous;
        }

        public void setPrevToNull()
        {
            prev = null;
        }

        public list2 getPrev()
        {
            return( prev );
        }

        public void setNext(list2 next_element)
        {
            next = next_element;
        }

        public void setNextToNull()
        {
            next = null;
        }

        public list2 getNext()
        {
            return( next );
        }

        public void setFirst(list2 new_first)
        {
            first = new_first;
        }

        public list2 getFirst()
        {
            return( first );
        }

        public void setLast(list2 new_last)
        {
            last = new_last;
        }

        public list2 getLast()
        {
            return( last );
        }

        public void addElement(Object obj)
        {
            list2 temp_list = new list2( obj );
            temp_list.setFirst( first );
            temp_list.setPrev( getLast() );
            setLast( temp_list );
            num_objects++;
        }

        public list2 getEntry(int entry_num)
        {
            if (( entry_num < 0 ) ||
                ( entry_num >= num_objects ))
                return( null );

            if ( entry_num == 0 )
                return( getFirst() );

            int i;
            list2 temp_list = first;

            for (i = 1; i < num_objects; i++)
            {
                temp_list = temp_list.getNext();

                if ( i == entry_num)
                    return( temp_list );
            }

            return( null );
        }

        public Object getObject(int entry_num)
        {
            list2 temp_entry = getEntry( entry_num );

            if ( temp_entry != null )
                return( temp_entry.getObject() );

            return( null );
        }

        public Object getObject()
        {
            return( item );
        }

        public void setObject( Object obj )
        {
            item = obj;
        }
    }


    private class Point2Float
    {
        private float x, y;

        /* Constructors */
        public Point2Float(float x_in, float y_in)
        {
            x = x_in;
            y = y_in;
        }

        /* public methods */
        public void setX(float x_in)
        {
            x = x_in;
        }

        public float getX()
        {
            return( x );
        }

        public void setY(float y_in)
        {
            y = y_in;
        }

        public float getY()
        {
            return( y );
        }
    }

    private class tie_point_2_struc
    {
        /* private variables and methods */
        /* constructors */
        /* public methods */
    }



}
