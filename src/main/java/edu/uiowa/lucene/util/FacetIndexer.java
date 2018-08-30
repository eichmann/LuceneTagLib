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

import javax.servlet.jsp.JspTagException;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.Syntax;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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

	static protected String prefix = 
		"PREFIX ld4l: <http://bib.ld4l.org/ontology/>"
			+ "PREFIX ld4lcornell: <http://draft.ld4l.org/cornell/>"
			+ "PREFIX madsrdf: <http://www.loc.gov/mads/rdf/v1#>"
			+ "PREFIX oa: <http://www.w3.org/ns/oa#>"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX void: <http://rdfs.org/ns/void#>"
			+ "PREFIX worldcat: <http://www.worldcat.org/oclc/>"
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>";

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, JspTagException {
        PropertyConfigurator.configure("/Users/eichmann/Documents/Components/log4j.info");
	conn = getConnection();
	indexDir = FSDirectory.open(new File("/usr/local/CD2H/lucene/facet_test"));
	taxoDir = FSDirectory.open(new File("/usr/local/CD2H/lucene/facet_test_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);

	indexGitHubUsers(indexWriter, facetFields);
	indexGitHubOrganizations(indexWriter, facetFields);
	indexGitHubRepositories(indexWriter, facetFields);

	indexNLightenUsers(indexWriter, facetFields);
	indexNLightenOrganizations(indexWriter, facetFields);
	indexNLightenResource(indexWriter, facetFields, "http://schema.org/Movie", "Movie");
	indexNLightenResource(indexWriter, facetFields, "http://schema.org/Course", "Course");
	
	indexCTSAsearch(indexWriter, facetFields);

	indexClinicalTrialOfficialContact(indexWriter, facetFields);
	indexClinicalTrials(indexWriter, facetFields);
	
	indexNIHFOA(indexWriter, facetFields);

	indexDataMed(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    @SuppressWarnings("deprecation")
    static void indexNLightenUsers(IndexWriter indexWriter, FacetFields facetFields) throws JspTagException, IOException {
	int count = 0;
	logger.info("indexing N-Lighten users...");

	org.apache.jena.query.ResultSet rs = getResultSet(prefix
		+ " SELECT ?s ?lab where { "
		+ "  ?s rdf:type <http://xmlns.com/foaf/0.1/Person> . "
		+ "  OPTIONAL { ?s rdfs:label ?labelUS  FILTER (lang(?labelUS) = \"en-US\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelENG FILTER (langMatches(?labelENG,\"en\")) } "
		+ "  OPTIONAL { ?s rdfs:label ?label    FILTER (lang(?label) = \"\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelANY FILTER (lang(?labelANY) != \"\") } "
		+ "  BIND(COALESCE(?labelUS, ?labelENG, ?label, ?labelANY ) as ?lab) " + " } ");
	while (rs.hasNext()) {
	    QuerySolution sol = rs.nextSolution();
	    String subjectURI = sol.get("?s").toString();
	    String label = sol.get("?lab") == null ? null : sol.get("?lab").asLiteral().getString();
	    
	    logger.info("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (label != null ) {
		theDocument.add(new Field("label", label, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", label, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    paths.add(new CategoryPath("Entity/Person/unknown", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	logger.info("\tusers indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexNLightenOrganizations(IndexWriter indexWriter, FacetFields facetFields) throws JspTagException, IOException {
	int count = 0;
	logger.info("indexing N-Lighten organizations...");

	org.apache.jena.query.ResultSet rs = getResultSet(prefix
		+ " SELECT ?s ?lab where { "
		+ "  ?s rdf:type <http://xmlns.com/foaf/0.1/Organization> . "
		+ "  OPTIONAL { ?s rdfs:label ?labelUS  FILTER (lang(?labelUS) = \"en-US\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelENG FILTER (langMatches(?labelENG,\"en\")) } "
		+ "  OPTIONAL { ?s rdfs:label ?label    FILTER (lang(?label) = \"\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelANY FILTER (lang(?labelANY) != \"\") } "
		+ "  BIND(COALESCE(?labelUS, ?labelENG, ?label, ?labelANY ) as ?lab) " + " } ");
	while (rs.hasNext()) {
	    QuerySolution sol = rs.nextSolution();
	    String subjectURI = sol.get("?s").toString();
	    String label = sol.get("?lab") == null ? null : sol.get("?lab").asLiteral().getString();
	    
	    logger.info("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (label != null ) {
		theDocument.add(new Field("label", label, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", label, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    paths.add(new CategoryPath("Entity/Organization/unknown", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	logger.info("\torganizations indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexNLightenResource(IndexWriter indexWriter, FacetFields facetFields, String clss, String facet) throws JspTagException, IOException {
	int count = 0;
	logger.info("indexing N-Lighten users...");

	org.apache.jena.query.ResultSet rs = getResultSet(prefix
		+ " SELECT ?s ?lab ?desc where { "
		+ "  ?s rdf:type <"+ clss + "> . "
		+ "  OPTIONAL { ?s <http://schema.org/description> ?desc } "
		+ "  OPTIONAL { ?s rdfs:label ?labelUS  FILTER (lang(?labelUS) = \"en-US\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelENG FILTER (langMatches(?labelENG,\"en\")) } "
		+ "  OPTIONAL { ?s rdfs:label ?label    FILTER (lang(?label) = \"\") } "
		+ "  OPTIONAL { ?s rdfs:label ?labelANY FILTER (lang(?labelANY) != \"\") } "
		+ "  BIND(COALESCE(?labelUS, ?labelENG, ?label, ?labelANY ) as ?lab) " + " } ");
	while (rs.hasNext()) {
	    QuerySolution sol = rs.nextSolution();
	    String subjectURI = sol.get("?s").toString();
	    String label = sol.get("?lab") == null ? null : sol.get("?lab").asLiteral().getString();
	    String description = sol.get("?desc") == null ? null : sol.get("?desc").asLiteral().getString();
	    
	    logger.info("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (label != null ) {
		theDocument.add(new Field("label", label, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", label, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (description != null ) {
		theDocument.add(new Field("description", description, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", description, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    paths.add(new CategoryPath("Entity/Educational Resource/" + facet, '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	logger.info("\t" + facet + "s indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexGitHubUsers(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing GitHub users...");
	PreparedStatement stmt = conn.prepareStatement("select login,name,bio from github.user,github.search_user where uid=id and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String login = rs.getString(1);
	    String name = rs.getString(2);
	    String bio = rs.getString(3);
	    
	    logger.info("login: " + login + "\t" + name);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "GitHub", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/GitHub", '/'));

	    theDocument.add(new Field("uri", "http://github.com/"+login, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (name != null ) {
		theDocument.add(new Field("label", name, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", name, Field.Store.NO, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", login, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", login, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (bio != null) {
		theDocument.add(new Field("content", bio, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    paths.add(new CategoryPath("Entity/Person/unknown", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\tusers indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexGitHubOrganizations(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing GitHub organizations...");
	PreparedStatement stmt = conn.prepareStatement("select login,name,description from github.organization,github.search_organization where orgid=id and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String login = rs.getString(1);
	    String name = rs.getString(2);
	    String bio = rs.getString(3);
	    
	    logger.info("login: " + login + "\t" + name);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "GitHub", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/GitHub", '/'));

	    theDocument.add(new Field("uri", "http://github.com/"+login, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (name != null ) {
		theDocument.add(new Field("label", name, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", name, Field.Store.NO, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", login, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", login, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (bio != null) {
		theDocument.add(new Field("content", bio, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    paths.add(new CategoryPath("Entity/Organization/unknown", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\torganizations indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexGitHubRepositories(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing GitHub repositories...");
	PreparedStatement stmt = conn.prepareStatement("select id,name,full_name,description,language from github.repository,github.search_repository where id=rid and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    int ID = rs.getInt(1);
	    String name = rs.getString(2);
	    String fullName = rs.getString(3);
	    String description = rs.getString(4);
	    String language = rs.getString(5);
	    
	    logger.info("name: " + fullName);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "GitHub", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/GitHub", '/'));
	    if (language == null)
		paths.add(new CategoryPath("Entity/Repository/unknown", '/'));
	    else {
		theDocument.add(new Field("content", language, Field.Store.NO, Field.Index.ANALYZED));
		paths.add(new CategoryPath("Entity/Repository/"+language, '/'));
	    }

	    theDocument.add(new Field("uri", "http://github.com/"+fullName, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (name != null ) {
		theDocument.add(new Field("label", name, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", name, Field.Store.NO, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", name, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", fullName, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (description != null) {
		theDocument.add(new Field("content", description, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    
	    PreparedStatement readStmt = conn.prepareStatement("select readme from github.readme where id=?");
	    readStmt.setInt(1, ID);
	    ResultSet readRS = readStmt.executeQuery();
	    while (readRS.next()) {
		String readme = readRS.getString(1);
		if (readme != null)
		    theDocument.add(new Field("content", readme, Field.Store.NO, Field.Index.ANALYZED));		
	    }
	    readStmt.close();

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\trepositories indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexClinicalTrialOfficialContact(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing ClinicalTrials.gov official contacts...");
	PreparedStatement stmt = conn.prepareStatement("select last_name,role,affiliation,count(*) from clinical_trials.overall_official group by 1,2,3 order by 4 desc");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String name = rs.getString(1);
	    String title = rs.getString(2);
	    String site = rs.getString(3);
	    
	    logger.info("login: " + name + "\t" + site);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "ClinicalTrials.gov", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/ClinicalTrials.gov", '/'));

	    theDocument.add(new Field("uri", "http://ClinicalTrials.gov/"+name, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (name != null ) {
		theDocument.add(new Field("label", name+(title == null ? "" : (", "+title)), Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", name, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (title == null)
		paths.add(new CategoryPath("Entity/Person/unknown", '/'));
	    else {
		theDocument.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Entity/Person/"+title, '/'));
		} catch (Exception e) {
		    logger.error("error adding title facet", e);
		}
	    }
	    
	    if (site != null) {
		theDocument.add(new Field("site", site, Field.Store.YES, Field.Index.NOT_ANALYZED));
		theDocument.add(new Field("content", site, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Site/"+site, '/'));
		} catch (Exception e) {
		    logger.error("error adding site facet", e);
		}
	    } else {
		paths.add(new CategoryPath("Site/unknown", '/'));		
	    }

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\tusers indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexClinicalTrials(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing ClinicalTrials.gov trials...");
	PreparedStatement stmt = conn.prepareStatement("select nct_id,brief_title,official_title,overall_status,phase,study_type,condition from clinical_trials.clinical_study");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String nctID = rs.getString(1);
	    String briefTitle = rs.getString(2);
	    String title = rs.getString(3);
	    String status = rs.getString(4);
	    String phase = rs.getString(5);
	    String type = rs.getString(6);
	    String condition = rs.getString(7);
	    
	    logger.info("trial: " + nctID + "\t" + briefTitle);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "ClinicalTrials.gov", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/ClinicalTrials.gov", '/'));
	    paths.add(new CategoryPath("Entity/Clinical Trial", '/'));

	    theDocument.add(new Field("uri", "https://clinicaltrials.gov/ct2/show/"+nctID, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("content", nctID, Field.Store.NO, Field.Index.ANALYZED));
	    if (briefTitle != null ) {
		theDocument.add(new Field("label", briefTitle, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", briefTitle, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    if (title != null)  {
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    }
	    
	    if (status != null) {
		theDocument.add(new Field("content", status, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Status/"+status, '/'));
		} catch (Exception e) {
		    logger.error("error adding status facet", e);
		}
	    }

	    if (phase != null) {
		theDocument.add(new Field("content", phase, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Phase/"+phase, '/'));
		} catch (Exception e) {
		    logger.error("error adding phase facet", e);
		}
	    }

	    if (type != null) {
		theDocument.add(new Field("content", type, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Type/"+type, '/'));
		} catch (Exception e) {
		    logger.error("error adding type facet", e);
		}
	    }

	    if (condition != null) {
		theDocument.add(new Field("content", condition, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Condition/"+condition, '/'));
		} catch (Exception e) {
		    logger.error("error adding condition facet", e);
		}
	    }

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\ttrials indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexCTSAsearch(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing CTSAsearch...");
	PreparedStatement stmt = conn.prepareStatement("select distinct person_real.id,site,uri,first_name,last_name,title,ctsa,platform from vivo_aggregated.person_real,vivo.site where person_real.id=site.id and uri!~'pubid' order by site,last_name,first_name");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String site = rs.getString(2);
	    String uri = rs.getString(3);
	    String firstName = rs.getString(4);
	    String lastName = rs.getString(5);
	    String title = rs.getString(6);
	    boolean ctsa = rs.getBoolean(7);
	    String platform = rs.getString(8);
	    
	    logger.info("site: " + site + "\t" + lastName + ", " + firstName);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "CTSAsearch", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (platform != null) {
		paths.add(new CategoryPath("Source/CTSAsearch/" + platform, '/'));
	    } else {
		paths.add(new CategoryPath("Source/CTSAsearch/unknown", '/'));
	    }
	    
	    theDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    if (firstName != null ) {
		theDocument.add(new Field("label", lastName+", "+firstName+(title == null ? "" : (", "+title)), Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("first_name", firstName, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", firstName, Field.Store.NO, Field.Index.ANALYZED));
	    } else
		theDocument.add(new Field("label", lastName+(title == null ? "" : (", "+title)), Field.Store.YES, Field.Index.ANALYZED));
	    theDocument.add(new Field("content", lastName, Field.Store.NO, Field.Index.ANALYZED));
	    if (title == null)
		paths.add(new CategoryPath("Entity/Person/unknown", '/'));
	    else {
		theDocument.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
		try {
		    paths.add(new CategoryPath("Entity/Person/"+title, '/'));
		} catch (Exception e) {
		    logger.error("error adding title facet", e);
		}
	    }
	    
	    theDocument.add(new Field("site", site, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("content", site, Field.Store.NO, Field.Index.ANALYZED));
	    paths.add(new CategoryPath("Site/"+site, '/'));
	    if (ctsa)
		paths.add(new CategoryPath("CTSA/"+site, '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	    count++;
	}
	stmt.close();
	logger.info("\tusers indexed: " + count);
    }
    
    @SuppressWarnings("deprecation")
    static void indexNIHFOA(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing NIH FOAs...");
	PreparedStatement stmt = conn.prepareStatement("select id,title,purpose,primary_ic,doc_num,guide_link from NIH_FOA.guide_doc order by id");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    int ID = rs.getInt(1);
	    String title = rs.getString(2);
	    String purpose = rs.getString(3);
	    String primaryIC = rs.getString(4);
	    String docNum = rs.getString(5);
	    String uri = rs.getString(6);

	    logger.info("FOA: " + primaryIC + ", " + docNum + "\t" + title);

	    PreparedStatement contentStmt = conn.prepareStatement("select html from NIH_FOA.content where id = ?");
	    contentStmt.setInt(1, ID);
	    ResultSet crs = contentStmt.executeQuery();
	    while (crs.next()) {
		String content = crs.getString(1);
		Document theDocument = new Document();
		List<CategoryPath> paths = new ArrayList<CategoryPath>();
		
		theDocument.add(new Field("source", "NIH FOA", Field.Store.YES, Field.Index.NOT_ANALYZED));
		theDocument.add(new Field("uri", uri, Field.Store.YES, Field.Index.NOT_ANALYZED));
		theDocument.add(new Field("id", ID + "", Field.Store.YES, Field.Index.NOT_ANALYZED));

		theDocument.add(new Field("label", docNum+" - "+title, Field.Store.YES, Field.Index.ANALYZED));

		theDocument.add(new Field("purpose", title, Field.Store.NO, Field.Index.ANALYZED));
		theDocument.add(new Field("purpose", purpose, Field.Store.NO, Field.Index.ANALYZED));

		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
		theDocument.add(new Field("content", purpose, Field.Store.NO, Field.Index.ANALYZED));
		theDocument.add(new Field("content", content, Field.Store.NO, Field.Index.ANALYZED));
		
		paths.add(new CategoryPath("Source/NIH FOA", '/'));
		paths.add(new CategoryPath("Site/NIH/" + primaryIC, '/'));
		
		PreparedStatement activityStmt = conn.prepareStatement("select code from nih_foa.activity_code where id = ?");
		activityStmt.setInt(1, ID);
		ResultSet activityRS = activityStmt.executeQuery();
		while (activityRS.next()) {
		    paths.add(new CategoryPath("Entity/Funding Opportunity/" + activityRS.getString(1), '/'));
		}
		activityStmt.close();
		
		facetFields.addFields(theDocument, paths);
		indexWriter.addDocument(theDocument);
	    }
	    contentStmt.close();
	}
	stmt.close();
	logger.info("\tFOAs indexed: " + count);
    }

    @SuppressWarnings("deprecation")
    static void indexDataMed(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing DataMed datasets...");
	PreparedStatement stmt = conn.prepareStatement("select id,title,source,landing_page,description,creators,meshterms from datamed.dataset where landing_page is not null");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    String ID = rs.getString(1);
	    String title = rs.getString(2);
	    String source = rs.getString(3);
	    String landingPage = rs.getString(4);
	    String description = rs.getString(5);
	    String creators = rs.getString(6);
	    String meshterms = rs.getString(7);

	    logger.info("dataset: " + ID + "\t" + title);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();

	    theDocument.add(new Field("source", "DataMed", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("uri", landingPage, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("id", ID + "", Field.Store.YES, Field.Index.NOT_ANALYZED));

	    if (title == null) {
		theDocument.add(new Field("label", "DataMed "+ID, Field.Store.YES, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", title, Field.Store.YES, Field.Index.ANALYZED));		
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    }
		
	    if (description != null)
		theDocument.add(new Field("content", description, Field.Store.NO, Field.Index.ANALYZED));
	    if (creators != null)
		theDocument.add(new Field("content", creators, Field.Store.NO, Field.Index.ANALYZED));
	    if (meshterms != null)
		theDocument.add(new Field("content", meshterms, Field.Store.NO, Field.Index.ANALYZED));

	    if (source != null && source.length() > 1)
		paths.add(new CategoryPath("Source/DataMed/" + source, '/'));
	    else
		paths.add(new CategoryPath("Source/DataMed/unknown", '/'));
	    paths.add(new CategoryPath("Entity/Data Set", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	}
	stmt.close();
	logger.info("\t datasets indexed: " + count);
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
	Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost/loki", props);
	conn.setAutoCommit(false);
	return conn;
    }

    public static org.apache.jena.query.ResultSet getResultSet(String queryString) throws JspTagException {
	Query theClassQuery = QueryFactory.create(queryString, Syntax.syntaxARQ);
	QueryExecution theClassExecution = QueryExecutionFactory.sparqlService("https://alaska.dev.eagle-i.net/sparqler/sparql", theClassQuery);
	return theClassExecution.execSelect();
    }

}
