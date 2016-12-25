/**
 * Created by serena on 16/11/24.
 */
import com.hankcs.hanlp.HanLP;
import java.util.List;

import java.io.*;


public class HanLP_ {
    public static void main(String[] args) throws IOException{

        FileWriter fw = new FileWriter("/users/serena/desktop/key_word_full_text", true);
        BufferedWriter bw = new BufferedWriter(fw);

            List<String> keywordList;
            File file = new File("/users/serena/desktop/wiki_after");
            InputStream in = new FileInputStream(file);
            InputStreamReader inreader = new InputStreamReader(in, "utf8");
            BufferedReader br = new BufferedReader(inreader);
            //System.out.println(j);
            //ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("/users/serena/desktop/key_word"));

            //把要写入的字符串都按照要写入文件的编码方式生成，再写入

            //bw.write(str2);
            String str1 = null;
            //int flag = 0;
            while ((str1 = br.readLine()) != null )
            {
                    keywordList = HanLP.extractKeyword(str1, 5); // 每句话提取5个关键词

                    for (int i = 0; i < keywordList.size(); i++)
                    {
                        String str2 = new String(keywordList.get(i).getBytes("utf8"), "utf8");
                        //System.out.println(str2);
                        bw.write(str2 + "\t");
                    }

            }
            bw.write("\n");
            bw.flush();
            bw.close();
    }
}

