package ctc.util;

import java.io.*;
import java.util.*;

/**
 * 此类主要提供一些常见的文件操作的方法
 * 
 * @author BalusC
 * @link http://balusc.blogspot.com/2007/09/FileUtil.html
 */
public final class FileUtil {

	 private static String separateSymbol = "/";
    // Init ---------------------------------------------------------------------------------------

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    // Constructors -------------------------------------------------------------------------------

    private FileUtil() {

    	if (System.getProperty("os.name").startsWith("Win"))
			separateSymbol = "\\" ;
		else
			separateSymbol = "/" ;
        // Utility class, hide constructor.
    }

    // Writers ------------------------------------------------------------------------------------

    /**
     * Write byte array to file. If file already exists, it will be overwritten.
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes) throws IOException {
        write(file, new ByteArrayInputStream(bytes), false);
    }

    /**
     * Write byte array to file with option to append to file or not. If not, then any existing
     * file will be overwritten.
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes, boolean append) throws IOException {
        write(file, new ByteArrayInputStream(bytes), append);
    }

    /**
     * Write byte inputstream to file. If file already exists, it will be overwritten.It's highly
     * recommended to feed the inputstream as BufferedInputStream or ByteArrayInputStream as those
     * are been automatically buffered.
     * @param file The file where the given byte inputstream have to be written to.
     * @param input The byte inputstream which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input) throws IOException {
        write(file, input, false);
    }

    /**
     * Write byte inputstream to file with option to append to file or not. If not, then any
     * existing file will be overwritten. It's highly recommended to feed the inputstream as
     * BufferedInputStream or ByteArrayInputStream as those are been automatically buffered.
     * @param file The file where the given byte inputstream have to be written to.
     * @param input The byte inputstream which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input, boolean append) throws IOException {
        mkdirs(file);
        BufferedOutputStream output = null;

        try {
            int contentLength = input.available();
            output = new BufferedOutputStream(new FileOutputStream(file, append));
            while (contentLength-- > 0) {
                output.write(input.read());
            }
        } finally {
            close(input, file);
            close(output, file);
        }
    }

    /**
     * Write character array to file. If file already exists, it will be overwritten.
     * @param file The file where the given character array have to be written to.
     * @param chars The character array which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars) throws IOException {
        write(file, new CharArrayReader(chars), false);
    }

    /**
     * Write character array to file with option to append to file or not. If not, then any
     * existing file will be overwritten.
     * @param file The file where the given character array have to be written to.
     * @param chars The character array which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars, boolean append) throws IOException {
        write(file, new CharArrayReader(chars), append);
    }

    /**
     * Write string value to file. If file already exists, it will be overwritten.
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), false);
    }

    /**
     * Write string value to file with option to append to file or not. If not, then any existing
     * file will be overwritten.
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string, boolean append) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), append);
    }

    /**
     * Write character reader to file. If file already exists, it will be overwritten. It's highly
     * recommended to feed the reader as BufferedReader or CharArrayReader as those are been
     * automatically buffered.
     * @param file The file where the given character reader have to be written to.
     * @param reader The character reader which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader) throws IOException {
        write(file, reader, false);
    }

    /**
     * Write character reader to file with option to append to file or not. If not, then any
     * existing file will be overwritten. It's highly recommended to feed the reader as
     * BufferedReader or CharArrayReader as those are been automatically buffered.
     * @param file The file where the given character reader have to be written to.
     * @param reader The character reader which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            int i = -1;
            while ((i = reader.read()) != -1) {
                writer.write(i);
            }
        } finally {
            close(reader, file);
            close(writer, file);
        }
    }

    /**
     * Write list of String records to file. If file already exists, it will be overwritten.
     * @param file The file where the given character reader have to be written to.
     * @param records The list of String records which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records) throws IOException {
        write(file, records, false);
    }

    /**
     * Write list of String records to file with option to append to file or not. If not, then any
     * existing file will be overwritten.
     * @param file The file where the given character reader have to be written to.
     * @param records The list of String records which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            for (String record : records) {
                writer.write(record);
                writer.write(LINE_SEPARATOR);
            }
        } finally {
            close(writer, file);
        }
    }

    // Readers ------------------------------------------------------------------------------------

    /**
     * Read byte array from file. Take care with big files, this would be memory hogging, rather
     * use readStream() instead.
     * @param file The file to read the byte array from.
     * @return The byte array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static byte[] readBytes(File file) throws IOException {
        BufferedInputStream stream = (BufferedInputStream) readStream(file);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return bytes;
    }

    /**
     * Read byte stream from file.
     * @param file The file to read the byte stream from.
     * @return The byte stream with the file contents (actually: BufferedInputStream).
     * @throws IOException If reading file fails.
     */
    public static InputStream readStream(File file) throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * Read character array from file. Take care with big files, this would be memory hogging,
     * rather use readReader() instead.
     * @param file The file to read the character array from.
     * @return The character array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static char[] readChars(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        return chars;
    }

