/*
 * ImageResultListener is used by Image service to return images from gallery.
 */
package org.example.tagproject;

import java.util.ArrayList;
import org.example.helper.ImageItem;

public interface ImageResultListener {

		public void imageAvailable(final ArrayList<ImageItem> images, int option);
}
