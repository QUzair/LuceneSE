package ie.tcd.quzair;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CranFileParser {

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "./cran_index";
    private static String CRAN_DIR = "./cran/cran.all.1400";

//    private static String CRAN_DIR = "./cran/crantest";


    private Analyzer analyzer;
    private Directory directory;
    private IndexWriter iwriter;

    Analyzer getAnalyzer() {
        return analyzer;
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public CranFileParser() throws IOException {
        //Set Up Index Directory and Default Analyzer
        this.analyzer = new StandardAnalyzer();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        //Create Index Writer
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        this.iwriter = new IndexWriter(directory, config);
    }

    public CranFileParser(Analyzer newAnalyzer) throws IOException {
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        //Create Index Writer
        IndexWriterConfig config = new IndexWriterConfig(newAnalyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        this.iwriter = new IndexWriter(directory, config);
    }

    public void addDocToIndex(String docId, String title, String author, String biblio, String content) throws IOException {

        // Create a new document
        Document doc = new Document();
        doc.add(new StringField(CranfieldModel.ID, docId, Field.Store.YES));
        doc.add(new TextField(CranfieldModel.TITLE, title, Field.Store.YES));
        doc.add(new TextField(CranfieldModel.AUTHOR, author, Field.Store.YES));
        doc.add(new TextField(CranfieldModel.SOURCE, biblio, Field.Store.YES));
        doc.add(new TextField(CranfieldModel.CONTENT, content, Field.Store.YES));

        this.iwriter.addDocument(doc);
    }

    public static void printDoc(String docId, StringBuilder title, StringBuilder author, StringBuilder biblio, StringBuilder content) {
        System.out.println("id: " + docId);
        System.out.println("title: " + title);
        System.out.println("author: " + author);
        System.out.println("biblio: " + biblio);
        System.out.println("content: " + content);
    }

    public void buildIndex() throws IOException {

        File cranFile = new File(CRAN_DIR);

        Boolean storeTitle = false, storeAuthor = false, storeBiblio = false, storeContent = false;

        List<String> allLinesList = new ArrayList<String>();

        StringBuilder titlebuilder = new StringBuilder();
        StringBuilder authorBuilder = new StringBuilder();
        StringBuilder biblioBuilder = new StringBuilder();
        StringBuilder contentBuilder = new StringBuilder();

        String docId = "0";
        String newId;

        allLinesList = Files.readAllLines(cranFile.toPath(), Charset.defaultCharset());

        //Go through Each Line
        for (String line : allLinesList) {
            String[] res = line.split(" ");
            //Line is an Index
            if (res[0].contains(".I")) {
                //New Doc
                newId = res[1];

                if (docId != "0") {
//                    printDoc(docId, titlebuilder, authorBuilder, biblioBuilder, contentBuilder);
                    //Add New Doc to Index
                    addDocToIndex(docId, titlebuilder.toString(), authorBuilder.toString(), biblioBuilder.toString(), contentBuilder.toString());
                }

                //Reset String Builders and assign new DocID
                storeTitle = false;
                storeAuthor = false;
                storeBiblio = false;
                storeContent = false;
                titlebuilder = new StringBuilder();
                authorBuilder = new StringBuilder();
                biblioBuilder = new StringBuilder();
                contentBuilder = new StringBuilder();
                docId = newId;

                //New DocId
            } else if (res[0].contains(".T")) {
                storeTitle = true;
                continue;
            } else if (res[0].contains(".A")) {
                storeAuthor = true;
                storeTitle = false;
                continue;
            } else if (res[0].contains(".B")) {
                storeBiblio = true;
                storeAuthor = false;
                continue;
            } else if (res[0].contains(".W")) {
                storeContent = true;
                storeBiblio = false;
                continue;
            }

            if (storeTitle) titlebuilder.append(line);
            else if (storeAuthor) authorBuilder.append(line);
            else if (storeBiblio) biblioBuilder.append(line);
            else if (storeContent) contentBuilder.append(line);
        }
        addDocToIndex(docId, titlebuilder.toString(), authorBuilder.toString(), biblioBuilder.toString(), contentBuilder.toString());


        // Commit everything and close
        this.iwriter.close();
        this.directory.close();
    }

    public static void main(String[] args) throws IOException {

        CranFileParser cnParser = new CranFileParser();
        cnParser.buildIndex();
    }
}