    /**
     * Read string value from file. Take care with big files, this would be memory hogging, rather
     * use readReader() instead.
     * @param file The file to read the string value from.
     * @return The string value with the file contents.
     * @throws IOException If reading file fails.
     */
    public static String readString(File file) throws IOException {
        return new String(readChars(file));
    }

    /**
     * Read character reader from file.
     * @param file The file to read the character reader from.
     * @return The character reader with the file contents (actually: BufferedReader).
     * @throws IOException If reading file fails.
     */
    public static Reader readReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    /**
     * Read list of String records from file.
     * @param file The file to read the character writer from.
     * @return A list of String records which represents lines of the file contents.
     * @throws IOException If reading file fails.
     */
    public static List<String> readRecords(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        List<String> records = new ArrayList<String>();
        String record = null;

        try {
            while ((record = reader.readLine()) != null) {
                records.add(record);
            }
        } finally {
            close(reader, file);
        }

        return records;
    }

    // Copiers ------------------------------------------------------------------------------------

    /**
     * Copy file. Any existing file at the destination will be overwritten.
     * @param source The file to read the contents from.
     * @param destination The file to write the contents to.
     * @throws IOException If copying file fails.
     */
    public static void copy(File source, File destination) throws IOException {
        copy(source, destination, true);
    }

