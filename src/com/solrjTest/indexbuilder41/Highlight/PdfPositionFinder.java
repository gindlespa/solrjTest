package indexbuilder41.Highlight;


import java.io.IOException;
import java.util.*;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import java.io.StringWriter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author murphy
 */
public class PdfPositionFinder  extends PDFTextStripper{
    protected float maxHeightOffset = 10f;
    public PdfPositionFinder() throws IOException{
        
        super();
    }
    
    public List<HighlightBox> getPositions(int pageNum, PDPage page, List<HighlightLocation> locs ) throws IOException
    {
        setStartPage(0);
        setEndPage(0);
        
        HighlightLocationUtil.sortLocationsNC(locs);
        this.setSortByPosition(false);
        resetEngine();
        /*
        while( regionIter.hasNext() )
        {
            setStartPage(getCurrentPageNo());
            setEndPage(getCurrentPageNo());
            //reset the stored text for the region so this class
            //can be reused.
            String regionName = regionIter.next();
            Vector<ArrayList<TextPosition>> regionCharactersByArticle = new Vector<ArrayList<TextPosition>>();
            regionCharactersByArticle.add( new ArrayList<TextPosition>() );
            
        }
                */
        PDStream contentStream = page.getContents();
        if( contentStream != null )
        {
            COSStream contents = contentStream.getStream();
            processPage( page, contents );
        }
        
        List<HighlightBox> boxes = getBoxes(locs,page);
        return boxes;
    }
    @Override
    protected void writePage() throws IOException
    {
        //Have it do nothing
    }
    
    protected List<HighlightBox> getBoxes(List<HighlightLocation> locs, PDPage page){
        List<HighlightBox> boxes = new ArrayList<HighlightBox>();
        int size = charactersByArticle.size();
        int currCharPos = 0;
        
        
        for(int i = 0; i < size;i++)
        {
            
            List<TextPosition> textList = (List<TextPosition>) charactersByArticle.get( i );
            Iterator<TextPosition> textIter = textList.iterator();
            while(textIter.hasNext())
            {
                TextPosition position = (TextPosition)textIter.next();
                String stringValue = position.getCharacter();
                Iterator<HighlightLocation> hlIter = locs.iterator();
                Boolean isFirst = true;
                while(hlIter.hasNext())
                {   
                    
                    HighlightLocation loc = hlIter.next();
                    
                    if(currCharPos >= loc.start && currCharPos <= loc.end){
                        
                        addPosition(position,loc,isFirst);
                        
                    }
                    isFirst = false;
                }
                //if(stringValue.matches("[a-zA-Z0-9]"))
                     currCharPos++;
            }
        }
        float xdif = page.getMediaBox().getLowerLeftX();
        float ydif = page.getMediaBox().getLowerLeftY();
          {
              
            for(HighlightLocation loc:locs)
            {
               
                for(HighlightBox bx:loc.boxes)
                {
                 
                  bx.x -= 3;
                  bx.width += 3;
                  bx.y -= 3;
                  bx.height += 6;
                }
            }
            
        }
        return boxes;
    }
    
    protected void addPosition(TextPosition position,  HighlightLocation loc,  Boolean isFirst)
    {
        HighlightBox box=null;
        if(loc.boxes==null){
            loc.boxes = new ArrayList<HighlightBox>();
            box = new HighlightBox();
            loc.boxes.add(box);
            fillNewBox(position,box, loc.color);
            box.lastX = position.getX();
        }
        else
        {
          box = loc.boxes.get(loc.boxes.size()-1);
          if(testSameBox(position,box))
          {
             addToBox(position,box); 
             box.lastX = position.getX();
          }
          else
          {
              box = new HighlightBox();
              loc.boxes.add(box);
              fillNewBox(position,box, loc.color);
              box.lastX = position.getX();
          }
        }
    }
    protected void fillNewBox(TextPosition position, HighlightBox box, String color){
        box.x = position.getX();
        box.y = position.getY();
        box.color = color;
        box.spacewidth = position.getWidthOfSpace();
        if(position.getHeight() != 0)
            box.width =  position.getWidth();
        else 
            box.width = (float) (position.getXScale() * .70); ;
        if(position.getHeight() != 0)
            box.height = position.getHeight();
        else{
            box.height = position.getTextPos().getYScale();
            
        }
        box.y = box.y - box.height;
    }    
    protected Boolean testSameBox(TextPosition position, HighlightBox box)
    {
        float yoff = 0;
        if(position.getHeight() == 0)
            yoff = position.getTextPos().getValue(0, 0);
        else 
            yoff = position.getHeight();
        if(Math.abs(box.y - position.getY() + yoff) <= maxHeightOffset )  // 
            if(position.getX() - box.lastX  <= maxHeightOffset)
                return true;
            else
                return false;
        return false;
    }
    
    
    protected void addToBox(TextPosition position, HighlightBox box)
    {
        float width = position.getWidth();
        if(width==0)
            width = position.getTextPos().getXScale();
        float x = position.getX();
        if(position.getX() <= box.x + 3){
            box.width +=  (float) (position.getXScale() * .70); // position.getTextPos().getXScale();
            box.lastX = position.getX() + box.width;
            if(position.getHeight() > box.height)
                box.height = position.getYScale();
        }
        else
        {
            box.width = Math.abs(x - box.x) +  width + position.getWidthOfSpace();
            if(position.getHeight() > box.height)
                box.height = position.getHeight();
        }
    }
        
}
