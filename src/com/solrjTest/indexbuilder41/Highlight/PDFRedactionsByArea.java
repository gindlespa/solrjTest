/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Highlight;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.pdfbox.util.TextPosition;


import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * This will extract text from a specified region in the PDF.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */
public class PDFRedactionsByArea extends PDFTextStripper
{
    private List<String> regions = new ArrayList<String>();
    private Map<String,Rectangle2D> regionArea = new HashMap<String,Rectangle2D>();
    private Map<String,Vector<ArrayList<TextPosition>>> regionCharacterList = 
        new HashMap<String,Vector<ArrayList<TextPosition>>>();
    private Map<String,StringWriter> regionText = new HashMap<String,StringWriter>();

    /**
     * Constructor.
     * @throws IOException If there is an error loading properties.
     */
    public PDFRedactionsByArea() throws IOException
    {
        super();
        setPageSeparator( "" );
    }

        
    /**
     * Instantiate a new PDFTextStripperArea object. Loading all of the operator
     * mappings from the properties object that is passed in. Does not convert
     * the text to more encoding-specific output.
     * 
     * @param props
     *            The properties containing the mapping of operators to
     *            PDFOperator classes.
     * 
     * @throws IOException
     *             If there is an error reading the properties.
     */
    public PDFRedactionsByArea(Properties props) throws IOException
    {
        super(props);
        setPageSeparator("");
    }

    /**
     * Instantiate a new PDFTextStripperArea object. This object will load
     * properties from PDFTextStripper.properties and will apply
     * encoding-specific conversions to the output text.
     * 
     * @param encoding
     *            The encoding that the output will be written in.
     * @throws IOException
     *             If there is an error reading the properties.
     */
    public PDFRedactionsByArea(String encoding) throws IOException
    {
        super(encoding);
        setPageSeparator("");
    }
    
    public void clearRegions(){
        regions.clear();
        regionArea.clear();
    }
   /**
     * Add a new region to group text by.
     *
     * @param regionName The name of the region.
     * @param rect The rectangle area to retrieve the text from.
     */
    
    public void addRegion( String regionName, Rectangle2D rect )
    {
        regions.add( regionName );
        regionArea.put( regionName, rect );
    }

    /**
     * Get the list of regions that have been setup.
     *
     * @return A list of java.lang.String objects to identify the region names.
     */
    public List<String> getRegions()
    {
        return regions;
    }

    /**
     * Get the text for the region, this should be called after extractRegions().
     *
     * @param regionName The name of the region to get the text from.
     * @return The text that was identified in that region.
     */
    public String getTextForRegion( String regionName )
    {
        StringWriter text = regionText.get( regionName );
        return text.toString();
    }