    /**
     * Copy file with the option to overwrite any existing file at the destination.
     * @param source The file to read the contents from.
     * @param destination The file to write the contents to.
     * @param overwrite Set whether to overwrite any existing file at the destination.
     * @throws IOException If the destination file already exists while <tt>overwrite</tt> is set
     * to false, or if copying file fails.
     */
    public static void copy(File source, File destination, boolean overwrite) throws IOException {
        if (destination.exists() && !overwrite) {
            throw new IOException(
                "Copying file " + source.getPath() + " to " + destination.getPath() + " failed."
                    + " The destination file already exists.");
        }

        mkdirs(destination);
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(source));
            output = new BufferedOutputStream(new FileOutputStream(destination));
            int contentLength = input.available();
            while (contentLength-- > 0) {
                output.write(input.read());
            }
        } finally {
            close(input, source);
            close(output, destination);
        }
    }

    // Movers -------------------------------------------------------------------------------------

    /**
     * Move (rename) file. Any existing file at the destination will be overwritten.
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @throws IOException If moving file fails.
     */
    public static void move(File source, File destination) throws IOException {
        move(source, destination, true);
    }

    /**
     * Move (rename) file with the option to overwrite any existing file at the destination.
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @param overwrite Set whether to overwrite any existing file at the destination.
     * @throws IOException If the destination file already exists while <tt>overwrite</tt> is set
     * to false, or if moving file fails.
     */
    public static void move(File source, File destination, boolean overwrite) throws IOException {
        if (destination.exists()) {
            if (overwrite) {
                destination.delete();
            } else {
                throw new IOException(
                    "Moving file " + source.getPath() + " to " + destination.getPath() + " failed."
                        + " The destination file already exists.");
            }
        }

        mkdirs(destination);

        if (!source.renameTo(destination)) {
            throw new IOException(
                "Moving file " + source.getPath() + " to " + destination.getPath() + " failed.");
        }
    }

    // Filenames ----------------------------------------------------------------------------------

    /**
     * Trim the eventual file path from the given file name. Anything before the last occurred "/"
     * and "\" will be trimmed, including the slash.
     * @param fileName The file name to trim the file path from.
     * @return The file name with the file path trimmed.
     */
    public static String trimFilePath(String fileName) {
        return fileName
            .substring(fileName.lastIndexOf("/") + 1)
            .substring(fileName.lastIndexOf("\\") + 1);
    }
    
    //重写已存在的文件
    public static File createFile(File filePath, String fileName) throws IOException {
    	File file = new File(filePath, fileName);
    	return file;
    }
    /**
     * Generate unique file based on the given path and name. If the file exists, then it will
     * add "[i]" to the file name as long as the file exists. The value of i can be between
     * 0 and 2147483647 (the value of Integer.MAX_VALUE).
     * @param filePath The path of the unique file.
     * @param fileName The name of the unique file.
     * @return The unique file.
     * @throws IOException If unique file cannot be generated, this can be caused if all file
     * names are already in use. You may consider another filename instead.
     * 不重写已存在的文件
     */
    public static File uniqueFile(File filePath, String fileName) throws IOException {
    	
    	File file = new File(filePath, fileName);

    	if (file.exists()) {

            // Split filename and add braces, e.g. "name.ext" --> "name[", "].ext".
            String prefix;
            String suffix;
            int dotIndex = fileName.lastIndexOf(".");

            if (dotIndex > -1) {
                prefix = fileName.substring(0, dotIndex) + "[";
                suffix = "]" + fileName.substring(dotIndex);
            } else {
                prefix = fileName + "[";
                suffix = "]";
            }

            int count = 0;

            // Add counter to filename as long as file exists.
            while (file.exists()) {
                if (count < 0) { // int++ restarts at -2147483648 after 2147483647.
                    throw new IOException("No unique filename available for " + fileName 
                        + " in path " + filePath.getPath() + ".");
                }

                // Glue counter between prefix and suffix, e.g. "name[" + count + "].ext".
                file = new File(filePath, prefix + (count++) + suffix);
            }
        }

        return file;
    }
    //保证中文文件名有效
    public static String ChineseCharacter(String fileName) throws IOException{
    	
    	String prefix;
        String suffix;
        int dotIndex = fileName.lastIndexOf(".");
    	
        suffix = fileName.substring(dotIndex + 1);
    	prefix = fileName.substring(0, dotIndex);
    	
    	prefix = new String(prefix.getBytes(),"gb2312");
    	suffix = new String(suffix.getBytes(),"gb2312");
    	
    	return prefix + "." + suffix;  
    	
    }
    // Helpers ------------------------------------------------------------------------------------

    /**
     * Check and create missing parent directories for the given file.
     * @param file The file to check and create the missing parent directories for.
     * @throws IOException If creating parent directories fails.
     */
    private static void mkdirs(File file) throws IOException {
        checkFile(file);
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Creating directories " + parentFile.getPath() + " failed.");
        }
    }
    
    /**
	* create file
	*  @param full path
	* @param filename
	* @throws IOException
	*/
	public static void createFile(String path,String filename){
		if (!(path.substring(path.length() - 1 )).equals(separateSymbol)){
			path = path + separateSymbol;
		}
		File file=new File(path + filename);
		try {
			mkdirs(file);
		} catch (IOException e) {
				System.out.println("ERROR:Create file fail.");
				//e.printStackTrace();
		}
	}

	/**
	* delete file
	* @param path
	* @param filename
	*/
	public static void delFile(String path,String filename){
		if (!(path.substring(path.length() - 1 )).equals(separateSymbol)){
			path = path + separateSymbol;
		}
		path = path +  filename;
		File file=new File(path);
		if(file.exists()&&file.isFile()){
			file.delete();
		}
	}
	
	  /** 强制删除文件
	  * try to delete given file , try 10 times 
	  * @param f 
	  * @return true if file deleted success, nor false; 
	  */  
	public static boolean forceDelete(String path,String filename)	{  
		if (!(path.substring(path.length() - 1 )).equals(separateSymbol)){
			path = path + separateSymbol;
		}
		path = path +  filename;
		File file = new File(path);
		
		boolean result = false;  
		int tryCount = 0;  
		while(!result && tryCount++ <10)  
		{  
			System.gc();  
			result = file.delete();  
		}  
		return result;  
	} 
	//判断某一文件是否存在,存在返回true,不存在返回false
	public static boolean checkFile(String filename){
		File f = new File(filename);
		if(f.exists()){
			return true;
		}
		return false;
		
	}
	java.io.File f = new java.io.File("test.txt");
	
    /**
     * Check if the given file is actually a file.
     * @param file The file object to be checked.
     * @throws IOException If the given file is actually not a file.
     */
    private static void checkFile(File file) throws IOException {
        if (file.exists() && !file.isFile()) {
            throw new IOException("File " + file.getPath() + " is actually not a file.");
        }
    }

    /**
     * Close the given inputstream of the given file.
     * @param input The inputstream to be closed.
     * @param file The inputstream's subject.
     */
    private static void close(InputStream input, File file) {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given outputstream of the given file.
     * @param input The outputstream to be closed.
     * @param file The outputstream's subject.
     */
    private static void close(OutputStream output, File file) {
        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given reader of the given file.
     * @param input The reader to be closed.
     * @param file The reader's subject.
     */
    private static void close(Reader reader, File file) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Close the given writer of the given file.
     * @param input The writer to be closed.
     * @param file The writer's subject.
     */
    private static void close(Writer writer, File file) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                closingFailed(file, e);
            }
        }
    }
    
    /**
     * Handle error when closing file failed.
     * @param file The file which isn't been closed.
     * @param ioException The IOException being thrown.
     */
    private static void closingFailed(File file, IOException e) {
        String message = "Closing file " + file.getPath() + " failed.";
        // Do your thing with the exception and the message. Print it, log it or mail it.
        System.err.println(message);
        e.printStackTrace();
    }
}