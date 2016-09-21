package indexbuilder41.jusk;



import com.sun.media.imageio.plugins.tiff.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.jpedal.PdfDecoder;
import org.jpedal.constants.JPedalSettings;
import org.jpedal.exception.PdfException;
import org.jpedal.fonts.FontMappings;
import org.jpedal.io.PdfFileReader;
import org.jpedal.objects.PdfPageData;
import org.jpedal.parser.DecodeStatus;
import org.jpedal.parser.PdfStreamDecoder;
import org.w3c.dom.Element;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author murphy
 */
public class jutil {
    private static int DPI_X = 300;
    private static int DPI_Y = 300;
    final static int XRES_TAG = 282;
    final static int YRES_TAG = 283;
private static String METADATA_NAME =
        "com_sun_media_imageio_plugins_tiff_image_1.0";
private static final char[] BITS_PER_SAMPLE = new char[] { 4 };
private static final char[] COMPRESSION = new char[] { BaselineTIFFTagSet.COMPRESSION_CCITT_T_6 };
private static final char[] INCH_RESOLUTION_UNIT = new char[] { 2 };
  public static boolean isWhite(BufferedImage img)
  {
    boolean white = true;
    for (int x = 1; x < img.getWidth(); x += 5)
    {
      for (int y = 1; y < img.getHeight(); y += 5)
        if (img.getRGB(x, y) != -1)
        {
          white = false;
          break;
        }
      if (!white) {
        break;
      }
    }
    return white;
  }
  public static boolean redactImage(BufferedImage img,int x, int y, int width, int height)
  {
    boolean white = true;
    for (int xp = x; x < x+ width; x++)
    {
      for (int wp = y; y < y + height; y++)
        img.setRGB(x, y,0);
        
    }
    return white;
  }
  
