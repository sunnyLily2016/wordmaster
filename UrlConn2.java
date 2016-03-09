import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: UrlConn2
 * @author: lixu
 * @date: Mar 8, 2016 2:05:22 PM
 */
public class UrlConn2 {
	private static int wordsNum = 0;
	private static BufferedWriter bw = null;
	private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy2.de.signintra.com", 80));

	private static Pattern letterPattern = Pattern.compile("<li class=\"currentpage\"><span>(.*?)</span></li>");
	private static Pattern pagePattern = Pattern.compile("<li class=\"activepage\"><span>(.*?)</span></li>");
	private static Pattern aPattern = Pattern.compile("<li>.*?<a href=\"http://www.*?\">(.*?)</a></li>");
	private static Pattern wordPattern = Pattern.compile("<a href=\"http://www.*?\" title=\".*?\">(.*?)</a>");
	
	public static void main(String[] args){
		try {
			String fileName = "./output2.txt";
			bw = new BufferedWriter(new FileWriter(fileName));
			fetchWords("A-B", 1);
			for(int i = 0; i < countLines(fileName); ++i)
   	     	{
        		System.out.println(choose(fileName));
   	     	}
		} catch (IOException e) {		
			e.printStackTrace();
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static void fetchWords(String letter, int page){
		// Get all words, write to file
		// Get next url
		// if exist, call fetchWords(newUrl);
		BufferedReader br = null;
		try {
			
			String urlString = new String("http://www.oxfordlearnersdictionaries.com/wordlist/english/oxford3000/Oxford3000_"+letter+"/?page="+page);
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection(proxy);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			String nextLetter = null;
			String nextPage = null;	
			String line = null;
			while((line = br.readLine()) != null) {
				// find the next letter
				Matcher letterMatcher = letterPattern.matcher(line);
				if(letterMatcher.find()) {
					line = br.readLine();
					letterMatcher = aPattern.matcher(line);
					if(letterMatcher.find()) {
						nextLetter = letterMatcher.group(1);
					}
				} 
				
				// find the next page
				Matcher pageMather = pagePattern.matcher(line);
				if(pageMather.find()) {
					line = br.readLine();
					pageMather = aPattern.matcher(line);
					if(pageMather.find()) {
						nextPage = pageMather.group(1);
					}
				} 

				// find all the words
				Matcher wordMatcher = wordPattern.matcher(line);
				if(wordMatcher.find()) {
					String word = wordMatcher.group(1);
					if(word.contains("1")) {
						word = word.substring(0, word.indexOf(" "));
					} else if(word.contains("2")) {
						continue;
					}
					bw.write(word);
					bw.newLine();
					bw.flush();
					wordsNum++;
					
				}
			}	
			
			if(nextPage != null) {
				fetchWords(letter, ++page);
			} else {
				 if(nextLetter != null) {
					fetchWords(nextLetter, 1);
				 } else {
					System.out.println("WordsNum:"+wordsNum);
				 }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	

	@SuppressWarnings("finally")
	public static int countLines(String fileName) {
		LineNumberReader reader = null;
		int count = 0;
		try {
			reader = new LineNumberReader(new FileReader(fileName));
			String line = null;
			while((line = reader.readLine()) != null) {}
			
			count = reader.getLineNumber();	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			return count;
		}
	}
	
	  
	@SuppressWarnings("finally")
	private static String choose(String fileName) {
	     String result = null;
	     String s = null;
	     int n = 0;
	     Random rand = new Random();
	     Scanner sc = null;
	     try {
			sc = new Scanner(new File(fileName));
			while(sc.hasNext()) {
		    	s = sc.nextLine(); 
		        if(rand.nextInt(++n) == 0)
		           result = s;         
		     }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(sc != null) {
				sc.close();
			}
			return result;
		}     
	}
}
