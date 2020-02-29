package ie.tcd.quzair;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CranfieldQueries {

    private static String CRAN_DIR = "./cran/cran.qry";
    private static String INDEX_DIRECTORY = "./cran_index";

    private Analyzer analyzer;
    private Directory directory;
    private IndexSearcher isearcher;
    private DirectoryReader ireader;


    public CranfieldQueries() throws IOException
    {
        this.analyzer = new StandardAnalyzer();
        this.directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        this.ireader = DirectoryReader.open(directory);
        this.isearcher = new IndexSearcher(ireader);
    }

    void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    void setSimilarity(Similarity similarity) {
        isearcher.setSimilarity(similarity);
    }

    public Map<String, String> parseQueries() throws IOException {
        Map<String, String> queries = new HashMap<>();

        File cranFile = new File(CRAN_DIR);
        Boolean storeContent = false;
        List<String> allLinesList = new ArrayList<String>();
        StringBuilder queryBuilder = new StringBuilder();

        allLinesList = Files.readAllLines(cranFile.toPath(), Charset.defaultCharset());
        int docId = 0;
        String newId;

        //Go through Each Line
        for (String line : allLinesList) {
            String[] res = line.split(" ");

            //Line is an Index
            if (res[0].contains(".I")) {
                queries.put(String.valueOf(docId++),queryBuilder.toString().replace("?",""));
                storeContent = false;
                queryBuilder = new StringBuilder();
            } else if (res[0].contains(".W")) {
                storeContent = true;
                continue;
            }
            if (storeContent) queryBuilder.append(line);
        }
        queries.put(String.valueOf(docId++),queryBuilder.toString().replace("?",""));
        queries.remove("0");

        return queries;
    }

    public void testQueries() throws IOException, ParseException {
        Map<String, String> queries = parseQueries();
        ArrayList<ArrayList<String[]>> queryResults = new ArrayList<>();
        File file = new File(getResultFilePath());
        BufferedWriter output = null;
        output = new BufferedWriter(new FileWriter(file));

        for (Map.Entry<String, String> entry : queries.entrySet()) {
            ArrayList<String[]> info = search(entry.getValue(),entry.getKey(),50);
            queryResults.add(info);
        }

        for(ArrayList<String[]> res: queryResults) {

            for(String[] info: res) {
                for(String temp: info) {
                    if(!temp.contains("STANDARD")) output.write(temp + " ");
                    else output.write(temp);
                }
                output.newLine();
            }
        }

        output.close();
        System.out.println("Saved DocRank results at " + getResultFilePath());

    }

    public ArrayList<String[]> search(String queryStr, String queryId, int hitsCount) throws ParseException, IOException {
        String fields[] = new String[]{CranfieldModel.TITLE, CranfieldModel.AUTHOR, CranfieldModel.SOURCE, CranfieldModel.CONTENT};
        QueryParser parser = new MultiFieldQueryParser(fields, this.analyzer);

        Query query = parser.parse(queryStr);
        ScoreDoc[] hits = this.isearcher.search(query, hitsCount).scoreDocs;
        ArrayList<Integer> docIds = new ArrayList<>();
        ArrayList<String[]> docRankList = new ArrayList<>();
        int ranking = 1;

        for (ScoreDoc hit : hits) {
            Document doc = this.isearcher.doc(hit.doc);
            int id = Integer.parseInt(doc.get(CranfieldModel.ID));
            docIds.add(id);
            String[] info = {
                    queryId,
                    "0",
                    String.valueOf(id),
                    Integer.toString(ranking++),
                    Float.toString(hit.score),
                    "STANDARD"
            };
            docRankList.add(info);
        }
        return docRankList;
    }

    public String getResultFilePath() {

        String workingDir = System.getProperty("user.dir");
        String systemFilePath = workingDir.concat("/systemFile.txt");

        // Different Retrieval Models
        String similarityDirPath = workingDir.concat("/simiarlityFiles");
        if(this.analyzer.getClass() != StandardAnalyzer.class) similarityDirPath = similarityDirPath.concat("/custom");
        new File(similarityDirPath).mkdirs();
        if(this.isearcher.getSimilarity().getClass() == BM25Similarity.class) {
            systemFilePath = (similarityDirPath.concat("/bm25_systemFile.txt"));
        } else if(this.isearcher.getSimilarity().getClass() == BooleanSimilarity.class) {
            systemFilePath = (similarityDirPath.concat("/boolean_systemFile.txt"));
        } else if(this.isearcher.getSimilarity().getClass() == ClassicSimilarity.class) {
            systemFilePath = (similarityDirPath.concat("/tfidf_systemFile.txt"));
        }



        return systemFilePath;

    }

    public void printQueries(Map<String,String> queries) {
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue()+"\n\n");
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        CranfieldQueries cq = new CranfieldQueries();
        cq.testQueries();
    }
}
