package com.solrjTest;

/**
 * Created by MGindlesperger on 4/11/2016.
 */
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
//import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.SolrClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.pdfbox.pdmodel.PDPage;


/* Example class showing the skeleton of using Tika and
   Sql on the client to index documents from
   both structured documents and a SQL database.

   NOTE: The SQL example and the Tika example are entirely orthogonal.
   Both are included here to make a
   more interesting example, but you can omit either of them.

 */
public class SqlTikaExample {
    //private ConcurrentUpdateSolrServer _server;
    private SolrClient cloud_server;
    private long _start = System.currentTimeMillis();
    private AutoDetectParser _autoParser;
    private int _totalTika = 0;
    private int _totalSql = 0;

    private Collection _docs = new ArrayList();

    public static void main(String[] args) {
        try {
            SqlTikaExample idxer = new SqlTikaExample("http://localhost:8983/solr/gettingstarted");
            String folder = "\\\\cw-process1\\e_drive\\ITP\\Crestor\\201602\\29\\CST0000022\\Production 26\\Doclink";
            File file = new File(folder);
            String[] names = file.list();

            for(String name : names)
            {
                if (new File(folder + "\\" + name).isDirectory())
                {
                    idxer.doTikaDocumentsCloud(new File(folder + "\\" + name));
                }
            }

            //idxer.doSqlDocuments();

            idxer.endIndexingCloud();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SolrIndexSearcher searcher = new SolrIndexSearcher();


    }

    private SqlTikaExample(String url) throws IOException, SolrServerException {
        // Create a multi-threaded communications channel to the Solr server.
        // Could be CommonsHttpSolrServer as well.
        //

            String zkHostString = "127.0.0.1:9983";
            //String zkHostString = "localhost:9983";
            //CloudSolrClient client = new CloudSolrClient()
            SystemDefaultHttpClient cl = new SystemDefaultHttpClient();
            cloud_server = new CloudSolrClient(zkHostString,cl);
            ((CloudSolrClient) cloud_server).setDefaultCollection("gettingstarted");
            ((CloudSolrClient) cloud_server).setZkConnectTimeout(1000);
            ((CloudSolrClient) cloud_server).setZkClientTimeout(1000);
            ((CloudSolrClient) cloud_server).connect();



            //cloud_server.setParser(new XMLResponseParser());
            //cloud_server.setParser(new BinaryResponseParser());
            //cloud_server.setDefaultCollection("gettingstarted");
            //cloud_server.connect();



        //_server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
        // binary parser is used by default for responses
        //_server.setParser(new XMLResponseParser());

        // One of the ways Tika can be used to attempt to parse arbitrary files.
        _autoParser = new AutoDetectParser();
    }

    // Just a convenient place to wrap things up.
//    private void endIndexing() throws IOException, SolrServerException {
//        if (_docs.size() > 0) { // Are there any documents left over?
//            _server.add(_docs, 300000); // Commit within 5 minutes
//        }
//        _server.commit(); // Only needs to be done at the end,
//        // commitWithin should do the rest.
//        // Could even be omitted
//        // assuming commitWithin was specified.
//        long endTime = System.currentTimeMillis();
//        log("Total Time Taken: " + (endTime - _start) +
//                " milliseconds to index " + _totalSql +
//                " SQL rows and " + _totalTika + " documents");
//    }

    private void endIndexingCloud() throws IOException, SolrServerException {
        if (_docs.size() > 0) { // Are there any documents left over?
            cloud_server.add(_docs, 300000); // Commit within 5 minutes
        }
        cloud_server.commit(); // Only needs to be done at the end,
        cloud_server.close();
        // commitWithin should do the rest.
        // Could even be omitted
        // assuming commitWithin was specified.
        long endTime = System.currentTimeMillis();
        log("Total Time Taken: " + (endTime - _start) +
                " milliseconds to index " + _totalSql +
                " SQL rows and " + _totalTika + " documents");
    }

    // I hate writing System.out.println() everyplace,
    // besides this gives a central place to convert to true logging
    // in a production system.
    private static void log(String msg) {
        System.out.println(msg);
    }

    /**
     * ***************************Tika processing here
     */
    // Recursively traverse the filesystem, parsing everything found.
//    private void doTikaDocuments(File root) throws IOException, SolrServerException {
//
//        // Simple loop for recursively indexing all the files
//        // in the root directory passed in.
//        for (File file : root.listFiles()) {
//            if (file.isDirectory()) {
//                doTikaDocuments(file);
//                continue;
//            }
//            // Get ready to parse the file.
//            ContentHandler textHandler = new BodyContentHandler(10000000);
//            Metadata metadata = new Metadata();
//            ParseContext context = new ParseContext();
//            // Tim Allison noted the following, thanks Tim!
//            // If you want Tika to parse embedded files (attachments within your .doc or any other embedded
//            // files), you need to send in the autodetectparser in the parsecontext:
//            context.set(Parser.class, _autoParser);
//
//            InputStream input = new FileInputStream(file);
//
//            // Try parsing the file. Note we haven't checked at all to
//            // see whether this file is a good candidate.
//            try {
//                _autoParser.parse(input, textHandler, metadata, context);
//            } catch (Exception e) {
//                // Needs better logging of what went wrong in order to
//                // track down "bad" documents.
//                log(String.format("File %s failed", file.getCanonicalPath()));
//                e.printStackTrace();
//                continue;
//            }
//            // Just to show how much meta-data and what form it's in.
//            //dumpMetadata(file.getCanonicalPath(), metadata);
//
//            // Index just a couple of the meta-data fields.
//            SolrInputDocument doc = new SolrInputDocument();
//
//            doc.addField("id", file.getCanonicalPath());
//
//            // Crude way to get known meta-data fields.
//            // Also possible to write a simple loop to examine all the
//            // metadata returned and selectively index it and/or
//            // just get a list of them.
//            // One can also use the LucidWorks field mapping to
//            // accomplish much the same thing.
//            String author = metadata.get("Author");
//            String last_modified = metadata.get("Last-Modified");
//            String title = metadata.get("dc:title");
//            String datecreated = metadata.get("Creation-Date");
//            String content_type = metadata.get("Content-Type");
//
//            if (author != null) {
//                doc.addField("author", author);
//            }
//
//            if (title != null) {
//                doc.addField("title", title);
//            }
//
//            if (last_modified != null) {
//                doc.addField("last_modified", last_modified);
//            }
//
//            if (datecreated != null) {
//                doc.addField("date_created", datecreated);
//            }
//
//            if (content_type != null) {
//                doc.addField("content_type", content_type);
//            }
//
//            doc.addField("text", textHandler.toString());
//
//            _docs.add(doc);
//            ++_totalTika;
//            System.out.println(_totalTika);
//
//            // Completely arbitrary, just batch up more than one document
//            // for throughput!
//            if (_docs.size() >= 100) {
//                // Commit within 5 minutes.
//                UpdateResponse resp = _server.add(_docs, 120000);
//                if (resp.getStatus() != 0) {
//                    log("Some horrible error has occurred, status is: " +
//                            resp.getStatus());
//                }
//                _docs.clear();
//            }
//        }
//    }

    // Just to show all the metadata that's available.
    private void dumpMetadata(String fileName, Metadata metadata) {
        log("Dumping metadata for file: " + fileName);
        for (String name : metadata.names()) {
            log(name + ":" + metadata.get(name));
        }
        log("nn");
    }

    /**
     * ***************************SQL processing here
     */
//    private void doSqlDocuments() throws SQLException {
//        Connection con = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            log("Driver Loaded......");
//
//            con = DriverManager.getConnection("jdbc:mysql://192.168.1.103:3306/test?"
//                    + "user=testuser&password=test123");
//
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery("select id,title,text from test");
//
//            while (rs.next()) {
//                // DO NOT move this outside the while loop
//                SolrInputDocument doc = new SolrInputDocument();
//                String id = rs.getString("id");
//                String title = rs.getString("title");
//                String text = rs.getString("text");
//
//                doc.addField("id", id);
//                doc.addField("title", title);
//                doc.addField("text", text);
//
//                _docs.add(doc);
//                ++_totalSql;
//
//                // Completely arbitrary, just batch up more than one
//                // document for throughput!
//                if (_docs.size() > 1000) {
//                    // Commit within 5 minutes.
//                    UpdateResponse resp = _server.add(_docs, 300000);
//                    if (resp.getStatus() != 0) {
//                        log("Some horrible error has occurred, status is: " +
//                                resp.getStatus());
//                    }
//                    _docs.clear();
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (con != null) {
//                con.close();
//            }
//        }
//    }

    // Recursively traverse the filesystem, parsing everything found.
    private void doTikaDocumentsCloud(File root) throws IOException, SolrServerException {

        // Simple loop for recursively indexing all the files
        // in the root directory passed in.
        for (File file : root.listFiles()) {
            if (file.isDirectory()) {
                doTikaDocumentsCloud(file);
                continue;
            }
            // Get ready to parse the file.
            ContentHandler textHandler = new BodyContentHandler(10000000);
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            // Tim Allison noted the following, thanks Tim!
            // If you want Tika to parse embedded files (attachments within your .doc or any other embedded
            // files), you need to send in the autodetectparser in the parsecontext:
            context.set(Parser.class, _autoParser);

            InputStream input = new FileInputStream(file);

            // Try parsing the file. Note we haven't checked at all to
            // see whether this file is a good candidate.
            try {
                _autoParser.parse(input, textHandler, metadata, context);
            } catch (Exception e) {
                // Needs better logging of what went wrong in order to
                // track down "bad" documents.
                log(String.format("File %s failed", file.getCanonicalPath()));
                e.printStackTrace();
                continue;
            }
            // Just to show how much meta-data and what form it's in.
            //dumpMetadata(file.getCanonicalPath(), metadata);

            // Index just a couple of the meta-data fields.
            SolrInputDocument doc = new SolrInputDocument();

            doc.addField("id", file.getCanonicalPath());

            // Crude way to get known meta-data fields.
            // Also possible to write a simple loop to examine all the
            // metadata returned and selectively index it and/or
            // just get a list of them.
            // One can also use the LucidWorks field mapping to
            // accomplish much the same thing.
            String author = metadata.get("Author");
            String last_modified = metadata.get("Last-Modified");
            String title = metadata.get("dc:title");
            String datecreated = metadata.get("Creation-Date");
            String content_type = metadata.get("Content-Type");

            if (author != null) {
                doc.addField("author", author);
            }

            if (title != null) {
                doc.addField("title", title);
            }

            if (last_modified != null) {
                doc.addField("last_modified", last_modified);
            }

            if (datecreated != null) {
                doc.addField("date_created", datecreated);
            }

            if (content_type != null) {
                doc.addField("content_type", content_type);
            }

            doc.addField("text", textHandler.toString());

            _docs.add(doc);
            ++_totalTika;
            System.out.println(_totalTika);

            // Completely arbitrary, just batch up more than one document
            // for throughput!
            if (_docs.size() >= 100) {
                // Commit within 5 minutes.
                UpdateResponse resp = cloud_server.add(_docs, 120000);// _server.add(_docs, 120000);
                if (resp.getStatus() != 0) {
                    log("Some horrible error has occurred, status is: " +
                            resp.getStatus());
                }
                _docs.clear();
            }
        }
    }
}
