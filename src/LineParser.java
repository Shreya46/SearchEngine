import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineParser implements Runnable
{
	Page page;
	String text;
	Tags tag;
	
	LineParser(String text, Tags tag, Page page)
	{
		this.tag = tag;
		this.page = page;
		this.text = text;
	}
	
	@Override
	public void run()
	{
		getBaseWord(text).forEach (root_word -> page.put ( Vocabulary.put(root_word), tag));
		
		/*Todo : handle edge cases like :
			0.  Create and handle more tags like : refs, links, etc
			1.  < 2000millenia > : int-words split
			2.  < ''face &  quarterly'' > :  headings split by " " still containing ' ' ' at beginning/end  */
	}
	
	public static List<String> getBaseWord (String line)
	{
		/*
				1. split on any non-alphanumeric char to get a word
				2. convert each word to lowercase
				3. ignore most commonly occurring words like 'the', 'a', etc
				4. ignore small words with len < 2
				5. get root for each word, Example : running -> run
				6. get wordId after putting it into Vocabulary
		 */
		
		return Arrays.stream (line.split("[^a-zA-Z0-9']"))
				.map (String::toLowerCase)
				.filter (lower_case_word -> !Constants.stop_words.contains(lower_case_word))
				.filter (word -> word.length() > 2)
				.map (Stemmer::getStem).collect(Collectors.toList());
	}
}