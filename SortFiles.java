import java.util.*;
import java.io.*;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;

public class SortFiles extends Thread
{
    public static File file;
	public static File rfile;
	public static Thread[] t=new Thread[10];
    static boolean file_reader_flag=true;
	public static List<File> sortedfilelist = new ArrayList<File>();
	public static List<File> filelist = new ArrayList<File>();
	String thread_name;
    SortFiles()
	{

	}
	SortFiles(String thread_name)
	{
		this.thread_name=thread_name;
	}
    synchronized public void run()											       // Every thread calls run method
	{
		try
		{
			sortfiles(file);
				
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
    public static File sort(File fname,int thread_count) throws IOException
    {
		try
		{
        // filelist=fname;
		file=fname;
        for(int i=1;i<=thread_count;i++)					                        // create multiple threads specified by user
		{
			t[i]=new SortFiles("Thread"+i);
			t[i].start(); 
		}
		for (int i=1;i<=thread_count;i++) 							
		{
			t[i].join();															// wait till thread execution is done
		}
    	}
		catch(Exception e)
		{
		System.out.println(e);
		}
		return rfile;
    }
                                                                                    // This method sorts individual block of files
    public static void sortfiles(File f) throws IOException                          
	{
    FileReader fileReader = new FileReader(f);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    List<String> lines = new ArrayList<String>();
    String line = null;
    while ((line = bufferedReader.readLine()) != null) {
        lines.add(line);
    }
    bufferedReader.close();

    Collections.sort(lines, Collator.getInstance());

    FileWriter writer = new FileWriter("sort"+f.getName());
    for(String str: lines) {
      writer.write(str + "\r\n");
    }
	rfile=f;
    writer.close();

}
}