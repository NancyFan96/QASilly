import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.seg.common.Term;

import java.io.*;
import java.util.List;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileInputStream;
/**
 * Created by serena on 16/11/26.
 */

// 目的:根据问题确定答案的类型

public class AnswerTypeInfer {
    public static void main(String args[]) throws IOException{
        Integer question_id = 1;
        File file = new File("/users/serena/desktop/questions1.txt");
        InputStream in = new FileInputStream(file);
        InputStreamReader inreader = new InputStreamReader(in, "utf8");
        BufferedReader br = new BufferedReader(inreader);

        FileWriter fw = new FileWriter("/users/serena/desktop/question_tag_7000", false);
        BufferedWriter bw = new BufferedWriter(fw);

        FileWriter fw2 = new FileWriter("/users/serena/desktop/answer_type_7000",false);
        BufferedWriter bw2 = new BufferedWriter(fw2);

        String str1 = null;
        List<Term>termList;
        Integer cnt = 1;
        while((str1 = br.readLine())!= null) {
            System.out.println("cnt = " + cnt.toString());
            cnt++;
            System.out.println(question_id.toString());
            bw.write(question_id.toString());
            bw2.write(question_id.toString());
            bw.write("\t");
            bw2.write("\t");
            question_id++;

            termList = NLPTokenizer.segment(str1);
            System.out.println(termList);
            //os.writeObject(keywordList);
            int found = 0;
            for (int i = 0; i < termList.size(); i++) {
                //String str2 = new String(termList.get(i));
                //System.out.println(termList.get(i));
                bw.write(termList.get(i) + "\t");
                if (found == 0) {
                    if ((termList.get(i)).toString().indexOf("哪里") != -1 ||
                            (termList.get(i)).toString().indexOf("地区") != -1 ||
                            (termList.get(i)).toString().indexOf("地点") != -1 ){
                        found = 1;
                        bw2.write("ns");
                    }
                    if(i < termList.size() - 1){
                        if (((termList.get(i)).toString().indexOf("哪个") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("国家") != -1) ||
                            ((termList.get(i)).toString().indexOf("哪个") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("城市") != -1) ||
                    ((termList.get(i)).toString().indexOf("大洲") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("是") != -1) ||
                            ((termList.get(i)).toString().indexOf("国籍") != -1 &&
                                (termList.get(i+1)).toString().indexOf("是") != -1) ||
                            ((termList.get(i)).toString().indexOf("国家") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("是") != -1) ||
                            ((termList.get(i)).toString().indexOf("哪个") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("城市") != -1) ||
                            ((termList.get(i)).toString().indexOf("国家") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("是") != -1) ||
                                ((termList.get(i)).toString().indexOf("什么") != -1 &&
                                        (termList.get(i+1)).toString().indexOf("楼上") != -1) ||
                                ((termList.get(i)).toString().indexOf("哪个") != -1 &&
                                        (termList.get(i+1)).toString().indexOf("大洋") != -1)) {
                        found = 1;
                        bw2.write("ns");   // 地名
                    }
                    }
                    if(i < termList.size() - 2){
                        if (((termList.get(i)).toString().indexOf("哪") != -1 &&
                                (termList.get(i+2)).toString().indexOf("城市") != -1) ||
                                (((termList.get(i)).toString().indexOf("哪") != -1 &&
                                        (termList.get(i+2)).toString().indexOf("国家") != -1))){
                            found = 1;
                            bw2.write("ns");
                        }
                    }
                }
                if (found == 0){
                    if ((termList.get(i)).toString().indexOf("谁") != -1 ||
                            (termList.get(i)).toString().indexOf("哪个人") != -1 ||
                        (termList.get(i)).toString().indexOf("诗人") != -1||
                            (termList.get(i)).toString().indexOf("作者") != -1 ||
                            (termList.get(i)).toString().indexOf("化学家") != -1 ||
                            ( i < termList.size() - 1 &&
                            (((termList.get(i)).toString().indexOf("女性") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("是") != -1))) ||
                            ( i < termList.size() - 1 &&
                            (((termList.get(i)).toString().indexOf("哪位") != -1 &&
                                    (termList.get(i+1)).toString().indexOf("人物") != -1)))){
                        found = 1;
                        bw2.write("nr"); // 人名
                    }
                }
                if (found == 0){
                    if ((termList.get(i)).toString().indexOf("几个") != -1 ||
                            (termList.get(i)).toString().indexOf("几回") != -1 ||
                            (termList.get(i)).toString().indexOf("几场") != -1 ||
                            (termList.get(i)).toString().indexOf("几") != -1 ||
                            (termList.get(i)).toString().indexOf("次") != -1 ||
                            (termList.get(i)).toString().indexOf("多少年") != -1 ||
                            (termList.get(i)).toString().indexOf("哪一年") != -1 ||
                            (termList.get(i)).toString().indexOf("年份") != -1){
                        found = 1;
                        bw2.write("m");
                    }
                    if(i < termList.size() - 1) {
                            if (((termList.get(i)).toString().indexOf("多少") != -1  &&
                                    (termList.get(i+1)).toString().indexOf("个") != -1) ||
                            ((termList.get(i)).toString().indexOf("多少") != -1  &&
                                    (termList.get(i+1)).toString().indexOf("枚") != -1) ||
                            ((termList.get(i)).toString().indexOf("哪") != -1  &&
                                    (termList.get(i+1)).toString().indexOf("年") != -1)){
                        found = 1;
                        bw2.write("m"); //m // 数量词
                        }
                    }
                }
                if(found ==0){
                    if (i < termList.size() - 1) {
                        if (((termList.get(i)).toString().indexOf("哪种") != -1  &&
                                (termList.get(i+1)).toString().indexOf("食物") != -1)) {
                            found = 1;
                            bw2.write("nf");
                        }
                    }
                }
                if(found ==0){
                    if (i < termList.size() - 1) {
                        if (((termList.get(i)).toString().indexOf("哪种") != -1  &&
                                (termList.get(i+1)).toString().indexOf("植物") != -1)) {
                            found = 1;
                            bw2.write("nb");
                        }
                    }
                }
            }
            if (found == 0){
                found = 1;
                bw2.write("n"); // 一般名词
            }
            bw.write("\n");
            bw2.write("\n");
        }
        bw.flush();
        bw.close();
        bw2.flush();
        bw2.close();


        /*f： 方位词   g：学术词汇  m：数量词 nb：生物名  nf：食品   nm：物品名  nn：职业、职务
        nr：人  ns：地  nt：机构团体  t：时间词  xu：网址url

        HanLP:   rr : 人称代词    rys: 处所疑问代词 ryt：时间疑问代词*/
    }
}


