/**
 * Created by zhouyang on 16/12/20.
 */
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.*;
import com.hankcs.hanlp.tokenizer.*;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.seg.*;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.awt.image.ImageWatched;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.InputStreamReader;
import java.io.FileInputStream;

public class AnswerExtract
{
    private final String file_type = "7000";//200 or 7000;

    private final String OpenClose = "close";//open or close;

    private boolean check_answer = false;//200 could check answer, 7000 could not;

    //close 7000
   private final String file_snippet_prefix = "/Users/zhouyang/Workspaces/QASystem/src/final_7000/v3_7000_";

    //open 7000 & 200
    // private final String file_snippet_prefix = "/Users/zhouyang/Workspaces/QASystem/src/snippet_zhidao_" + file_type + "/snippet_for_";






    private final String file_key_words = "/Users/zhouyang/Workspaces/QASystem/src/in/key_word_" + file_type;
    private final String file_answer_type = "/Users/zhouyang/Workspaces/QASystem/src/in/answer_type_" + file_type;
    private final String file_test = "/Users/zhouyang/Workspaces/QASystem/src/in/test.txt";


    private final String file_question = "/Users/zhouyang/Workspaces/QASystem/src/in/question_" + file_type + ".txt";
    private final String file_vectors = "/Users/zhouyang/Workspaces/QASystem/src/in/vectors.bin." + file_type;

    private final String file_predict_sentence = "/Users/zhouyang/Workspaces/QASystem/src/meta/predict_sentence.txt";
    private final String file_predict_words = "/Users/zhouyang/Workspaces/QASystem/src/meta/predict_words.txt";

    private final String file_res = "/Users/zhouyang/Workspaces/QASystem/src/out/results" + file_type + ".txt";



    private BufferedReader br_key_word = null;
    private BufferedReader br_answer_type = null;

    private OutputStreamWriter osw_sentence = null;
    private OutputStreamWriter osw_words = null;

    private ArrayList<String> StopWord = null;
    private ArrayList<String> StopType = null;
    private ArrayList<String> mark = null;
    private final int sentence_num = 4;


