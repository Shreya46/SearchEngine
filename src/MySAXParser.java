import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MySAXParser extends DefaultHandler
{
	private static final String WIKI_TITLE = "title";
	private static final String WIKI_TEXT = "text";
	
	private Tags tag;
	private Page page;
	private long startTime;
	private long pageCounter;
	private StringBuilder text_content;
	private FileInputStream fileInputStream;
	private ExecutorService threadPoolService;
	
	public void parseXml(String fileName) throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		text_content = new StringBuilder();
		SAXParser parser = factory.newSAXParser();
		fileInputStream = new FileInputStream (fileName);
		parser.parse(fileInputStream, this);
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
	{
		if (tag != Tags.unknown &&  length>3)
		{
			String readText = new String(ch, start, length);
			if (tag == Tags.title)
				page.setTitle(readText);
			LineParser partLine = new LineParser(readText, tag, page);
			threadPoolService.execute(partLine);
		}
	}
	
	@Override
	public void startDocument()
	{
		new Vocabulary();
		System.out.print("\n\t PHASE 1 : PARSING XML FILE, creating page level inverted index........");
		pageCounter = 0;
		startTime = System.currentTimeMillis();
		text_content = new StringBuilder();
		tag = Tags.unknown;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	{
		if (WIKI_TITLE.equals(qName))
		{
			threadPoolService = Executors.newCachedThreadPool();
			tag = Tags.title;
			try
			{   page = new Page (pageCounter++, fileInputStream.getChannel().position()); }
			catch (IOException e)
			{   e.printStackTrace();	}
		}
		if (WIKI_TEXT.equals(qName))
			tag = Tags.text_body;
		if (tag != Tags.unknown)
			text_content.setLength(0);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
	{
		if (WIKI_TEXT.equals(qName))
		{
			threadPoolService.shutdown();
			try
			{	threadPoolService.awaitTermination(10, TimeUnit.MINUTES);	}
			catch (InterruptedException e)
			{ e.printStackTrace(); }
			page.run();
		}
		
		if (tag != Tags.unknown)
			tag = Tags.unknown;
	}
	
	@Override
	public void endDocument()
	{
		Long diff = (System.currentTimeMillis()  - startTime);
		System.out.println(" COMPLETED. took " + diff.toString() + " MILLI-SEC.");
	}
}