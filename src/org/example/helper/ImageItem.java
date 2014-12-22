/*
 * Class to hold the image information
 */
package org.example.helper;

import android.graphics.Bitmap;

public class ImageItem {

		private Bitmap image;
		private String fileName;

		public ImageItem(Bitmap image, String imagePath) {
			super();
			this.image = image;
			this.fileName= imagePath;
		}

		public Bitmap getImage() {
			return image;
		}

		public void setImage(Bitmap image) {
			this.image = image;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
}
