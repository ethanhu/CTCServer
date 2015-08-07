package ctc.db.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
/*
 * DatabaseColumnIterator.java
 *
 * Created on October 18, 2007, 5:26 PM
 *
 */

/**
 * Iterate over results from Database.getColumn
 */
public class DatabaseColumnIterator implements Iterator {
    private ResultSet r;
    /** Creates a new instance of DatabaseColumnIterator */
    public DatabaseColumnIterator(ResultSet r) { this.r = r;  }

    public boolean hasNext() {
        try {
            return !r.isLast();
        } catch (SQLException ex) {
            return false;
        }
    }

    public Object next()  {
        Object o;
        try {
            o = r.getObject(1);               
        } catch (SQLException ex) {
            throw new NoSuchElementException();
        }
        try {
            r.next();
        } catch (SQLException ex) {
           // error next time
        }
        return o;
    }
    
    public void remove() {
        throw new UnsupportedOperationException() ;
    }
    
    public void destroy(){
        try {
            r.close();
        } catch (SQLException ex) {
           // done, don't bother with errors
        }
        r=null;
    }
}

