/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package indexbuilder41.Tests;
import indexbuilder41.AlphanumericTokenizer;
import indexbuilder41.AutoPhrasingTokenFilter;
import indexbuilder41.MurphyAnalyzer;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
/**
 *
 * @author murphy
 */
public class AutoTest {
    public static void main(String[] args) throws Exception{
        //testAutoPhrase();
        testAnalyzer();
    }
    public static  void testAutoPhrase( ) throws Exception {
    // sets up a list of phrases - Normally this would be supplied by AutoPhrasingTokenFilterFactory
    final CharArraySet phraseSets = new CharArraySet( Arrays.asList("income tax", "tax refund", "property tax"), false);
         
    final String input = "what is my Income'tax refund this year now that my property tax is so high 111";
    AlphanumericTokenizer wt = new AlphanumericTokenizer();
    wt.setReader(new StringReader(input));
    LowerCaseFilter filter = new LowerCaseFilter(wt);
    AutoPhrasingTokenFilter aptf = new AutoPhrasingTokenFilter(Version.LATEST, filter, phraseSets, false );
    CharTermAttribute term = aptf.addAttribute(CharTermAttribute.class);
    aptf.reset();

    boolean hasToken = false;
    do {
      hasToken = aptf.incrementToken( );
      if (hasToken) System.out.println( "token:'" + term.toString( ) + "'" );
    } while (hasToken);
  }
   public static void testAnalyzer() throws IOException, Exception
   {    
       ArrayList<String> al = new ArrayList<>();
       al.add("income tax");
       al.add("tax refund");
       al.add("property tax");
        final String input = "what is my Income'tax refund this year now that my property tax is so high 111";
       MurphyAnalyzer ma = new MurphyAnalyzer();
       TokenStream ts = ma.tokenStream("text", input);
       boolean hasToken = false;
    do {
      hasToken = ts.incrementToken( );
      if (hasToken) {
          
          System.out.print( "Term:'" + ts.getAttribute(CharTermAttribute.class).toString( )  + "'" );
         /*
          System.out.print("\t");
          System.out.print( "  " + PositionIncrementAttribute.class.getName() + ":'" + ts.getAttribute(PositionIncrementAttribute.class).toString( ) + "'" );
          System.out.print("\t");
          System.out.print( "  " + OffsetAttribute.class.getName() + ":'" + ts.getAttribute(OffsetAttribute.class).toString( ) + "'" );
          */
          System.out.println();
      }
    } while (hasToken);
       
   }
}
