package ie.tcd.quzair;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {

        // Different Similarity Algorithms to Test
        Similarity bm25Similairty= new BM25Similarity();
        Similarity booleanSimilarity = new BooleanSimilarity();
        Similarity tfidfSimilarity = new ClassicSimilarity();

        //BuildIndex with Standard Analyzer
        CranFileParser cnParser = new CranFileParser();
        cnParser.buildIndex();

        //Create Search DocRanks
        CranfieldQueries cq = new CranfieldQueries();

        System.out.println("With BM25 Similarity");
        cq.setSimilarity(bm25Similairty);
        cq.testQueries();

        System.out.println("With Boolean Similarity");
        cq.setSimilarity(booleanSimilarity);
        cq.testQueries();

        System.out.println("With TFIDF Similarity");
        cq.setSimilarity(tfidfSimilarity);
        cq.testQueries();



        //BuildIndex with Custom Analyzer
        cnParser = new CranFileParser(new CustomAnalyzer());
        cnParser.setAnalyzer(new CustomAnalyzer());
        cnParser.buildIndex();

        //Create Search DocRanks
        cq = new CranfieldQueries();

        System.out.println("With BM25 Similarity Custom");
        cq.setSimilarity(bm25Similairty);
        cq.setAnalyzer(new CustomAnalyzer());
        cq.testQueries();

        System.out.println("With Boolean Similarity Custom");
        cq.setSimilarity(booleanSimilarity);
        cq.setAnalyzer(new CustomAnalyzer());
        cq.testQueries();

        System.out.println("With TFIDF Similarity Custom");
        cq.setSimilarity(tfidfSimilarity);
        cq.setAnalyzer(new CustomAnalyzer());
        cq.testQueries();
    }
}
