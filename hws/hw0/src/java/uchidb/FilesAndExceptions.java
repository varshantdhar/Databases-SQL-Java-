package uchidb;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.IndexOutOfBoundsException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FilesAndExceptions {


	/**
	 * Read a number of bytes from a file. Throw an exception is the read fails. DO NOT CLOSE THE FILE
	 * @param file The file to read from
	 * @param offset byte position to read from
	 * @param len number of bytes to read from the file
	 * @return byte[] of the bytes from the file
	 * @throws UChiDBException if file is not found, or if range is not valid
	 */
	public static byte[] readFile(File file, int offset, int len) throws UChiDBException {
		InputStream is = null;
		byte[] bytes = null;

		try {
			//TODO directly read the bytes from the file. do not read the entire file
<<<<<<< HEAD
			bytes = new byte[len];
			is = new FileInputStream(file);
			is.skip(offset);
			is.read(bytes, 0, len);
		} catch (FileNotFoundException e) {
			throw new UChiDBException();
		} catch (IndexOutOfBoundsException e) {
			throw new UChiDBException();
		}
		catch (IOException ex) {
			ex.printStackTrace();
=======
		} catch (IOException ex) {
>>>>>>> 10c552ee028e414121f116880ae5812048eb1905
			//TODO handle the file exceptions and throw appropriate exception
			//You may need to add additional exceptions
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			//finally will always execute, regardless if an exception is thrown
			//clean up resources here
			//TODO close is
		}
		return bytes;
	}



	/** Write bytes to a file. DO NOT CLOSE THE FILE.
	 * @param file
	 * @param bytes
	 * @throws UChiDBException If file does not exist or write fails
	 */
	public static void writeFile(File file, byte[] bytes) throws UChiDBException {
		OutputStream os = null;
		try {
			//TODO Write the file
			os = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			os.write(bytes);
			os.flush();
		} catch (IOException e) {
			//TODO handle the file exceptions and throw appropriate exception
			//You may need to add additional exceptions
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//finally will always execute, regardless if an exception is thrown
			//clean up resources here
			//TODO close the os
		}
	}

}
