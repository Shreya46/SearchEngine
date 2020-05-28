import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileOps  //ThreadSafeSingleton for lazy binding
{
	private static FileOps instance;
	private Map<Long, String> bookmarks;
	
	private FileOps()
	{
		bookmarks = new HashMap<>();
	}
	
	static synchronized FileOps getInstance()
	{
		if(instance == null)
			instance = new FileOps();
		return instance;
	}
	
	void saveBookmarks()
	{
		this.save(false,"Bookmarks", Constants.bookmarks, Constants.genericToString(this.bookmarks));
	}
	
	void loadBookmarks() throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(Constants.bookmarks_path));
		String line = "";
		while (line!= null)
		{
			line = in.readLine();
			if (line == null )
				break;
			
			String[] pg_title = line.split("\t:\t");
			String title = pg_title[1];
			Long pg = Long.valueOf(pg_title[0]);
			this.bookmarks.put(pg, title);
		}
	}
	
	void put(long id, long pointer, String title)
	{
		bookmarks.putIfAbsent(id, pointer+"_"+title);
	}
	
	public String get (long id)
	{	return bookmarks.get(id);	}
	
	void save(boolean append, String type, String id, String data)
	{
		File file = new File("Data/output/"+ type + "/" + id);
		try
		{   file.createNewFile();   }
		catch (IOException e)
		{   System.out.println("file already present"); }
		FileWriter fr = null;
		BufferedWriter br = null;
		try
		{
			if(append)
				fr = new FileWriter(file, true);
			else
				fr = new FileWriter(file);
			br = new BufferedWriter(fr);
			br.write(data);
		}
		catch (IOException e)
		{	e.printStackTrace();    }
		finally
		{
			try
			{
				assert br != null;
				br.close();
				fr.close();
			}
			catch (IOException e)
			{ e.printStackTrace();  }
		}
	}
	
	public int BookmarkSize()
	{
		return this.bookmarks.size();
	}
}