import java.util.*;
import java.io.*;
import java.io.File;
import java.nio.charset.Charset;
import java.text.Collator;

public class SharedMemory extends Thread
{
    public static FileReader fr;
	public static BufferedReader br;
	public static BufferedReader br1,br2;
    public static Thread[] t=new Thread[10];
    public static boolean file_reader_flag=true;
    public static SortFiles sf =new SortFiles();
    public static List<File> fi =new ArrayList<File>();
    public static String file_name;
    String thread_name;
                                                                                                // Ask for user input filename and number of threads
    public static void main(String args[]) throws Exception                                  
    {
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter file name");
        String fname=br.readLine();
        System.out.println("Enter number of threads");
        int thread_count=Integer.parseInt(br.readLine());
        terasort(fname,thread_count);
    }

    public static void terasort(String fname, int thread_count) throws IOException
    {
        try
        {
        File temp;
        List<File> sortedfile = new ArrayList<File>();
        File file =new File(fname);
        File mergedFile = new File("MergedFile.txt");
        System.out.println("file size"+file.length());
        fi=splitFile(file);                                                                       // calls splitFile method
        File fma=new File(fname);                                                                  // delete original input file
		fma.delete();
        System.out.println("In terasort"+fi.size());
        long startTime = System.currentTimeMillis();                                              // Start time

        for(int i=0;i<fi.size();i++)
        {
        sortedfile.add(sf.sort(fi.get(i),thread_count));                                          // calls method sortedfile from SortFiles.java to sort individual chunk of block
        }

        String x=merge_sorted_files(sortedfile,"mergedFile.txt");
        long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;                                                      // End time
        System.out.println("Time:-"+totalTime);                                                    // Time taken to sort the file
        }
        catch(Exception e)
        {
			System.out.println("inside ctatch in terasort");	
		}
    }

public static List<File> splitFile(File f) throws IOException {                                     // This method splits the large file into chunks of blocks
        int Counter = 1;
        List<File> allfile =new ArrayList<File>();
        int chunk = 20 * 1024 *1024;                                                                 // 20 MB
        byte[] buffer = new byte[chunk];

        String fileName = f.getName();

        //try-with-resources to ensure closing stream
        try (BufferedInputStream bs = new BufferedInputStream(new FileInputStream(f))) {

            int bytesize = 0;
            while ((bytesize = bs.read(buffer)) > 0) {
                System.out.println("bytesize"+bytesize);

                //write each chunk of data into separate file with different number in name
                String filePartName = String.format("%s.%03d", fileName, Counter++);
                File newFile = new File(f.getParent(), filePartName+".txt");
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, bytesize);
                    allfile.add(newFile);
                }
            }
        }
        return allfile;
    }


    public static  String merge_sorted_files(List<File>sortedfile,String fname) throws Exception             // This method finally merge the sorted small chunk blocks
	{	
        String final_file_name=fname;
		try
		{

		File ffile= new File("sorted_"+final_file_name);
		ffile.delete();
		long f1_length=0,f2_length=0;
		int file_count=0;
		FileReader fr1,fr2;	
		String new_rline;
        int bytes_in_line=100;
		File f;
		ArrayList<String> arraylist1;		
		ArrayList<String> file_name_array=new ArrayList<String>();	
		for(int i=0;i<sortedfile.size();i++)
		{
			file_name_array.add((sortedfile.get(i)).getName());
		}
		
		int flag1=0,flag2=0;
        int i=0;
		while(file_count<file_name_array.size())
		{	
			long f1_records_read=0;
			long f2_records_read=0;
			String f1line=null;
			String f2line=null;
			String f1_line_key=null;
			String f2_line_key=null;
            i=i+1;
            System.out.println("Times"+i);
			file_count++;	
			File file1=new File(file_name_array.get(file_count));
			f1_length=(file1.length()/bytes_in_line);			
			System.out.println("f1_length "+f1_length);		
			fr1=new FileReader(file_name_array.get(file_count));							// read Server_list.txt 
			br1=new BufferedReader(fr1);			
			file_count++;
			File file2=new File(file_name_array.get(file_count));
			f2_length=(file2.length()/bytes_in_line);	
			System.out.println("f2_length "+f2_length);		
			fr2=new FileReader(file_name_array.get(file_count));
                                                                                		// read Server_list.txt 
            br2=new BufferedReader(fr2);
			FileWriter fwn=new FileWriter("cycle"+file_count+".txt");
			BufferedWriter bwn=new BufferedWriter(fwn);
			f1line=br1.readLine();
			f1_line_key=f1line.substring(0,10);
			f2line=br2.readLine();	
			f2_line_key=f2line.substring(0,10);
			f1_records_read++;
			f2_records_read++;
			
			System.out.println("file 3");
			while(((f1_records_read<=f1_length) && (f2_records_read<=f2_length)))
			{	
				flag1=flag2=0;					
				if((f1_line_key.compareTo(f2_line_key))<=0)
				{
					bwn.write(f1line);
					bwn.newLine();
					f1_records_read++;
					if(f1_records_read<=f1_length)					
					f1line=br1.readLine();
					f1_line_key=f1line.substring(0,10);	
					flag1=1;
					System.out.println("file 4");				
				}
				else
				{
					bwn.write(f2line);
					bwn.newLine();
					f2_records_read++;
					if(f2_records_read<=f2_length)					
					f2line=br2.readLine();
					f2_line_key=f2line.substring(0,10);
					flag2=1;
					System.out.println("file 5 before file 7");
				}									
			}
			System.out.println("file 7 after file 5");
			if(flag2==0)
			{			
				bwn.write(f2line);
				bwn.newLine();	
				f2_records_read++;							
			}
			while(f2_records_read<=f2_length)
			{
				f2line=br2.readLine();					
				bwn.write(f2line);
				bwn.newLine();
				f2_records_read++;				
			}

			if(flag1==0)
			{	
				bwn.write(f1line);
				bwn.newLine();	
				f1_records_read++;								
			}
			while(f1_records_read<=f1_length)
			{				
				f1line=br1.readLine();				
				bwn.write(f1line);
				bwn.newLine();
				f1_records_read++;				
			}
			
			br1.close();
			br2.close();
			bwn.close();
			System.out.println("Array List Size is:- "+file_name_array.size());
			f=new File(file_name_array.get(file_count-1));
			f.delete();			
			f=new File(file_name_array.get(file_count));
			f.delete();			
			file_name="pass_"+file_count+".txt";
			System.out.println("end of while");
		}
		file_name_array.clear();
		}
		catch(Exception e)
		{
			System.out.println("Exception!!");
			File un_sorted_file=new File(final_file_name);			
			un_sorted_file.delete();			
			br1.close();
			br2.close();
			File oname = new File(file_name);
			File nName = new File("sorted_"+final_file_name);
			if(oname.renameTo(nName))
			{
				System.out.println("renamed");
			} 
			else 
			{
				System.out.println("Error");
			} 
			e.printStackTrace();			
		}		
        return "abc";
	}
}