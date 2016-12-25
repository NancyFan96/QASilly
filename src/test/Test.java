package test;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

public class Test {
	public static void main(String args[]) throws IOException{
		String question = "历史事件戚继光抗倭发生在哪个朝代";
		String zhidaoBaseFile = "snippet_for_5.txt";
		String sentence = null;
		
        FileInputStream fis = new FileInputStream(zhidaoBaseFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br_zhidao = new BufferedReader(isr);
        
        ArrayList<String> mark = new ArrayList<String>(Arrays.asList("，", "（", "）", "”", "“", "：", "。", "？", "《", "》", "、"));

        
        List<String> wisdomFromZhidaoList = new ArrayList<String>();
        while((sentence = br_zhidao.readLine()) != null){
        	String addWisdom = null;
	 	       
        	System.out.println(sentence);
			List<Term> termList = NLPTokenizer.segment(sentence);
			List<String> keyList = HanLP.extractKeyword(sentence,5);

			System.out.println(termList);
			System.out.println(keyList);
			
			if(termList.size() < 4){
				 int ii = 1;
				 addWisdom = termList.get(termList.size()-ii).word;
				 while(ii <= termList.size() && mark.contains(addWisdom)){
					 System.out.println(addWisdom);
					 ii++;
					 addWisdom = termList.get(termList.size()-ii).word;
				 }
				 wisdomFromZhidaoList.add(addWisdom);
			}
			/*else if(keyList.size() < 4){
				 int ii = 1;
				 addWisdom = keyList.get(keyList.size()-ii);
				 while(ii <= keyList.size() && mark.contains(addWisdom)){
					 System.out.println(addWisdom);
					 ii++;
					 addWisdom = keyList.get(keyList.size()-ii);
				 }
				 wisdomFromZhidaoList.add(addWisdom);
			}	*/
        }//while
		Collections.sort(wisdomFromZhidaoList);
//		System.out.println("wisdomList: "+wisdomFromZhidaoList);
		for(String wisdom : wisdomFromZhidaoList){
			int cnt = wisdomFromZhidaoList.lastIndexOf(wisdom) - wisdomFromZhidaoList.indexOf(wisdom) + 1;
			if(cnt > wisdomFromZhidaoList.size()/2){
				System.out.println("Find wisdom "+wisdom);
			}
		}
	}
}
