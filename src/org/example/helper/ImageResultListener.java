/*
 * ImageResultListener is used by Image service to return images from gallery.
 */
package org.example.helper;

import java.util.ArrayList;

public interface ImageResultListener {

		public void imageAvailable(final ArrayList<ImageItem> images, int option);
}