  public static BufferedImage doJpetal(PdfDecoder document, int page, ServletContext conf) throws Exception {
      BufferedImage img;
       
       img = doJpetal(document, page);
       System.out.println(img.getWidth());
       System.out.println(img.getHeight());
       document.closePdfFile();
      return img;
      
  }
  /*
  public static BufferedImage doJpetal(String filepath, int page) throws Exception {
      BufferedImage img;
       InputStream ips = new FileInputStream(filepath);
       PdfDecoder document = new PdfDecoder();
       document.openPdfFileFromInputStream(ips, true);
       img = doJpetal(document, page);
       System.out.println(img.getWidth());
       System.out.println(img.getHeight());
       document.closePdfFile();
      return img;
      
  }
  * 
  */
  public static BufferedImage doJpetal(PdfDecoder decode_pdf, int page) throws Exception {
      
      return doJpetal(decode_pdf, page, (float)1.0);
  }
  public static BufferedImage doJpetal(PdfDecoder decode_pdf, int page, float factor) throws Exception {
    
      decode_pdf.useHiResScreenDisplay(false);
    
    BufferedImage bimg = null;
    boolean pause = false;
    if (page == 1) {
      pause = true;
    }
    int timeout = 10;
    timeout *= 10;
    org.jpedal.io.PdfFileReader.alwaysCacheInMemory = -1;
    decode_pdf.decodePage(page);
    
    
    
    
   
    while ((!decode_pdf.isPageAvailable(page)) && (timeout > 0))
    {
      Thread.sleep(100L);

      if (timeout-- < 0) {
        throw new Exception("Timeout");
      }
    }
    Boolean isOK = Boolean.valueOf(false);
    while ((!isOK.booleanValue()) && (timeout > 0))
    {
      try {
        if (timeout-- < 0)
          throw new Exception("Timeout");
        String s = decode_pdf.getPageDecodeReport();

        
        PageFormat f = decode_pdf.getPageFormat(page);
        double height = f.getHeight();
        double width = f.getWidth();
        
        //decode_pdf.setExtractionMode(0, factor);
        PdfPageData pageData = decode_pdf.getPdfPageData();

        int scaling = 2;
        width=factor*pageData.getMediaBoxWidth(page);
        height=factor*pageData.getMediaBoxHeight(page);

        Map mapValues = new HashMap();
        mapValues.put(JPedalSettings.EXTRACT_AT_PAGE_SIZE,
        new String[]{String.valueOf(width),String.valueOf(height)});

        mapValues.put(JPedalSettings.PAGE_SIZE_OVERRIDES_IMAGE,
                    Boolean.TRUE);

        bimg = decode_pdf.getPageAsHiRes(page, mapValues,false);
      }
      catch (PdfException e)
      {
        e.printStackTrace();
      }
      if (bimg != null)
        isOK = Boolean.valueOf(true);
      else
        Thread.sleep(100L);
    }
    return bimg;
  }
  // PRODUCTION SOFTWARE
  public static void WriteImageCitation(BufferedImage bimage,int x3, int y3, int x4, int y4,  String savefilename) throws FileNotFoundException, IOException
  {
    int ht = bimage.getHeight();
    int wd = bimage.getWidth();
    BufferedImage nimage = bimage.getSubimage(x3*2, ht - y3*2, (x4-x3)*2, (y3-y4)*2);

    MyImageIOOutputStream  iostream = new MyImageIOOutputStream(new FileOutputStream("d:\\output\\" + savefilename)); 
    ImageIO.write(nimage, "JPG", iostream);
    
  }
  public static void writeImageRedact(BufferedImage bimage,StampingInfo stampInfo, String type, String outpath, int resolution, int scaleover, int color, List<redaction> rlist, String text) throws IOException
  {
    int ht = bimage.getHeight();
    int wd = bimage.getWidth();
  
    if (resolution == 0)
    {
      double tres = 3145728.0D / (ht * wd);
      resolution = (int)(100.0D * Math.sqrt(tres));
      if (resolution > 100) {
        resolution = 100;
      }
    }

    ht = ht * resolution / 100;
    wd = wd * resolution / 100;

    int bcolor = 1;
    if (color == 1)
      bcolor = 12;
    if (color == 2)
      bcolor = 10;
    if (color == 3)
      bcolor = 5;
    if (color == 4)
      bcolor = 6;
    BufferedImage nimage = new BufferedImage(wd, ht+ stampInfo.toppadding + stampInfo.bottompadding, bcolor);

    Graphics2D g2 = nimage.createGraphics();
    g2.setColor(Color.WHITE);
    g2.fillRect(0, 0, wd, ht + stampInfo.toppadding + stampInfo.bottompadding);
    g2.drawImage(bimage, 0, 0 + stampInfo.toppadding, wd, ht, null);
    g2.setColor(Color.BLACK);
    float factor = 300/72;
    for(redaction r:rlist){
        g2.fillRect(Math.round(r.x * factor),Math.round((r.y) * factor) + stampInfo.toppadding,Math.round( r.width * factor),Math.round( r.height * factor));
    }
        for (Stamp stamp : stampInfo.slist) {
            int fontSize = stamp.fontsize;
            String fontname = stamp.fontname;
            int x;
            x = 0;
            int y;
            y = 0;
            switch(stamp.leftrightcenter){
                
                case LEFT:
                    x = stamp.X;
                    break;
                case RIGHT:
                    x = wd - stamp.X;
                    break;
                case CENTER:
                    x = wd/2 - stamp.X;
                    break;
                default:
                    x = stamp.X;
                    break;
            }   switch(stamp.topBottom){
                case TOP:
                    y = stamp.Y + fontSize;
                    break;
                case BOTTOM:
                    y = ht - stamp.Y + stampInfo.toppadding + stampInfo.bottompadding;
                    break;
                default:
                    y = stamp.Y;
                    break;
            }   
            String stext = stamp.text;
            if(stext.equals("STAMP"))
                stext = text;
            g2.setFont(new Font(fontname,Font.PLAIN,fontSize));
            g2.drawString(stext, x, y);
        }
    g2.dispose();
    
    /*
    TIFFEncodeParam param = new TIFFEncodeParam();
    param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
    param.setTileSize(wd, ht);
    param.setReverseFillOrder(true);
    param.setLittleEndian(false);
    
    
    long[][] l1 = new long[][] { { 300, 1 } };
    //TIFFTag tag = new TIFFTag();
    com.sun.media.jai.codec.TIFFField xres = new com.sun.media.jai.codec.TIFFField(XRES_TAG,5, 1,(Object)l1);
    com.sun.media.jai.codec.TIFFField yres = new com.sun.media.jai.codec.TIFFField(YRES_TAG,5, 1,l1);
    com.sun.media.jai.codec.TIFFField WhiteIsZero = new com.sun.media.jai.codec.TIFFField(262, com.sun.media.jai.codec.TIFFField.TIFF_SHORT, 1, (Object) new char[] { 0 });
    
    com.sun.media.jai.codec.TIFFField ex3 = new com.sun.media.jai.codec.TIFFField(296, com.sun.media.jai.codec.TIFFField.TIFF_SHORT, 1, (Object) new char[] {2});
    param.setExtraFields(new com.sun.media.jai.codec.TIFFField[] { xres, yres, WhiteIsZero, ex3 });
    param.setTileSize(nimage.getWidth(), nimage.getHeight());                        
                      
                           
    FileOutputStream out = new FileOutputStream(outpath);
    ByteArrayOutputStream outTiff = new ByteArrayOutputStream();
                           //OutputStream outTiff = new FileOutputStream(tmpFile);
    ImageEncoder encoder = TIFFCodec.createImageEncoder("tiff", outTiff, param);                         
    encoder.encode(nimage);
    outTiff.close(); 
    outTiff.writeTo(out);
    out.close();
    */
    //ImageIO.write(nimage, "tif",new File("e:\\out.tiff"));
    
    Iterator it = ImageIO.getImageWritersBySuffix(type);
    ImageWriter writer = (ImageWriter)it.next();
    
    IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(null);
    if(type.equals("tif"))
    {
        TIFFImageWriteParam imageWriteParam = null;
        imageWriteParam= (TIFFImageWriteParam)writer.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(imageWriteParam.MODE_EXPLICIT );
        imageWriteParam.setCompressionType("CCITT T.6");
        
        imageWriteParam.setTiling(wd, ht, 0, 0);
        
        ImageTypeSpecifier imageType =
            ImageTypeSpecifier.createFromRenderedImage(bimage);
        streamMetadata =
            writer.getDefaultImageMetadata(imageType, null);
       streamMetadata = setDPIViaAPI(streamMetadata, ht + stampInfo.bottompadding + stampInfo.toppadding);
       FileImageOutputStream iostream = new FileImageOutputStream(new File(outpath));

        writer.setOutput(iostream);
        writer.write( null, new IIOImage(nimage, null, streamMetadata), imageWriteParam);
        writer.dispose();
    }
    if(type.equals("jpg"))
    {   
        JPEGImageWriteParam imageWriteParam;
        imageWriteParam= (JPEGImageWriteParam)writer.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT );
        imageWriteParam.setCompressionQuality(0.5F);
        //imageWriteParam.setCompressionType("CCITT T.6");
        
        //imageWriteParam.setTiling(wd, ht, 0, 0);
        
        ImageTypeSpecifier imageType =
            ImageTypeSpecifier.createFromRenderedImage(bimage);
        
        streamMetadata =
            writer.getDefaultImageMetadata(imageType, null);
       //streamMetadata = setDPIViaAPI(streamMetadata, ht + stampInfo.bottompadding + stampInfo.toppadding);
        IIOMetadata data = writer.getDefaultImageMetadata(new ImageTypeSpecifier(nimage), imageWriteParam);
		Element tree = (Element)data.getAsTree("javax_imageio_jpeg_image_1.0");
		Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
		jfif.setAttribute("Xdensity", Integer.toString(DPI_X));
		jfif.setAttribute("Ydensity", Integer.toString(DPI_Y));
		jfif.setAttribute("resUnits", "1"); 
                data.setFromTree("javax_imageio_jpeg_image_1.0", tree);
       FileImageOutputStream iostream = new FileImageOutputStream(new File(outpath));

    writer.setOutput(iostream);
    writer.write( null, new IIOImage(nimage, null, data), imageWriteParam);
    writer.dispose();
    }
    
    
    
    
  }
  
  public static void writeImage(BufferedImage bimage, OutputStream outstream, int resolution, int scaleover, int color)
    throws Exception
  {
    //JPEGImageWriteParam param = new JPEGImageWriteParam(null);
    //param.setCompressionMode(2);

    int ht = bimage.getHeight();
    int wd = bimage.getWidth();

    if (resolution == 0)
    {
      double tres = 3145728.0D / (ht * wd);
      resolution = (int)(100.0D * Math.sqrt(tres));
      if (resolution > 100) {
        resolution = 100;
      }
    }

    ht = ht * resolution / 100;
    wd = wd * resolution / 100;

    int bcolor = 1;
    if (color == 1)
      bcolor = 12;
    if (color == 2)
      bcolor = 10;
    if (color == 3)
      bcolor = 5;
    if (color == 4)
      bcolor = 6;
    BufferedImage nimage = new BufferedImage(wd, ht, bcolor);

    Graphics2D g2 = nimage.createGraphics();
    g2.drawImage(bimage, 0, 0, wd, ht, null);
    g2.dispose();
    //param.setCompressionQuality(0.65F);
    //Iterator it = ImageIO.getImageWritersBySuffix("png");
    
    //ImageWriter writer = (ImageWriter)it.next();
    
    MyImageIOOutputStream  iostream = new MyImageIOOutputStream(outstream); 
    ImageIO.write(nimage, "PNG", iostream);
    /*
    byte[] out = baos.toByteArray();
    ByteArrayInputStream istream = new ByteArrayInputStream(out);
    PngOptimizer om = new PngOptimizer();
    PngImage source = new PngImage(istream);
    PngImage optimized = om.optimize(source, true, 5);
    

    ByteArrayOutputStream optimizedBytes = new ByteArrayOutputStream();
    optimized.writeDataOutputStream(optimizedBytes);
    optimizedBytes.writeTo(response.getOutputStream());
    
    //MyImageIOOutputStream  iostream = new MyImageIOOutputStream(response.getOutputStream()); 
    
    //baos.writeTo(response.getOutputStream());
    /*
    writer.setOutput(iostream);
    writer.write(null, new IIOImage(nimage, null, null), null);
    writer.dispose();
    */
  }
  

  
  public static BufferedImage getRotatedImage(BufferedImage src, int angle) { 

    if (src == null) {

      System.out.println("getRotatedImage: input image is null");
      return null;

    }
    GraphicsEnvironment ge = GraphicsEnvironment.
              getLocalGraphicsEnvironment();
    int transparency = src.getColorModel().getTransparency();
    GraphicsDevice[] gs =
              ge.getScreenDevices();
    GraphicsDevice gd = gs[0];
    GraphicsConfiguration[] gc = gd.getConfigurations();
    BufferedImage dest =  gc[0].createCompatibleImage(
                              src.getWidth(), src.getHeight(), transparency );
    Graphics2D g2d = dest.createGraphics();

    AffineTransform origAT = g2d.getTransform(); 

    AffineTransform rot = new AffineTransform(); 
    rot.rotate(Math.toRadians(angle), src.getWidth()/2, src.getHeight()/2); 
    g2d.transform(rot); 

    g2d.drawImage(src, 0, 0, null);   

    g2d.setTransform(origAT);   
    g2d.dispose();

    return dest; 
  }
      private static IIOMetadataNode createRationalNode(String tagName,
                                                      int tagNumber,
                                                      int numerator,
                                                      int denominator) {
        // Create the field node with tag name and number.
        IIOMetadataNode field = new IIOMetadataNode("TIFFField");
        field.setAttribute("name", tagName);
        field.setAttribute("number", "" + tagNumber);

        // Create the RATIONAL node.
        IIOMetadataNode rational = new IIOMetadataNode("TIFFRational");
        rational.setAttribute("value", numerator+"/"+denominator);

        // Create the RATIONAL node and append RATIONAL node.
        IIOMetadataNode rationals = new IIOMetadataNode("TIFFRationals");
        rationals.appendChild(rational);

        // Append RATIONALS node to field node.
        field.appendChild(rationals);

        return field;
    }

    /**
     * Set DPI using API.
     */
    private static IIOMetadata setDPIViaAPI(IIOMetadata imageMetadata, int HEIGHT)
        throws IIOInvalidTreeException {
        // Derive the TIFFDirectory from the metadata.
        TIFFDirectory dir = TIFFDirectory.createFromMetadata(imageMetadata);
        
        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        
        // BitsPerSample tag
        TIFFTag tagBitSample = base
        .getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);

        // Row and Strip tags...
        TIFFTag tagRowStrips = base
        .getTag(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);

        // Compression tag
        TIFFTag tagCompression = base
        .getTag(BaselineTIFFTagSet.TAG_COMPRESSION);
        
        TIFFTag tagResUnit = base.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT);
        
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution fields.
        TIFFField fieldXRes = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL,
                                            1, new long[][] {{DPI_X, 1}});
        TIFFField fieldYRes = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL,
                                            1, new long[][] {{DPI_Y, 1}});
        TIFFTag tag = base.getTag(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION);
        TIFFField f = dir.getTIFFField(tag.getNumber());
        TIFFField fieldBitSample = new TIFFField(tagBitSample,TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE);
        TIFFField fieldRowStrips = new TIFFField(tagRowStrips,TIFFTag.TIFF_LONG, 1, new long[] { HEIGHT });
        TIFFField fieldResUnit = new TIFFField(tagResUnit, TIFFTag.TIFF_SHORT,1, INCH_RESOLUTION_UNIT);

        TIFFField field2 = new TIFFField(tag, TIFFTag.TIFF_SHORT, 1, (Object) new char[] { 0 });
        dir.addTIFFField(fieldXRes);
        dir.addTIFFField(fieldYRes);
        dir.addTIFFField(fieldBitSample);
        dir.addTIFFField(field2);
        dir.addTIFFField(fieldResUnit);
        dir.addTIFFField(fieldRowStrips);
        // Convert to metadata object and return.
        return dir.getAsMetadata();
    }
     private static void setDPIViaDOM(IIOMetadata imageMetadata)
        throws IIOInvalidTreeException {
        // Get the DOM tree.
        IIOMetadataNode root =
            (IIOMetadataNode)imageMetadata.getAsTree(METADATA_NAME);

        // Get the IFD.
        IIOMetadataNode ifd =
            (IIOMetadataNode)root.getElementsByTagName("TIFFIFD").item(0);

        // Get {X,Y}Resolution tags.
        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);

        // Create {X,Y}Resolution nodes.
        IIOMetadataNode nodeXRes = createRationalNode(tagXRes.getName(),
                                                      tagXRes.getNumber(),
                                                      DPI_X, 1);
        IIOMetadataNode nodeYRes = createRationalNode(tagYRes.getName(),
                                                      tagYRes.getNumber(),
                                                      DPI_Y, 1);

        // Append {X,Y}Resolution nodes to IFD node.
        ifd.appendChild(nodeXRes);
        ifd.appendChild(nodeYRes);

        // Set metadata from tree.
        imageMetadata.setFromTree(METADATA_NAME, root);
    }
}
