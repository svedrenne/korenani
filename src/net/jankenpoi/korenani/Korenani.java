package net.jankenpoi.korenani;

import java.io.IOException;
import java.sql.SQLException;

import com.polarcloud.rikaichan.RcxData;


public class Korenani {

 
    public static String text = 
//"日本語検定";
//"本編をご覧になるには、会員登録をしてください。お申し込みはこちらから。";
//"ミュージカル";
//"冒頭写真｜原発４基と海、排気塔＝２０１１年３月１２日、福島県大熊町、朝日新聞社ヘリから、山本裕之撮影プロローグ";
//"上海艾杰飞人力资源有限公司（リクルート中国法人）企画部のチンと申します。ご転職をお考えになる際、お役にたてればと思います！是非つながってください。";
//"「有り難ふ御座りまする」（有り難う御座います）の元の意味は、" + "読んで字のごとく「なかなかないことです」です。"
//            + "「なかなかない、貴重なことです」という表現が、いつしか、謝意を表す言葉として転用されているわけです。";
    "別に、問題ないんです。又ね。"
            ;
    
    //
    private static String pathToDictionary = "dictionaries/rikaichan-en-2-01-140601/dict.sqlite";
    
    public static void myMain(String[] args) throws IOException, SQLException {
        
        RcxData.deinflectInit();
        
        for (int i=0; i<text.length(); i++) {
            String word = text.substring(i, Math.min(i + 15, text.length()));
            RcxData.DictionaryEntry[] dicoEntries = RcxData._wordSearch(word, pathToDictionary, 10);
            for (int j = 0; j < dicoEntries.length; j++) {
                System.out.println("Korenani.main() dicoEntry[" + j + "] = " + dicoEntries[j]);
            }
        }
    }
    
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        myMain(args);
        
    }
    
}
