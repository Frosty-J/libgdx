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

package com.badlogic.gdx.backends.headless;

import com.badlogic.gdx.Files;

import java.util.Locale;

public class HeadlessApplicationConfiguration {
	/** The amount of updates targeted per second. Use 0 to never sleep;  negative to not call the render method at all.
	 *  Default is 60. */
	public int updatesPerSecond = 60;
	/** Preferences directory for headless. Default depends on operating system. */
	public String preferencesDirectory = getDefaultPreferencesDirectory();
	public Files.FileType preferencesFileType = getDefaultPreferencesFileType();
	public String preferencesBackupDirectory = ".prefs/";
	public Files.FileType preferencesBackupFileType = Files.FileType.External;

	/** The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}. */
	public int maxNetThreads = Integer.MAX_VALUE;

	private enum OperatingSystem {
		WINDOWS, MAC, LINUX, UNKNOWN
	}

	/**
	 * Returns where preferences are stored by default.
	 * Typically AppData/Roaming on Windows, Library/Preferences on macOS and .config on Linux.
	 * @return The default preferences directory.
	 */
	public String getDefaultPreferencesDirectory() {
		switch (getOS()) {

			case WINDOWS:
				String appdata = System.getenv("APPDATA");
				String windir = System.getenv("WINDIR");
				return (appdata != null) ? appdata // 2000/XP/Vista/7/8/10/11
					: (windir != null) ? windir + "/Application Data" // 95/98/Me
					: preferencesBackupDirectory; // Default to backup directory (probably ~/.prefs/) if it's broken

			case MAC:
				return "Library/Preferences";

			case LINUX:
				String configHome = System.getenv("XDG_CONFIG_HOME");
				if (configHome != null) {
					//TODO: Someone with regex skills should make this work with all environment variables
					if (configHome.contains("$HOME")) configHome = configHome.replace("$HOME", System.getenv("HOME"));
					else if (configHome.contains("$")) configHome = null;
				}
				return (configHome != null) ? configHome : ".config";

			default:
				return preferencesBackupDirectory;

		}
	}

	/**
	 * @return The default FileType for the operating system - External or Absolute.
	 */
	public Files.FileType getDefaultPreferencesFileType() {
		OperatingSystem os = getOS();
		switch (os) {
			case WINDOWS:
				return (System.getenv("APPDATA") != null || System.getenv("WINDIR") != null)
					? Files.FileType.Absolute : preferencesBackupFileType;
			case MAC: return Files.FileType.External;
			case LINUX: return (System.getenv("XDG_CONFIG_HOME") != null) ? Files.FileType.Absolute : Files.FileType.External;
		}
		return Files.FileType.External;
	}

	private OperatingSystem getOS() {
		String osName = System.getProperty("os.name").toLowerCase(Locale.US);
		if (osName.contains("windows")) return OperatingSystem.WINDOWS;
		else if (osName.contains("mac")) return OperatingSystem.MAC;
		else if (osName.contains("linux")) return OperatingSystem.LINUX;
		else return OperatingSystem.UNKNOWN;
	}

}