    /**
     * Process the page to extract the region text.
     *
     * @param page The page to extract the regions from.
     * @throws IOException If there is an error while extracting text.
     */
    public void extractRegions( PDPage page, int pageNum ) throws IOException
    {
        this.currentPageNo = pageNum;
        Iterator<String> regionIter = regions.iterator();
        while( regionIter.hasNext() )
        {
            setStartPage(getCurrentPageNo());
            setEndPage(getCurrentPageNo());
            //reset the stored text for the region so this class
            //can be reused.
            String regionName = regionIter.next();
            Vector<ArrayList<TextPosition>> regionCharactersByArticle = new Vector<ArrayList<TextPosition>>();
            regionCharactersByArticle.add( new ArrayList<TextPosition>() );
            regionCharacterList.put( regionName, regionCharactersByArticle );
            regionText.put( regionName, new StringWriter() );
        }

        PDStream contentStream = page.getContents();
        if( contentStream != null )
        {
            COSStream contents = contentStream.getStream();
            processPage( page, contents );
        }
    }

    
    /**
     * {@inheritDoc}
     */
    protected void oprocessTextPosition( TextPosition text )
    {
        Iterator<String> regionIter = regionArea.keySet().iterator();
        Boolean uncovered = true;
        while( regionIter.hasNext() )
        {
            String region = regionIter.next();
            Rectangle2D rect = regionArea.get( region );
            if( rect.contains( text.getX(), text.getY() ) )
            {
                charactersByArticle = (Vector)regionCharacterList.get( region );
                uncovered = false;
                
            }
            
        }
        if(uncovered)
            super.processTextPosition( text );
    }

    
    /**
     * This will print the processed page text to the output stream.
     *
     * @throws IOException If there is an error writing the text.
     */
    public void writePage() throws IOException
    {
        this.output = new StringWriter();
        super.writePage();
        
        /*
        Iterator<String> regionIter = regionArea.keySet().iterator();
        while( regionIter.hasNext() )
        {
            String region = regionIter.next();
            charactersByArticle = (Vector)regionCharacterList.get( region );
            output = regionText.get( region );
            super.writePage();
        }
        * 
        */
    }
    /*
    protected StringBuilder normalizeAdd(LinkedList<WordWithTextPositions> normalized,
            StringBuilder lineBuilder, List<TextPosition> wordPositions, TextPosition text, boolean iscovered)
    {
        if (text instanceof PDFTextStripper.WordSeparator) 
        {
            normalized.add(createWord(lineBuilder.toString(), new ArrayList<TextPosition>(wordPositions)));
            lineBuilder = new StringBuilder();
            wordPositions.clear();
        }
        else 
        {
            if(!iscovered)
                lineBuilder.append(text.getCharacter());
            else
                lineBuilder.append("\013");
            wordPositions.add(text);
        }
        return lineBuilder;
    }
     protected List<WordWithTextPositions> normalize(List<TextPosition> line, boolean isRtlDominant, boolean hasRtl)
    {
        LinkedList<WordWithTextPositions> normalized = new LinkedList<WordWithTextPositions>();
        StringBuilder lineBuilder = new StringBuilder();
        List<TextPosition> wordPositions = new ArrayList<TextPosition>();
        // concatenate the pieces of text in opposite order if RTL is dominant
        if (isRtlDominant)
        {
            int numberOfPositions = line.size();
            for(int i = numberOfPositions-1;i>=0;i--)
            {
                
                lineBuilder = normalizeAdd(normalized, lineBuilder, wordPositions, line.get(i),iscovered(line.get(i)));
            }
        }
        else
        {
            for(TextPosition text : line)
            {
                lineBuilder = normalizeAdd(normalized, lineBuilder, wordPositions, text,iscovered(text));
            }
        }
        if (lineBuilder.length() > 0) 
        {
            normalized.add(createWord(lineBuilder.toString(), wordPositions));
        }
        return normalized;
    }
     */
     protected boolean iscovered(TextPosition pos){
         Iterator<String> regionIter = regionArea.keySet().iterator();
                Boolean iscovered = false;
                while( regionIter.hasNext() )
                {
                    String region = regionIter.next();
                    Rectangle2D rect = regionArea.get( region );
                    if( rect.contains( pos.getX(), pos.getY() ) )
                    {
                        return true;
                    }
                }
         return false;
     }
     
    protected List<String> normali1ze(List<TextPosition> line, boolean isRtlDominant, boolean hasRtl){
        LinkedList<String> normalized = new LinkedList<String>();
        StringBuilder lineBuilder = new StringBuilder();
        
        for(TextPosition text : line){
            if (text instanceof PDFTextStripper.WordSeparator) {
                String lineStr = lineBuilder.toString();
                if (hasRtl) {
                    lineStr = normalize.makeLineLogicalOrder(lineStr,isRtlDominant);
                }
                lineStr = normalize.normalizePres(lineStr);
                normalized.add(lineStr);
                lineBuilder = new StringBuilder();
            }
            else 
            {
                Iterator<String> regionIter = regionArea.keySet().iterator();
                Boolean iscovered = false;
                while( regionIter.hasNext() )
                {
                    String region = regionIter.next();
                    Rectangle2D rect = regionArea.get( region );
                    if( rect.contains( text.getX(), text.getY() ) )
                    {
                        iscovered = true;
                        break;
                    }
                }
            if(iscovered)
                lineBuilder.append("\013");
            else
                lineBuilder.append(text.getCharacter());
            }   
        }
        if (lineBuilder.length() > 0) {
            String lineStr = lineBuilder.toString();
            if (hasRtl) {
                lineStr = normalize.makeLineLogicalOrder(lineStr,isRtlDominant);
            }
            lineStr = normalize.normalizePres(lineStr);
            normalized.add(lineStr);
        }
        return normalized;
    }
     
    public String readPage(){
        String outstring = output.toString();
        output = new StringWriter();
        return outstring;
    }
    
}
