package indexbuilder41.Highlight;

import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author murphy
 */
public class HighlightLocation implements Comparable<HighlightLocation> {
    public int start;
    public int length;
    public int end;
    public int saStart;
    public int saEnd;
    public int page = 0;
    public String color;
    public String token;
    public List<HighlightBox> boxes = null;
    public int compareTo(HighlightLocation hl)
    {
        if(hl.page < page)
            return 1;
        if(hl.page > page)
            return -1;
        if(hl.start < start)
            return 1;
        if(hl.start > start)
            return -1;
        return 0;
    }
}
