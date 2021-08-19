package com.obezhik.multiply_file_picker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;

import androidx.annotation.WorkerThread;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {

	@WorkerThread
	public static void from(File srcFile, File destFile) throws IOException {
		try (
			InputStream in = new FileInputStream(srcFile);
			OutputStream out = new FileOutputStream(destFile)
		) {
			if (Build.VERSION.SDK_INT > 25) {
				IOUtils.copyLarge(in, out);
			} else {
				try {
					int c;
					while ((c = in.read()) != -1) {
						out.write(c);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@WorkerThread
	public static void from(Context context, Uri srcUri, File destFile) throws IOException {
		try (
			InputStream in = context.getContentResolver().openInputStream(srcUri);
			OutputStream out = new FileOutputStream(destFile)
		) {
			if (Build.VERSION.SDK_INT > 25) {
				IOUtils.copyLarge(in, out);
			} else {
				try {
					int c;
					while ((c = in.read()) != -1) {
						out.write(c);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@WorkerThread
	public static File from(Context context, Uri uri) throws IOException {
		String fileName = getFileName(context, uri);
		String[] splitName = splitFileName(fileName);
		File tempFile = File.createTempFile(splitName[0], splitName[1]);
		tempFile = rename(tempFile, fileName);
		tempFile.deleteOnExit();

		from(context, uri, tempFile);

		return tempFile;
	}

	@WorkerThread
	public static File from(File file) throws IOException {
		String fileName = file.getName();
		String[] splitName = splitFileName(fileName);
		File tempFile = File.createTempFile(splitName[0], splitName[1]);
		tempFile = rename(tempFile, fileName);
		tempFile.deleteOnExit();

		from(file, tempFile);

		return tempFile;
	}

	@WorkerThread
	public static ArrayList<File> from(Context context, List<Uri> uris) throws IOException {
		ArrayList<File> result = new ArrayList<>();

		for (Uri uri : uris) {
			result.add(from(context, uri));
		}

		return result;
	}

	public static File createTempFile(String fileName) throws IOException {
		String[] splitName = splitFileName(fileName);
		File tempFile = File.createTempFile(splitName[0], splitName[1]);
		tempFile = rename(tempFile, fileName);
		tempFile.deleteOnExit();

		return tempFile;
	}

	private static String[] splitFileName(String fileName) {
		String name = fileName;
		String extension = "";
		int i = fileName.lastIndexOf(".");
		if (i != -1) {
			name = fileName.substring(0, i);
			extension = fileName.substring(i);
		}

		return new String[]{name, extension};
	}

	private static String getFileName(Context context, Uri uri) {
		String result = null;
		if (uri.getScheme().equals("content")) {
			try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} catch (Exception ignore) {
			}
		}
		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf(File.separator);
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static File rename(File file, String newName) {
		File newFile = new File(file.getParent(), newName);
		if (!newFile.equals(file)) {
			if (newFile.exists()) {
				newFile.delete();
			}
			file.renameTo(newFile);
		}
		return newFile;
	}

}