    public AnswerExtract() throws IOException
    {
        FileInputStream fis = new FileInputStream(file_key_words);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        br_key_word = new BufferedReader(isr);


        fis = new FileInputStream(file_answer_type);
        isr = new InputStreamReader(fis, "UTF-8");
        br_answer_type = new BufferedReader(isr);


        FileOutputStream fos = new FileOutputStream(file_predict_sentence);
        osw_sentence = new OutputStreamWriter(fos, "UTF-8");

        fos = new FileOutputStream(file_predict_words);
        osw_words = new OutputStreamWriter(fos, "UTF-8");


        StopWord = new ArrayList<String>(Arrays.asList("”", "，", "》", "的", "个", "有可能", "？", "是", "为"));
        StopType = new ArrayList<String>(Arrays.asList("f", "nx", "t", "xu", "rr", "ryt", "rys",
                "ude1", "a", "d", "ac", "ry", "vu", "ad", "pba", "w", "v"));

        mark = new ArrayList<String>(Arrays.asList("，", "（", "）", "”", "“", "：", "。", "？", "《", "》", "、"));


    }
    public static void main(String args[])
    {
        AnswerExtract onQA;
        try
        {
            onQA = new AnswerExtract();
            onQA.sentence_words_extract();
            onQA.answer_extract();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public boolean checkIfStopWord(String str)
    {
        for(String stopword : StopWord)
        {
            if(stopword.equals(str))
                return true;
        }
        return false;
    }

    public boolean checkIfStopType(String str)
    {
        for(String stoptype : StopType)
        {
            if(stoptype.equals(str))
                return true;
        }
        return false;
    }


    //chose one sentence from the file to attain the result finaly.
    public void sentence_words_extract() throws IOException
    {
        String question = null;
        String answer_type = null;
        String sentence;

        int ques_num = 0;
        OutputStreamWriter osw_test = null;

        FileOutputStream fos = new FileOutputStream(file_test);
        osw_test = new OutputStreamWriter(fos, "UTF-8");

        while((question = br_key_word.readLine()) != null)
        {
            ques_num++;

            answer_type = br_answer_type.readLine();
            StringTokenizer st = new StringTokenizer(answer_type);
            answer_type = st.nextToken();
            answer_type = st.nextToken();


            st = new StringTokenizer(question);
            ArrayList<String> key_words = new ArrayList<String>();
            while(st.hasMoreElements())
            {
                key_words.add(st.nextToken());
            }


            String filename_dataset = null;
            //using the zhidao to do the words extract;



            if(OpenClose.equals("open")) {
                //open 7000 & 200
                filename_dataset = file_snippet_prefix + ques_num + ".txt";
            }
            else {
                //close 7000
                filename_dataset = file_snippet_prefix + ques_num;
            }





            FileInputStream fis = new FileInputStream(filename_dataset);

            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br_sentences = new BufferedReader(isr);

            ArrayList<String> cadidate_Sentences = new ArrayList<String>();
            while((sentence = br_sentences.readLine()) != null)
            {
                if(sentence.length() != 0)
                    cadidate_Sentences.add(sentence);
            }






            for(int i = 0; i < cadidate_Sentences.size(); i++)
            {
                List<Term> termList = NLPTokenizer.segment(cadidate_Sentences.get(i));
                for(int j = 0; j < termList.size(); j++)
                {
                    osw_test.write(termList.get(j).word + "\t");
                }
                osw_test.write("\n");
            }







            int [] match_num = new int[cadidate_Sentences.size()];

            for(int i = 0; i < cadidate_Sentences.size(); i++)
            {
                match_num[i] = 0;
                for(int j = 0; j < key_words.size(); j++)
                {
                    if (cadidate_Sentences.get(i).contains(key_words.get(j)))
                    {
                        match_num[i]++;
                    }
                }
            }

            HashMap<String, Integer> sort_sentence = new HashMap<String, Integer>();
            for(int i = 0; i < cadidate_Sentences.size(); i++)
            {
                sort_sentence.put(cadidate_Sentences.get(i), match_num[i]);
            }
            List<String> v = new ArrayList<String>(sort_sentence.keySet());
            Collections.sort(v, new Comparator<Object>()
            {
                public int compare(Object arg0, Object arg1)
                {
                    return new Double(sort_sentence.get(arg1)).compareTo(new Double(sort_sentence.get(arg0)));
                }
            });






//            String selectsentence = cadidate_Sentences.get(0) + cadidate_Sentences.get(1)
//                    + cadidate_Sentences.get(2) + cadidate_Sentences.get(3);


            String selectsentence = new String();
            int k = v.size() >= sentence_num ? sentence_num : v.size();
            for(int i = 0; i < k; i++)
                selectsentence += v.get(i);

            osw_sentence.write(selectsentence + "\n");

            List<Term> termList = NLPTokenizer.segment(selectsentence);
//            System.out.println(termList);

            for(int i = 0; i < termList.size(); i++)
            {
                Term term = termList.get(i);
                if(((term.nature.toString().contains(answer_type) || answer_type.contains(term.nature.toString()))
                        && !checkIfStopType(term.nature.toString())
                        && !checkIfStopWord(term.word)))
                    osw_words.write(term.word + "\t");
            }
            osw_words.write("\n");

        }
        osw_sentence.flush();
        osw_sentence.close();
        osw_words.flush();
        osw_words.close();
        osw_test.flush();
        osw_test.close();

    }


    public void answer_extract() throws IOException
    {
        FileInputStream fis = new FileInputStream(file_predict_words);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br_words = new BufferedReader(isr);


        fis = new FileInputStream(file_predict_sentence);
        isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br_sentence = new BufferedReader(isr);


        fis = new FileInputStream(file_question);
        isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br_question = new BufferedReader(isr);


        fis = new FileInputStream(file_key_words);
        isr = new InputStreamReader(fis, "UTF-8");
        br_key_word = new BufferedReader(isr);


        fis = new FileInputStream(file_answer_type);
        isr = new InputStreamReader(fis, "UTF-8");
        br_answer_type = new BufferedReader(isr);


        FileOutputStream fos = new FileOutputStream(file_res);
        OutputStreamWriter osw_res = new OutputStreamWriter(fos, "UTF-8");



        //br_key_word, br_answer_type;


        //using conbined result to do the word2vec training.
        Word2VEC vec = new Word2VEC();
        vec.loadModel(file_vectors);


        String key_words = null;
        String answer = null;
        String candidate_words = null;
        int cnt = 0, cnt_ideal = 0, cnt_top3 = 0;
        int ques_cnt = 0;
        while((key_words = br_key_word.readLine()) != null)
        {
            ques_cnt++;
            StringTokenizer st_key = new StringTokenizer(key_words);

            String question = br_question.readLine();
            String sentence = br_sentence.readLine();
            String answertype = br_answer_type.readLine();

            StringTokenizer st_answertype = new StringTokenizer(answertype);
            answertype = st_answertype.nextToken();
            answertype = st_answertype.nextToken();

//            System.out.println(question);

            StringTokenizer st_ques = new StringTokenizer(question);
            question = st_ques.nextToken();

//            ArrayList<String> a = new ArrayList<String>();
//            ArrayList<String> a = get_keywords(question);
            ArrayList<String> a = null;

            if(a == null) {
                a = new ArrayList<String>();

                while (st_key.hasMoreElements()) {
                    a.add(st_key.nextToken());
                }
            }



            candidate_words = br_words.readLine();
            StringTokenizer st_candi = new StringTokenizer(candidate_words);
            ArrayList<String> b = new ArrayList<String>();
            while(st_candi.hasMoreElements())
            {
                String temp = st_candi.nextToken();
                if(!b.contains(temp))
                    b.add(temp);
            }
            if(check_answer)
                answer = st_ques.nextToken();

            String myanswer = template_get_answer(question, a, sentence, b);
            if(myanswer != null)
            {
                System.out.println(ques_cnt + "\t" + myanswer);
                osw_res.write(ques_cnt + "\t" + myanswer + "\n");
                if(check_answer)
                    System.out.println(myanswer + " " + answer);
                if(check_answer && myanswer.equals(answer))
                    cnt++;
                continue;
            }


            ArrayList<Double> points = get_point(a, b, vec);

//            for(int i = 0; i < b.size(); i++) {
//                System.out.print("[" + b.get(i) + ", " + points.get(i) + "]");
//            }
//            System.out.print("\n");


            HashMap<String, Double> sort_answer = new HashMap<String, Double>();
            for(int i = 0; i < b.size(); i++)
            {
                sort_answer.put(b.get(i), points.get(i));
            }
            List<String> v = new ArrayList<String>(sort_answer.keySet());
            Collections.sort(v, new Comparator<Object>()
            {
                public int compare(Object arg0, Object arg1)
                {
                    return new Double(sort_answer.get(arg1)).compareTo(new Double(sort_answer.get(arg0)));
                }
            });




            if(v.size() == 0) {
                List<Term> termList = NLPTokenizer.segment(sentence);
//            System.out.println(termList);
                ArrayList<String> c = new ArrayList<String>();
                for(int i = 0; i < termList.size(); i++)
                {
                    Term term = termList.get(i);
                    if(!checkIfStopType(term.nature.toString()) && !checkIfStopWord(term.word)
                            && !c.contains(termList.get(i).word))
                        c.add(termList.get(i).word);
                }
                ArrayList<Double> points_excep = get_point(a, c, vec);


                HashMap<String, Double> sort_answer_excep = new HashMap<String, Double>();
                for(int i = 0; i < c.size(); i++)
                {
                    sort_answer.put(c.get(i), points_excep.get(i));
                }
                List<String> v_excep = new ArrayList<String>(sort_answer_excep.keySet());
                Collections.sort(v_excep, new Comparator<Object>()
                {
                    public int compare(Object arg0, Object arg1)
                    {
                        return new Double(sort_answer_excep.get(arg1)).compareTo(new Double(sort_answer_excep.get(arg0)));
                    }
                });

                if(v_excep.size() != 0) {
                    myanswer = v_excep.get(0);
                }
                else {
                    myanswer = null;
                }

                System.out.println(ques_cnt + "\t" + myanswer);
                osw_res.write(ques_cnt + "\t" + myanswer + "\n");

                continue;
            }

            for(int i = 0; i < v.size() && i < 2; i++)
            {
                if(check_answer && v.get(i).equals(answer))
                    cnt_top3++;
            }

            myanswer = GetAnswer(a, (ArrayList<String>)v);

            System.out.println(ques_cnt + "\t" + myanswer);
            osw_res.write(ques_cnt + "\t" + myanswer + "\n");

            if(check_answer && myanswer.equals(answer))
                cnt++;


            if(check_answer && b.contains(answer))
                cnt_ideal++;

//            if(check_answer && sentence.contains(answer))
//                cnt_ideal++;
        }
        osw_res.flush();
        osw_res.close();
        if(check_answer)
            System.out.print(cnt + " " + cnt_ideal + " " + cnt_top3);
    }

    public String template_get_answer(String question, ArrayList<String> key_words,
                                      String sentence, ArrayList<String> candidate_words) throws UnsupportedEncodingException
    {

        if(question.contains("谁"))
        {
            Segment segment = HanLP.newSegment().enableNameRecognize(true);
            List<Term> termList = segment.seg(sentence);
            for(int i = 0; i < termList.size(); i++)
            {
//                if(termList.get(i).nature.toString().contains("nr"))
//                {
//                    return termList.get(i).word;
//                }
            }
//            System.out.println(termList);
        }
        if(question.contains("下一句"))
        {
            Pattern p = Pattern.compile( "“[\\s\\S]*”");
            Matcher m = p.matcher(question);
            if (m.find()) {
                String senten = m.group(0);
                senten = senten.replace("“", "");
                senten = senten.replace("”", "");
//                System.out.println(senten);
                String temp = senten;

                p = Pattern.compile(senten + "，[\\s\\S]*。");
                m = p.matcher(sentence);
                if(m.find())
                {
//                    System.out.println(m.group(0));
                    senten = m.group(0);

//                    int min_index = (1 << 30);
//                    for(int i = 0; i < mark.size(); i++)
//                    {
//                        int t = senten.indexOf(mark.get(i));
//                        if(t > 0)
//                            min_index = Math.min(min_index, t);
//                    }

                    int t2 = senten.indexOf("，");
                    if(t2 + 1 + temp.length() < senten.length()) {
                        senten = senten.substring(t2 + 1, t2 + 1 + temp.length());

//                    senten = senten.substring(senten.indexOf("，")+1,senten.indexOf("。"));
//                    System.out.println(senten);
                        return senten;
                    }
                }

            }
        }

        return null;
    }


    public ArrayList<String> get_keywords(String question)
    {
        ArrayList<String> key_words = new ArrayList<String>();
        List<Term> termList = NLPTokenizer.segment(question);

        for(int i = termList.size() - 1; i >= 0; i--) {

            if (termList.get(i).nature.toString().contains("vshi")) {

                int KeyWordNum = findKeyWord(termList, i);
                if (KeyWordNum != -1) {

                    Term KeyWord = termList.get(KeyWordNum);
                    if(!key_words.contains(KeyWord.word))
                        key_words.add(KeyWord.word);

                    int KeyWordNum2 = _findKeyWord(termList, KeyWordNum);
                    if (KeyWordNum2 != -1) {

                        Term KeyWord2 = termList.get(KeyWordNum2);
                        if(!key_words.contains(KeyWord2.word))
                            key_words.add(KeyWord2.word);

//                    System.out.println(KeyWord2.word + " " + KeyWord.word + " " + question);

                    }
                }
            }

            if (termList.get(i).word.contains("哪")) {
                int KeyWordNum = findKeyWord2(termList, i);
                if(KeyWordNum != -1) {

                    Term KeyWord = termList.get(KeyWordNum);
                    if(!key_words.contains(KeyWord.word))
                        key_words.add(KeyWord.word);
                }
            }

            if(termList.get(i).word.contains("什么"))
            {
                int KeyWordNum = findKeyWord(termList, i);
                if (KeyWordNum != -1) {

                    Term KeyWord = termList.get(KeyWordNum);
                    if(!key_words.contains(KeyWord.word))
                        key_words.add(KeyWord.word);

                    int KeyWordNum2 = findKeyWord2(termList, KeyWordNum);
                    if (KeyWordNum2 != -1) {

                        Term KeyWord2 = termList.get(KeyWordNum2);
                        if(!key_words.contains(KeyWord2.word))
                            key_words.add(KeyWord2.word);

//                    System.out.println(KeyWord2.word + " " + KeyWord.word + " " + question);

                    }
                }
            }
            if(termList.get(i).word.contains("多少"))
            {

            }
            if(termList.get(i).word.contains("谁"))
            {
            }

        }
//        System.out.println(key_words);
        if(key_words.size() == 0)
            return null;
        return key_words;
    }


    public int findKeyWord(List<Term> termList, int num)
    {
        for(int i = 1; i < 5; i++)
        {
            if(num - i >= 0) {
                Term term = termList.get(num - i);

                if (term.nature.toString() != "d" && term.nature.toString() != "v"
                        && !checkIfStopType(term.nature.toString()) && !checkIfStopWord(term.word))
                    return num - i;
            }
        }
        return -1;
    }

    public int _findKeyWord(List<Term> termList, int num)
    {
        while(num >= 0 && !termList.get(num).word.equals("的"))
            num--;
        num --;
        while(num >= 0)
        {
            Term term = termList.get(num);
            if(term.nature.toString() != "d" && term.nature.toString() != "v"
                    && !checkIfStopType(term.nature.toString()) && !checkIfStopWord(term.word))
                return num;
            num --;
        }
        return -1;
    }

    public int findKeyWord2(List<Term> termList, int num)
    {
        for (int i = 1; i < 5; i++)
        {
            if (num + i < termList.size())
            {
                Term term = termList.get(num + i);

                if (term.nature.toString() != "d" && term.nature.toString() != "v"
                        && !checkIfStopType(term.nature.toString()) && !checkIfStopWord(term.word))
                    return num + i;
            }
        }
        return -1;
    }


    public String GetAnswer(ArrayList<String> key_words, ArrayList<String> candidate_words)
    {
        for(int i = 0; i < candidate_words.size(); i++)
        {
            if(!key_words.contains(candidate_words.get(i)))
                return candidate_words.get(i);
        }
        return null;
    }

    public ArrayList<Double> get_point(ArrayList<String> key_words, ArrayList<String> candidate_words, Word2VEC vec)
    {
        ArrayList<Double> points = new ArrayList<Double>();
        for(int i = 0; i < candidate_words.size(); i++)
            points.add(0.0);

        for(int i = 0; i < key_words.size(); i++) {

            Set<WordEntry> result = new TreeSet<WordEntry>();
            result = vec.distance(key_words.get(i));
            if(result == null)
                continue;
            Iterator iter = result.iterator();
            while (iter.hasNext()) {
                WordEntry word = (WordEntry) iter.next();
//                System.out.println(word.name + " " + word.score);
                int index = candidate_words.indexOf(word.name);
                if(index != -1)
                {
                    double original_value = points.get(index);
                    points.set(index, word.score + original_value);
                }
            }
        }
        return points;
    }

}


