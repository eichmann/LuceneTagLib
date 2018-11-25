package edu.uiowa.lucene.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

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
import org.apache.lucene.facet.params.FacetIndexingParams;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter.OrdinalMap;
import org.apache.lucene.facet.util.TaxonomyMergeUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class FacetIndexer {
    static Logger logger = Logger.getLogger(FacetIndexer.class);
    static private Directory indexDir = null;
    static private Directory taxoDir = null;
    static Connection wintermuteConn = null;
    static Connection deepConn = null;
    static String pathPrefix = "/usr/local/CD2H/lucene/";
    static String[] sites = {
	    			pathPrefix + "github",
	    			pathPrefix + "nlighten",
	    			pathPrefix + "clinical_trials",
	    			pathPrefix + "nih_foa",
	    			pathPrefix + "datamed",
	    			pathPrefix + "datacite",
	    			pathPrefix + "diamond",
	    			"/usr/local/RAID/CTSAsearch/lucene/ctsasearch"
	    			};
    
    static Hashtable<String, Vector<String>> gitHubCategoryCache = new Hashtable<String, Vector<String>>();

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
        PropertyConfigurator.configure(args[0]);
	wintermuteConn = getConnection("wintermute.slis.uiowa.edu");
	deepConn = getConnection("deep-thought.slis.uiowa.edu");

	indexGitHub();
	indexNLighten();
	indexClinicalTrials();
	indexNIHFOA();
	indexDataMed();
	indexDataCite();
	indexDIAMOND();
    }
    
    public static void mergeIndices(String[] requests, String sitePathPrefix, String targetPath) throws SQLException, CorruptIndexException, IOException {
	IndexWriterConfig config = new IndexWriterConfig(org.apache.lucene.util.Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43));
	config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	config.setRAMBufferSizeMB(500);
	IndexWriter theWriter = new IndexWriter(FSDirectory.open(new File(targetPath)), config);
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(FSDirectory.open(new File(targetPath + "_tax")));
	OrdinalMap map = new org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter.MemoryOrdinalMap();
	FacetIndexingParams params = new FacetIndexingParams();
	
	logger.info("sites: " + requests);
	for (String site : requests) {
	    logger.info("merging " + site + "...");
	    Directory index = FSDirectory.open(new File(site));
	    Directory index_tax = FSDirectory.open(new File(site + "_tax"));
	    TaxonomyMergeUtils.merge(index, index_tax, map, theWriter, taxoWriter, params);
	    index_tax.close();
	    index.close();
	}

	taxoWriter.close();
	theWriter.close();
	logger.info("done");
    }

    static void indexGitHub() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "github"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "github_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	populateGitHubCategoryCache();
	indexGitHubUsers(indexWriter, facetFields);
	indexGitHubOrganizations(indexWriter, facetFields);
	indexGitHubRepositories(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexNLighten() throws IOException, JspTagException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "nlighten"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "nlighten_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexNLightenUsers(indexWriter, facetFields);
	indexNLightenOrganizations(indexWriter, facetFields);
	indexNLightenResource(indexWriter, facetFields, "http://schema.org/Movie", "Movie");
	indexNLightenResource(indexWriter, facetFields, "http://schema.org/Course", "Course");
	indexNLightenResource(indexWriter, facetFields, "http://vivoweb.org/ontology/core#CaseStudy", "Case Study");
	indexNLightenResource(indexWriter, facetFields, "http://purl.org/n-lighten/NLN_0000041", "Resource Material");

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexClinicalTrials() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "clinical_trials"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "clinical_trials_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexClinicalTrialOfficialContact(indexWriter, facetFields);
	indexClinicalTrials(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexNIHFOA() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "nih_foa"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "nih_foa_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexNIHFOA(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexDataMed() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "datamed"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "datamed_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexDataMed(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexDataCite() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "datacite"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "datacite_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexDataCite(indexWriter, facetFields);

	taxoWriter.close();
	indexWriter.close();
    }
    
    static void indexDIAMOND() throws IOException, SQLException {
	Directory indexDir = FSDirectory.open(new File(pathPrefix + "diamond"));
	Directory taxoDir = FSDirectory.open(new File(pathPrefix + "diamond_tax"));

	IndexWriter indexWriter = new IndexWriter(indexDir,
		new IndexWriterConfig(Version.LUCENE_43, new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_43)));

	// Writes facet ords to a separate directory from the main index
	DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

	// Reused across documents, to add the necessary facet fields
	FacetFields facetFields = new FacetFields(taxoWriter);
	
	indexDIAMONDAssessments(indexWriter, facetFields);
	indexDIAMONDTrainingMaterials(indexWriter, facetFields);

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
	    
	    logger.debug("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", "https://alaska.dev.eagle-i.net/institution/#inst?uri="+subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
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
	    
	    logger.debug("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", "https://alaska.dev.eagle-i.net/institution/#inst?uri="+subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
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
	    
	    logger.debug("uri: " + subjectURI + "\t" + label);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();
	    
	    theDocument.add(new Field("source", "N-Lighten", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    paths.add(new CategoryPath("Source/N-Lighten", '/'));

	    theDocument.add(new Field("uri", "https://alaska.dev.eagle-i.net/institution/#inst?uri="+subjectURI, Field.Store.YES, Field.Index.NOT_ANALYZED));
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
	PreparedStatement stmt = wintermuteConn.prepareStatement("select login,name,bio from github.user,github.search_user where uid=id and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String login = rs.getString(1);
	    String name = rs.getString(2);
	    String bio = rs.getString(3);
	    
	    logger.debug("login: " + login + "\t" + name);

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
	PreparedStatement stmt = wintermuteConn.prepareStatement("select login,name,description from github.organization,github.search_organization where orgid=id and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String login = rs.getString(1);
	    String name = rs.getString(2);
	    String bio = rs.getString(3);
	    
	    logger.debug("login: " + login + "\t" + name);

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
	PreparedStatement stmt = wintermuteConn.prepareStatement("select id,name,full_name,description,language from github.repository,github.search_repository where id=rid and relevant");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    int ID = rs.getInt(1);
	    String name = rs.getString(2);
	    String fullName = rs.getString(3);
	    String description = rs.getString(4);
	    String language = rs.getString(5);
	    
	    logger.debug("name: " + fullName);

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
	    
	    if (gitHubCategoryCache.containsKey(fullName))
		for (String categoryPath : gitHubCategoryCache.get(fullName)) {
		    paths.add(new CategoryPath(categoryPath, '/'));
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
	    
	    PreparedStatement readStmt = wintermuteConn.prepareStatement("select readme from github.readme where id=?");
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
    
    static void populateGitHubCategoryCache() throws SQLException {
	logger.info("loading GitHub facet annotations...");
	logger.info("\teducational material...");
	PreparedStatement stmt = wintermuteConn.prepareStatement("select full_name from github.repository where name~'^BDK[0-9]' order by name");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String name = rs.getString(1);
	    Vector<String> categories = gitHubCategoryCache.get(name);
	    if (categories == null) {
		categories = new Vector<String>();
		gitHubCategoryCache.put(name, categories);
	    }
	    categories.add("Entity/Educational Resource/unknown");
	}
	stmt.close();
    }
    
    @SuppressWarnings("deprecation")
    static void indexClinicalTrialOfficialContact(IndexWriter indexWriter, FacetFields facetFields) throws SQLException, IOException {
	int count = 0;
	logger.info("indexing ClinicalTrials.gov official contacts...");
	PreparedStatement stmt = wintermuteConn.prepareStatement("select last_name,role,affiliation,count(*) from clinical_trials.overall_official group by 1,2,3 order by 4 desc");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String name = rs.getString(1);
	    String title = rs.getString(2);
	    String site = rs.getString(3);
	    
	    logger.debug("login: " + name + "\t" + site);

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
		    paths.add(new CategoryPath("Site/"+site.replaceAll("/", "_"), '/'));
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
	PreparedStatement stmt = wintermuteConn.prepareStatement("select nct_id,brief_title,official_title,overall_status,phase,study_type,condition from clinical_trials.clinical_study");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String nctID = rs.getString(1);
	    String briefTitle = rs.getString(2);
	    String title = rs.getString(3);
	    String status = rs.getString(4);
	    String phase = rs.getString(5);
	    String type = rs.getString(6);
	    String condition = rs.getString(7);
	    
	    logger.debug("trial: " + nctID + "\t" + briefTitle);

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
	PreparedStatement stmt = deepConn.prepareStatement("select distinct person_real.id,site,uri,first_name,last_name,title,ctsa,platform from vivo_aggregated.person_real,vivo.site where person_real.id=site.id and uri!~'pubid' order by site,last_name,first_name");
	ResultSet rs = stmt.executeQuery();
	while (rs.next()) {
	    String site = rs.getString(2);
	    String uri = rs.getString(3);
	    String firstName = rs.getString(4);
	    String lastName = rs.getString(5);
	    String title = rs.getString(6);
	    boolean ctsa = rs.getBoolean(7);
	    String platform = rs.getString(8);
	    
	    logger.debug("site: " + site + "\t" + lastName + ", " + firstName);

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
		    paths.add(new CategoryPath("Entity/Person/"+title.replaceAll("/", "_"), '/'));
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
	PreparedStatement stmt = deepConn.prepareStatement("select id,title,purpose,primary_ic,doc_num,guide_link from NIH_FOA.guide_doc order by id");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    int ID = rs.getInt(1);
	    String title = rs.getString(2);
	    String purpose = rs.getString(3);
	    String primaryIC = rs.getString(4);
	    String docNum = rs.getString(5);
	    String uri = rs.getString(6);

	    logger.debug("FOA: " + primaryIC + ", " + docNum + "\t" + title);

	    PreparedStatement contentStmt = deepConn.prepareStatement("select html from NIH_FOA.content where id = ?");
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
		
		PreparedStatement activityStmt = deepConn.prepareStatement("select code from nih_foa.activity_code where id = ?");
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
	PreparedStatement stmt = wintermuteConn.prepareStatement("select id,title,source,landing_page,description,creators,meshterms from datamed.dataset where landing_page is not null");
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

	    logger.debug("dataset: " + ID + "\t" + title);

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
	logger.info("\tdatasets indexed: " + count);
    }

/* doi
 * title
 * container title is subsource
 * description
 * subtype - entity subtype - 335 values
 * resource_type_id - alternative entity subtype - 15 values
*/    @SuppressWarnings("deprecation")
    static void indexDataCite(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing DataCite datasets...");
	PreparedStatement stmt = wintermuteConn.prepareStatement("select doi,title,container_title,description,resource_type_id from datacite.dataset");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    String doi = rs.getString(1);
	    String title = rs.getString(2);
	    String source = rs.getString(3);
	    String description = rs.getString(4);
	    String subtype = rs.getString(5);

	    logger.debug("dataset: " + doi + "\t" + title);
	    if (count % 1000000 == 0)
		logger.info("\tdataset count: " + count + "...");

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();

	    theDocument.add(new Field("source", "DataCite", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("uri", "https://doi.org/"+doi, Field.Store.YES, Field.Index.NOT_ANALYZED));

	    if (title == null) {
		theDocument.add(new Field("label", "DataCite "+doi, Field.Store.YES, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", title, Field.Store.YES, Field.Index.ANALYZED));		
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    }
		
	    if (description != null)
		theDocument.add(new Field("content", description, Field.Store.NO, Field.Index.ANALYZED));

	    if (source != null && source.length() > 1)
		try {
		    paths.add(new CategoryPath("Source/DataCite/" + source.replaceAll("/", "_"), '/'));
		} catch (Exception e) {
		    logger.error("error adding source facet", e);
		}
	    else
		paths.add(new CategoryPath("Source/DataCite/unknown", '/'));
	    
	    if (subtype != null && subtype.length() > 1)
		paths.add(new CategoryPath("Entity/Data Set/" + subtype, '/'));
	    else
		paths.add(new CategoryPath("Entity/Data Set/unknown", '/'));

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	}
	stmt.close();
	logger.info("\tdatasets indexed: " + count);
    }

@SuppressWarnings("deprecation")
static void indexDIAMONDAssessments(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing DIAMOND assessments...");
	PreparedStatement stmt = wintermuteConn.prepareStatement("select id,title,assessment_methods,subject_area,bp_categories,learning_level from diamond.assessment");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    int ID = rs.getInt(1);
	    String title = rs.getString(2);
	    String assessment_methods = rs.getString(3);
	    String subject_area = rs.getString(4);
	    String bp_categories = rs.getString(5);
	    String learning_level = rs.getString(6);

	    logger.debug("assessment: " + ID + "\t" + title);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();

	    theDocument.add(new Field("source", "DIAMOND", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("uri", "https://diamondportal.org/assessments/"+ID, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("id", ID + "", Field.Store.YES, Field.Index.NOT_ANALYZED));

	    if (title == null) {
		theDocument.add(new Field("label", "DIAMOND "+ID, Field.Store.YES, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", title, Field.Store.YES, Field.Index.ANALYZED));		
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    }
		
	    if (assessment_methods != null)
		theDocument.add(new Field("content", assessment_methods, Field.Store.NO, Field.Index.ANALYZED));
	    if (subject_area != null)
		theDocument.add(new Field("content", subject_area, Field.Store.NO, Field.Index.ANALYZED));
	    if (bp_categories != null)
		theDocument.add(new Field("content", bp_categories, Field.Store.NO, Field.Index.ANALYZED));
	    if (learning_level != null)
		theDocument.add(new Field("content", learning_level, Field.Store.NO, Field.Index.ANALYZED));

	    paths.add(new CategoryPath("Source/DIAMOND", '/'));
	    paths.add(new CategoryPath("Entity/Educational Resource/Assessment", '/'));
	    paths.add(new CategoryPath("Learning Level/"+learning_level, '/'));
	    for (String assessment : assessment_methods.split(",")) {
		paths.add(new CategoryPath("Assessment Method/"+assessment.trim(), '/'));
	    }
	    
	    PreparedStatement domainStmt = wintermuteConn.prepareStatement("select domain from diamond.competency_domain where type='assessments' and id=?");
	    domainStmt.setInt(1, ID);
	    ResultSet domainRS = domainStmt.executeQuery();
	    while (domainRS.next()) {
		paths.add(new CategoryPath("Competency Domain/"+domainRS.getString(1), '/'));
	    }

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	}
	stmt.close();
	logger.info("\tassessments indexed: " + count);
}

@SuppressWarnings("deprecation")
static void indexDIAMONDTrainingMaterials(IndexWriter indexWriter, FacetFields facetFields) throws IOException, SQLException {
	int count = 0;
	logger.info("indexing DIAMOND training materials...");
	PreparedStatement stmt = wintermuteConn.prepareStatement("select id,title,abstract,keywords,subject_area,learning_objectives,delivery_method,target_learners,learning_level from diamond.training_material");
	ResultSet rs = stmt.executeQuery();

	while (rs.next()) {
	    count++;
	    int ID = rs.getInt(1);
	    String title = rs.getString(2);
	    String abstr = rs.getString(3);
	    String keywords = rs.getString(4);
	    String subject_area = rs.getString(5);
	    String learning_objectives = rs.getString(6);
	    String delivery_method = rs.getString(7);
	    String target_learners = rs.getString(8);
	    String learning_level = rs.getString(9);

	    logger.debug("training material: " + ID + "\t" + title);

	    Document theDocument = new Document();
	    List<CategoryPath> paths = new ArrayList<CategoryPath>();

	    theDocument.add(new Field("source", "DIAMOND", Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("uri", "https://diamondportal.org/trainings/"+ID, Field.Store.YES, Field.Index.NOT_ANALYZED));
	    theDocument.add(new Field("id", ID + "", Field.Store.YES, Field.Index.NOT_ANALYZED));

	    if (title == null) {
		theDocument.add(new Field("label", "DIAMOND "+ID, Field.Store.YES, Field.Index.ANALYZED));
	    } else {
		theDocument.add(new Field("label", title, Field.Store.YES, Field.Index.ANALYZED));		
		theDocument.add(new Field("content", title, Field.Store.NO, Field.Index.ANALYZED));
	    }
		
	    if (abstr != null)
		theDocument.add(new Field("content", abstr, Field.Store.NO, Field.Index.ANALYZED));
	    if (keywords != null)
		theDocument.add(new Field("content", keywords, Field.Store.NO, Field.Index.ANALYZED));
	    if (subject_area != null)
		theDocument.add(new Field("content", subject_area, Field.Store.NO, Field.Index.ANALYZED));
	    if (learning_objectives != null)
		theDocument.add(new Field("content", learning_objectives, Field.Store.NO, Field.Index.ANALYZED));
	    if (delivery_method != null)
		theDocument.add(new Field("content", delivery_method, Field.Store.NO, Field.Index.ANALYZED));
	    if (target_learners != null)
		theDocument.add(new Field("content", target_learners, Field.Store.NO, Field.Index.ANALYZED));
	    if (learning_level != null)
		theDocument.add(new Field("content", learning_level, Field.Store.NO, Field.Index.ANALYZED));

	    paths.add(new CategoryPath("Source/DIAMOND", '/'));
	    paths.add(new CategoryPath("Entity/Educational Resource/Training Material", '/'));
	    paths.add(new CategoryPath("Delivery Method/"+delivery_method, '/'));
	    paths.add(new CategoryPath("Learning Level/"+learning_level, '/'));
	    if (keywords != null)
		for (String keyword : keywords.split(",")) {
		    paths.add(new CategoryPath("Keyword/"+keyword.trim(), '/'));
		}
	    if (target_learners != null)
		for (String learner : target_learners.split(";")) {
		    paths.add(new CategoryPath("Entity/Person/"+learner.trim(), '/'));
		}
	    
	    PreparedStatement domainStmt = wintermuteConn.prepareStatement("select domain from diamond.competency_domain where type='trainings' and id=?");
	    domainStmt.setInt(1, ID);
	    ResultSet domainRS = domainStmt.executeQuery();
	    while (domainRS.next()) {
		paths.add(new CategoryPath("Competency Domain/"+domainRS.getString(1), '/'));
	    }

	    facetFields.addFields(theDocument, paths);
	    indexWriter.addDocument(theDocument);
	}
	stmt.close();
	logger.info("\ttraining materials indexed: " + count);
}

    public static Connection getConnection(String host) throws SQLException, ClassNotFoundException {
	Class.forName("org.postgresql.Driver");
	Properties props = new Properties();
	props.setProperty("user", "eichmann");
	props.setProperty("password", "translational");
//	if (use_ssl.equals("true")) {
//	    props.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
//	    props.setProperty("ssl", "true");
//	}
	Connection conn = DriverManager.getConnection("jdbc:postgresql://"+host+"/loki", props);
	conn.setAutoCommit(false);
	return conn;
    }

    public static org.apache.jena.query.ResultSet getResultSet(String queryString) throws JspTagException {
	Query theClassQuery = QueryFactory.create(queryString, Syntax.syntaxARQ);
	QueryExecution theClassExecution = QueryExecutionFactory.sparqlService("https://alaska.dev.eagle-i.net/sparqler/sparql", theClassQuery);
	return theClassExecution.execSelect();
    }

}
