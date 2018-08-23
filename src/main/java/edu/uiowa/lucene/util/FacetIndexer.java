package edu.uiowa.lucene.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class FacetIndexer {
    static Logger logger = Logger.getLogger(FacetIndexer.class);
    static private Directory indexDir = null;
    static private Directory taxoDir = null;
    static Connection conn = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
	conn = getConnection();
	indexDir = FSDirectory.open(new File("/usr/local/CD2H/lucene/facet_test"));
	taxoDir = FSDirectory.open(new File("/usr/local/CD2H/lucene/facet_test_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);

	indexVIVO(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    @SuppressWarnings("deprecation")
    static void indexVIVO(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	PreparedStatement stmt = conn.prepareStatement("select id,site,uri,first_name,last_name,title,ctsa from vivo_aggregated.person natural join vivo.site order by site,last_name,first_name");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String site = rs.getString(2);
	    String uri = rs.getString(3);
	    String firstName = rs.getString(4);
	    String lastName = rs.getString(5);
	    String title = rs.getString(6);
	    boolean ctsa = rs.getBoolean(7);
	    
	    logger.info("site: " + site + "\t" + lastName + ", " + firstName);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("first_name", firstName, Field.Store.YES, Field.Index.ANALYZED));
	    theDocument.add(new Field("content", firstName, Field.Store.NO, Field.Index.ANALYZED));
	    theDocument.add(new Field("last_name", lastName, Field.Store.YES, Field.Index.ANALYZED));
	    theDocument.add(new Field("content", lastName, Field.Store.NO, Field.Index.ANALYZED));
	    theDocument.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
	    theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    if (title == null)
		paths.add(new CategoryPath("Person/VIVO", '/'));
	    else
		paths.add(new CategoryPath("Person/VIVO/"+title, '/'));
		
	    theDocument.add(new Field("site", site, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("content", site, Field.Store.NO, Field.Index.ANALYZED));
	    paths.add(new CategoryPath("Site/"+site, '/'));
	    if (ctsa)
		paths.add(new CategoryPath("CTSA/"+site, '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	}
	stmt.close();
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
	Class.forName("org.postgresql.Driver");
	Properties props = new Properties();
	props.setProperty("user", "");
	props.setProperty("password", "");
//	if (use_ssl.equals("true")) {
//	    props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
//	    props.setProperty("ssl", "true");
//	}
	Connection conn = DriverManager.getConnection("jdbc:postgresql://deep-thought.slis.uiowa.edu/loki", props);
	conn.setAutoCommit(false);
	return conn;
    }
}
