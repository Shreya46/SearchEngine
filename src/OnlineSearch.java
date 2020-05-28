import java.io.*;
import java.util.*;

public class OnlineSearch
{
	public static int totalNoOfPages;
	public static ArrayList<Long> indexer;
	public static void main(String[] args) throws Exception
	{
		resourceCheck();
		indexer = fetchWordIndexer();
		FileOps fileOps = FileOps.getInstance();
		fileOps.loadBookmarks();
		totalNoOfPages = fileOps.BookmarkSize();
		
		//ToDo :
		//      1. user input
		//      2. parse words through same functions
		//      2.5 fetch and save list for each wordId from Integrated
		//      3. tf-idf
		//      4. load bookmarks
		
		String userInput = "";
		Scanner scanner = new Scanner(System.in);
		
		while (true)
		{
			userInput = scanner.nextLine();
			if ("exit".compareToIgnoreCase(userInput) == 0)
				System.exit(0);
			
			List <Long> wordIds = new ArrayList<>();
			LineParser.getBaseWord(userInput).forEach(word -> wordIds.add(Vocabulary.get(word)));
			if (wordIds.size() > 0)
				new PostOps(wordIds);
		}
	}
	
	private static ArrayList<Long> fetchWordIndexer() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(Constants.wordIdIndex_full_path));
		ArrayList <Long> wordIdIndexer = new ArrayList<>();
		String line = "";
		while (line!= null)
		{
			line = in.readLine();
			if (line == null )
				break;
			
			wordIdIndexer.add(Long.parseLong(line));
		}
		return wordIdIndexer;
	}
	
	private static void resourceCheck()
	{
		int integrated = Objects.requireNonNull(new File("Data/output/Integrated").listFiles()).length;
		int vocab = Objects.requireNonNull(new File("Data/output/Vocab").listFiles()).length;
		int bookmarks = Objects.requireNonNull(new File("Data/output/Bookmarks").listFiles()).length;
		int wordIndexer = Objects.requireNonNull(new File("Data/output/Page").listFiles()).length;
		
		if(integrated==0 || vocab==0 || bookmarks==0 || wordIndexer==0)
		{
			System.out.println("One or more required resources not found, please run offline part first.");
			System.exit(1);
		}
		
		try
		{   Vocabulary.load();	}
		catch (IOException e)
		{
			System.out.println("Error loading vocabulary");
			System.exit(1);
		}
	}
}