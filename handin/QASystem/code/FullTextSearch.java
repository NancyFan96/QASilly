/**
 * Created by serena on 16/11/29.
 */
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.nio.file.Path;

import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.swing.text.html.parser.DocumentParser;

public class FullTextSearch
{
    public static void main(String[] args) throws IOException
    {

        String indexDir = "/users/serena/desktop/index"; // indexPath
        String dataDir = "/users/serena/desktop/wiki_145"; // docsPath


        RAMDirectory directory = new RAMDirectory();
        final File dir = new File(dataDir);

        Analyzer analyzer = new IKAnalyzer(); // 使用中文分词器

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer); // 版本是坑!

        IndexWriter indexWriter = new IndexWriter(directory, config);
        //config.setMaxBufferedDocs(1000);  // 触发flush


        File[] files = dir.listFiles();
        System.out.println(files.length);

        for (int i = 0; i < files.length; i++)       //对约145万个file
        {
            FileInputStream fis;
            fis = new FileInputStream(files[i]);
            String s ="";
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();

            while( (s = br.readLine()) != null) {
                sb.append(s + "\n");
            }

            br.close();
            String str = sb.toString();
            Document document = new Document();
            //document.add(new Field("document_content",files[i].toString(), TextField.TYPE_STORED));
            document.add(new Field("document_content", str,TextField.TYPE_STORED));
        //document.add(new Field("document_content","马尔代夫第一大产业", TextField.TYPE_STORED));
            //System.out.println(document.get("document_content"));
        indexWriter.addDocument(document);
            if(i% 100 ==0)
            System.out.println(i);
        }

        indexWriter.commit();
        indexWriter.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(reader);






        FileInputStream fis2;
        fis2 = new FileInputStream("/Users/serena/desktop/200questions.txt");
        String s ="";
        BufferedReader br = new BufferedReader(new InputStreamReader(fis2, StandardCharsets.UTF_8));
        Analyzer analyzer2 = new IKAnalyzer(true); // 使用中文分词器
        Integer cnt = 1;
        while( (s = br.readLine()) != null)  // 对于每个问题
        {
            FileWriter fw2 = new FileWriter("/Users/serena/documents/workspace/QA/final_200_10/final_" + cnt.toString(),false);
            BufferedWriter bw2 = new BufferedWriter(fw2);

            String queryString = s;
            Query query = null;

            try {
                QueryParser qp = new QueryParser("document_content", analyzer2);
                query = qp.parse(queryString);
            } catch (ParseException e) {
            }

            TopScoreDocCollector collector = TopScoreDocCollector.create(10, true); // 每个问题前10个文档
            indexSearcher.search(query, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            //System.out.println("Found "+hits.length+" hits." );
            for (int i = 0; i < hits.length; i++)
            {
                int docID = hits[i].doc;
                Document d = indexSearcher.doc(docID);
                bw2.write(d.get("document_content"));
                //System.out.println((1) + ". " + "\t" + d.get("document_content"));
            }
            bw2.close();
            cnt++; // 用cnt来参与命名
        }

        reader.close();

    }
}
