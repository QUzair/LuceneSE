package ie.tcd.quzair;

import java.io.IOException;

import java.nio.file.Paths;

import org.apache.lucene.document.Document;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;

public class PersonalQueries
{

    // the location of the search index
    private static String INDEX_DIRECTORY = "./cran_index";

    // Limit the number of search results we get
    private static int MAX_RESULTS = 10;

    public static void main(String[] args) throws IOException
    {
        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // builder class for creating our query
        BooleanQuery.Builder query = new BooleanQuery.Builder();

        // Some words that we want to find and the field in which we expect
        // to find them
        Query term1 = new TermQuery(new Term("content", "flow"));
        Query term2 = new TermQuery(new Term("content", "viscous"));
        Query term3 = new TermQuery(new Term("content", "criticism"));

        // construct our query using basic boolean operations.
        query.add(new BooleanClause(term1, BooleanClause.Occur.SHOULD	));   // AND
        query.add(new BooleanClause(term2, BooleanClause.Occur.MUST));     // OR
        query.add(new BooleanClause(term3, BooleanClause.Occur.SHOULD)); // NOT

        // Get the set of results from the searcher
        ScoreDoc[] hits = isearcher.search(query.build(), MAX_RESULTS).scoreDocs;

        // Print the results
        System.out.println("Documents: " + hits.length);
        for (int i = 0; i < hits.length; i++)
        {
            Document hitDoc = isearcher.doc(hits[i].doc);
            System.out.println(i + ") " + hitDoc.get("docId") + " " + hits[i].score);
        }

        // close everything we used
        ireader.close();
        directory.close();
    }
}
