import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Vocabulary
{
	private static long id_counter;
	private static Map<String, Long> word_id_mapping;
	
	Vocabulary()
	{
		id_counter = 0;
		word_id_mapping = new ConcurrentHashMap<>();
	}
	
	static Long put(String word)
	{
		word_id_mapping.putIfAbsent(word, id_counter++);
		return word_id_mapping.get(word);
	}
	
	static Long get(String word)
	{
		return word_id_mapping.getOrDefault(word, -1L);
	}
	
	static void save()
	{
		FileOps fileOps = FileOps.getInstance();
		fileOps.save(false , "Vocab", Constants.vocab_file, Constants.genericToString(word_id_mapping));
	}
	
	public static void clear()
	{
		word_id_mapping.clear();
	}
	
	static void load() throws IOException
	{
		id_counter = 0;
		word_id_mapping = new ConcurrentHashMap<>();
		
		BufferedReader fileReader = new BufferedReader(new FileReader(Constants.vocab_full_path));
		String line;
		do
		{
			line = fileReader.readLine();
			if(line!=null && !line.isEmpty())
			{
				String[] word_id = line.split("\t:\t");
				word_id_mapping.put(word_id[0], Long.valueOf(word_id[1]));
			}
		}while (line != null);
		
		id_counter = word_id_mapping.size() -1;
	}
}
