package com.mobile.scavenger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

public class Imagemanager {
	private final String PATH = "/data/data/com.helloandroid.imagedownloader/"; // put
																				// the
																				// downloaded
																				// file
																				// here

	public void DownloadFromUrl(String imageURL, String fileName) { // this is
																	// the
																	// downloader
																	// method
		try {
			URL url = new URL(
					"http://mobi-app-licious.com/scavangerapi/gcm_send_notification/images/10_100000408369681_1357287549.jpg"); // you
																																// can
																																// write
																																// here
																																// any
																																// link
			File file = new File(fileName);

			long startTime = System.currentTimeMillis();

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.close();

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}
}