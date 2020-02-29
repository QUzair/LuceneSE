package ie.tcd.quzair;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CustomAnalyzer extends Analyzer {


    public List<String> getStopWords() throws IOException {
        File cranFile = new File("./stopWords.txt");
        List<String> allLinesList = new ArrayList<String>();
        List<String> resList = new ArrayList<String>();
        allLinesList = Files.readAllLines(cranFile.toPath(), Charset.defaultCharset());

        for(String line: allLinesList) {
            String[] res = line.split(" ");
            for (String r:res) {
                resList.add(r);
            }
        }

        return resList;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new ClassicTokenizer();

        //Creating New Token Stream
        TokenStream tokenStream = new LowerCaseFilter(source);

        //Adding Filters
        tokenStream = new EnglishPossessiveFilter(tokenStream);
        tokenStream = new PorterStemFilter(tokenStream);
        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        tokenStream = new KStemFilter(tokenStream);


        CharArraySet newStopSet = null;
        try {
            newStopSet = StopFilter.makeStopSet(getStopWords());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tokenStream = new StopFilter(tokenStream, newStopSet);
        return new TokenStreamComponents(source, tokenStream);
    }


}
