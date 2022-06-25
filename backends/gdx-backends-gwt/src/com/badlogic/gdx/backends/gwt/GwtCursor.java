/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.gwt;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gwt.user.client.Window;

public class GwtCursor implements Cursor {
	String cssCursorProperty = "";

	public GwtCursor (Pixmap pixmap, int xHotspot, int yHotspot) {
		this(xHotspot, yHotspot, pixmap);
	}

	public GwtCursor (int xHotspot, int yHotspot, Pixmap... pixmaps) {
		if (pixmaps == null) {
			this.cssCursorProperty = "auto";
			return;
		}

		float[] scales = new float[pixmaps.length];

		StringBuilder cssCursorProperty = new StringBuilder();
		cssCursorProperty.toString();

		if (!imageSetCompatible()) {
			cssCursorProperty
				.append("url('")
				.append(pixmaps[0].getCanvasElement().toDataUrl("image/png"))
				.append("')")
				.append(xHotspot)
				.append(" ")
				.append(yHotspot)
				.append(", auto");
		} else {
			// Add vendor prefix for WebKit browsers (Chrome, Safari, Edge, etc.)
			if (GwtApplication.agentInfo().isSafari()) cssCursorProperty.append("-webkit-");
			cssCursorProperty
				.append("image-set(url('")
				.append(pixmaps[0].getCanvasElement().toDataUrl("image/png"))
				.append("') 1x");
		}

		pixmapChecks(pixmaps);
		hotspotChecks(xHotspot, yHotspot, pixmaps[0]);

		boolean firstPixmap = true;
		for (Pixmap pixmap : pixmaps) {

			if (firstPixmap) {
				firstPixmap = false;
			} else {
				scales[1] = (float) pixmap.getHeight() / pixmaps[0].getHeight();
			}

		}

	}

	static String getNameForSystemCursor (SystemCursor systemCursor) {
		if (systemCursor == SystemCursor.Arrow) {
			return "default";
		} else if (systemCursor == SystemCursor.Crosshair) {
			return "crosshair";
		} else if (systemCursor == SystemCursor.Hand) {
			return "pointer"; // Don't change to 'hand'; 'hand' is non-standard holdover from IE5
		} else if (systemCursor == SystemCursor.HorizontalResize) {
			return "ew-resize";
		} else if (systemCursor == SystemCursor.VerticalResize) {
			return "ns-resize";
		} else if (systemCursor == SystemCursor.Ibeam) {
			return "text";
		} else if (systemCursor == SystemCursor.NWSEResize) {
			return "nwse-resize";
		} else if (systemCursor == SystemCursor.NESWResize) {
			return "nesw-resize";
		} else if (systemCursor == SystemCursor.AllResize) {
			return "move";
		} else if (systemCursor == SystemCursor.NotAllowed) {
			return "not-allowed";
		} else if (systemCursor == SystemCursor.None) {
			return "none";
		} else {
			throw new GdxRuntimeException("Unknown system cursor " + systemCursor);
		}
	}

	@Override
	public void dispose () {
	}

	private void pixmapChecks(Pixmap[] pixmaps) {
		for (int i = 0; i < pixmaps.length; i++) {
			if (pixmaps[i].getFormat() != Pixmap.Format.RGBA8888)
				throw new GdxRuntimeException("Cursor image pixmap" + i + " is not in RGBA8888 format.");

			if (pixmaps[i].getWidth() < 1)
				throw new GdxRuntimeException(
					"Cursor image pixmap" + i + " width of " + pixmaps[i].getWidth() + " is not greater than zero.");

			if (pixmaps[i].getHeight() < 1)
				throw new GdxRuntimeException(
					"Cursor image pixmap" + i + " height of " + pixmaps[i].getHeight() + " is not greater than zero.");
		}
	}

	private void hotspotChecks(int xHotspot, int yHotspot, Pixmap pixmap) {
		if (xHotspot < 0 || xHotspot >= pixmap.getWidth())
			throw new GdxRuntimeException(
				"xHotspot coordinate of " + xHotspot + " is not within image width bounds: [0, " + (pixmap.getWidth() - 1) + "].");

		if (yHotspot < 0 || yHotspot >= pixmap.getHeight())
			throw new GdxRuntimeException(
				"yHotspot coordinate of " + yHotspot + " is not within image height bounds: [0, " + (pixmap.getHeight() - 1) + "].");
	}

	/** Detect if Firefox version is compatible with image-set.
	 * Other browsers have supported it long enough to be of little concern.
	 * @return True if compatible with CSS property image-set, including if -webkit- prefix is required.
	 */
	private static native boolean imageSetCompatible() /*-{
		let match = $wnd.navigator.userAgent.match(/Firefox\/([0-9]+)\./);
		return match !== null && match[1] >= 88;
	}-*/;
}
