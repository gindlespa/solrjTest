/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexbuilder41.Highlight;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author murphy
 */
public class HighlightLocationUtil {
    public static void sortLocations(List<HighlightLocation> locs)
    {
        // Make sure of no overlap and in order
        Collections.sort(locs);
        for(int i = 0; i < locs.size(); i++)
            locs.get(i).end = locs.get(i).start + locs.get(i).length - 1;
        for(int i = 1; i < locs.size(); i++)
        {
            HighlightLocation hl1 = locs.get(i-1);
            HighlightLocation hl2 = locs.get(i);
            
            if(hl1.page == hl2.page && hl2.saStart  <= hl1.saEnd + 1)
            {
                hl1.length = hl2.start + hl2.length -hl1.start ;
                hl1.end = hl2.end;
                hl1.saEnd = hl2.saEnd;
                locs.remove(i);
                i--;
            }
        }
    }
    
    public static void sortLocationsNC(List<HighlightLocation> locs)
    {
        // Make sure of no overlap and in order
        Collections.sort(locs);
        for(int i = 0; i < locs.size(); i++)
            locs.get(i).end = locs.get(i).start + locs.get(i).length - 1;
        
    }
    
}
