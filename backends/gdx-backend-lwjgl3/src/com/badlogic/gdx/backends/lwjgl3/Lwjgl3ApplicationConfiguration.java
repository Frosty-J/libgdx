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

package com.badlogic.gdx.backends.lwjgl3;

import java.io.PrintStream;
import java.nio.IntBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics.Lwjgl3Monitor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;

public class Lwjgl3ApplicationConfiguration extends Lwjgl3WindowConfiguration {
	boolean disableAudio = false;

	/** The maximum number of threads to use for network requests. Default is {@link Integer#MAX_VALUE}. */
	int maxNetThreads = Integer.MAX_VALUE;

	int audioDeviceSimultaneousSources = 16;
	int audioDeviceBufferSize = 512;
	int audioDeviceBufferCount = 9;

	boolean useGL30 = false;
	int gles30ContextMajorVersion = 3;
	int gles30ContextMinorVersion = 2;

	int r = 8, g = 8, b = 8, a = 8;
	int depth = 16, stencil = 0;
	int samples = 0;
	boolean transparentFramebuffer;

	int idleFPS = 60;
	int foregroundFPS = 0;

	String preferencesDirectory = getDefaultPreferencesDirectory();
	Files.FileType preferencesFileType = getDefaultPreferencesFileType();
	boolean allowLegacyPreferences = true;

	HdpiMode hdpiMode = HdpiMode.Logical;

	boolean debug = false;
	PrintStream debugStream = System.err;
	
	static Lwjgl3ApplicationConfiguration copy(Lwjgl3ApplicationConfiguration config) {
		Lwjgl3ApplicationConfiguration copy = new Lwjgl3ApplicationConfiguration();
		copy.set(config);
		return copy;
	}
	
	void set (Lwjgl3ApplicationConfiguration config){
		super.setWindowConfiguration(config);
		disableAudio = config.disableAudio;
		audioDeviceSimultaneousSources = config.audioDeviceSimultaneousSources;
		audioDeviceBufferSize = config.audioDeviceBufferSize;
		audioDeviceBufferCount = config.audioDeviceBufferCount;
		useGL30 = config.useGL30;
		gles30ContextMajorVersion = config.gles30ContextMajorVersion;
		gles30ContextMinorVersion = config.gles30ContextMinorVersion;
		r = config.r;
		g = config.g;
		b = config.b;
		a = config.a;
		depth = config.depth;
		stencil = config.stencil;
		samples = config.samples;
		transparentFramebuffer = config.transparentFramebuffer;
		idleFPS = config.idleFPS;
		foregroundFPS = config.foregroundFPS;
		preferencesDirectory = config.preferencesDirectory;
		preferencesFileType = config.preferencesFileType;
		allowLegacyPreferences = config.allowLegacyPreferences;
		hdpiMode = config.hdpiMode;
		debug = config.debug;
		debugStream = config.debugStream;
	}
	
	/**
	 * @param visibility whether the window will be visible on creation. (default true)
	 */
	public void setInitialVisible(boolean visibility) {
		this.initialVisible = visibility;
	}

	/**
	 * Whether to disable audio or not. If set to false, the returned audio
	 * class instances like {@link Audio} or {@link Music} will be mock
	 * implementations.
	 */
	public void disableAudio(boolean disableAudio) {
		this.disableAudio = disableAudio;
	}

	/**
	 * Sets the maximum number of threads to use for network requests.
	 */
	public void setMaxNetThreads(int maxNetThreads) {
		this.maxNetThreads = maxNetThreads;
	}

	/**
	 * Sets the audio device configuration.
	 * 
	 * @param simultaniousSources
	 *            the maximum number of sources that can be played
	 *            simultaniously (default 16)
	 * @param bufferSize
	 *            the audio device buffer size in samples (default 512)
	 * @param bufferCount
	 *            the audio device buffer count (default 9)
	 */
	public void setAudioConfig(int simultaniousSources, int bufferSize, int bufferCount) {
		this.audioDeviceSimultaneousSources = simultaniousSources;
		this.audioDeviceBufferSize = bufferSize;
		this.audioDeviceBufferCount = bufferCount;
	}

	/**
	 * Sets whether to use OpenGL ES 3.0 emulation. If the given major/minor
	 * version is not supported, the backend falls back to OpenGL ES 2.0
	 * emulation. The default parameters for major and minor should be 3 and 2
	 * respectively to be compatible with Mac OS X. Specifying major version 4
	 * and minor version 2 will ensure that all OpenGL ES 3.0 features are
	 * supported. Note however that Mac OS X does only support 3.2.
	 * 
	 * @see <a href=
	 *      "http://legacy.lwjgl.org/javadoc/org/lwjgl/opengl/ContextAttribs.html">
	 *      LWJGL OSX ContextAttribs note</a>
	 * 
	 * @param useGL30
	 *            whether to use OpenGL ES 3.0
	 * @param gles3MajorVersion
	 *            OpenGL ES major version, use 3 as default
	 * @param gles3MinorVersion
	 *            OpenGL ES minor version, use 2 as default
	 */
	public void useOpenGL3(boolean useGL30, int gles3MajorVersion, int gles3MinorVersion) {
		this.useGL30 = useGL30;
		this.gles30ContextMajorVersion = gles3MajorVersion;
		this.gles30ContextMinorVersion = gles3MinorVersion;
	}

	/**
	 * Sets the bit depth of the color, depth and stencil buffer as well as
	 * multi-sampling.
	 * 
	 * @param r
	 *            red bits (default 8)
	 * @param g
	 *            green bits (default 8)
	 * @param b
	 *            blue bits (default 8)
	 * @param a
	 *            alpha bits (default 8)
	 * @param depth
	 *            depth bits (default 16)
	 * @param stencil
	 *            stencil bits (default 0)
	 * @param samples
	 *            MSAA samples (default 0)
	 */
	public void setBackBufferConfig(int r, int g, int b, int a, int depth, int stencil, int samples) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.depth = depth;
		this.stencil = stencil;
		this.samples = samples;
	}

	/**
	 * Set transparent window hint
	 * @deprecated Results may vary on different OS and GPUs. See https://github.com/glfw/glfw/issues/1237
	 * @param transparentFramebuffer
	 */
	@Deprecated
	public void setTransparentFramebuffer (boolean transparentFramebuffer) {
		this.transparentFramebuffer = transparentFramebuffer;
	}

	/**Sets the polling rate during idle time in non-continuous rendering mode. Must be positive.
	 * Default is 60. */
	public void setIdleFPS (int fps) {
		this.idleFPS = fps;
	}

	/**Sets the target framerate for the application. The CPU sleeps as needed. Must be positive.
	 * Use 0 to never sleep. Default is 0. */
	public void setForegroundFPS (int fps) {
		this.foregroundFPS = fps;
	}

	/**
	 * Sets the directory where {@link Preferences} will be stored, as well as
	 * the file type to be used to store them. Default varies by operating system.
	 */
	public void setPreferencesConfig(String directory, Files.FileType fileType) {
		this.preferencesDirectory = directory;
		this.preferencesFileType = fileType;
	}

	/** Sets the directory where {@link Preferences} will be stored, as well as
	 * the file type to be used to store them. Default varies by operating system.
	 * Also sets whether .prefs should be checked for preferences (default location prior to libGDX 1.10.1). */
	public void setPreferencesConfig(String directory, Files.FileType fileType, boolean allowLegacy) {
		this.preferencesDirectory = directory;
		this.preferencesFileType = fileType;
		this.allowLegacyPreferences = allowLegacy;
	}

	/** Sets whether .prefs should be checked for preferences (default location prior to libGDX 1.10.1). */
	public void setPreferencesConfig(boolean allowLegacy) {
		this.allowLegacyPreferences = allowLegacy;
	}

	/**
	 * Defines how HDPI monitors are handled. Operating systems may have a
	 * per-monitor HDPI scale setting. The operating system may report window
	 * width/height and mouse coordinates in a logical coordinate system at a
	 * lower resolution than the actual physical resolution. This setting allows
	 * you to specify whether you want to work in logical or raw pixel units.
	 * See {@link HdpiMode} for more information. Note that some OpenGL
	 * functions like {@link GL20#glViewport(int, int, int, int)} and
	 * {@link GL20#glScissor(int, int, int, int)} require raw pixel units. Use
	 * {@link HdpiUtils} to help with the conversion if HdpiMode is set to
	 * {@link HdpiMode#Logical}. Defaults to {@link HdpiMode#Logical}.
	 */
	public void setHdpiMode(HdpiMode mode) {
		this.hdpiMode = mode;
	}

	/**
	 * Enables use of OpenGL debug message callbacks. If not supported by the core GL driver
	 * (since GL 4.3), this uses the KHR_debug, ARB_debug_output or AMD_debug_output extension
	 * if available. By default, debug messages with NOTIFICATION severity are disabled to
	 * avoid log spam.
	 *
	 * You can call with {@link System#err} to output to the "standard" error output stream.
	 *
	 * Use {@link Lwjgl3Application#setGLDebugMessageControl(Lwjgl3Application.GLDebugMessageSeverity, boolean)}
	 * to enable or disable other severity debug levels.
	 */
	public void enableGLDebugOutput(boolean enable, PrintStream debugOutputStream) {
		debug = enable;
		debugStream = debugOutputStream;
	}

	/**
	 * @return the currently active {@link DisplayMode} of the primary monitor
	 */
	public static DisplayMode getDisplayMode() {
		Lwjgl3Application.initializeGlfw();
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		return new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(), videoMode.refreshRate(),
				videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
	}

	/**
	 * @return the currently active {@link DisplayMode} of the given monitor
	 */
	public static DisplayMode getDisplayMode(Monitor monitor) {
		Lwjgl3Application.initializeGlfw();
		GLFWVidMode videoMode = GLFW.glfwGetVideoMode(((Lwjgl3Monitor)monitor).monitorHandle);
		return new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Monitor)monitor).monitorHandle, videoMode.width(), videoMode.height(), videoMode.refreshRate(),
				videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
	}

	/**
	 * @return the available {@link DisplayMode}s of the primary monitor
	 */
	public static DisplayMode[] getDisplayModes() {
		Lwjgl3Application.initializeGlfw(); 
		Buffer videoModes = GLFW.glfwGetVideoModes(GLFW.glfwGetPrimaryMonitor());
		DisplayMode[] result = new DisplayMode[videoModes.limit()];
		for (int i = 0; i < result.length; i++) {
			GLFWVidMode videoMode = videoModes.get(i);
			result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(GLFW.glfwGetPrimaryMonitor(), videoMode.width(), videoMode.height(),
					videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
		}
		return result;
	}

	/**
	 * @return the available {@link DisplayMode}s of the given {@link Monitor}
	 */
	public static DisplayMode[] getDisplayModes(Monitor monitor) {
		Lwjgl3Application.initializeGlfw();
		Buffer videoModes = GLFW.glfwGetVideoModes(((Lwjgl3Monitor)monitor).monitorHandle);
		DisplayMode[] result = new DisplayMode[videoModes.limit()];
		for (int i = 0; i < result.length; i++) {
			GLFWVidMode videoMode = videoModes.get(i);
			result[i] = new Lwjgl3Graphics.Lwjgl3DisplayMode(((Lwjgl3Monitor)monitor).monitorHandle, videoMode.width(), videoMode.height(),
					videoMode.refreshRate(), videoMode.redBits() + videoMode.greenBits() + videoMode.blueBits());
		}
		return result;
	}

	/**
	 * @return the primary {@link Monitor}
	 */
	public static Monitor getPrimaryMonitor() {
		Lwjgl3Application.initializeGlfw();
		return toLwjgl3Monitor(GLFW.glfwGetPrimaryMonitor());
	}

	/**
	 * @return the connected {@link Monitor}s
	 */
	public static Monitor[] getMonitors() {
		Lwjgl3Application.initializeGlfw();
		PointerBuffer glfwMonitors = GLFW.glfwGetMonitors();
		Monitor[] monitors = new Monitor[glfwMonitors.limit()];
		for (int i = 0; i < glfwMonitors.limit(); i++) {
			monitors[i] = toLwjgl3Monitor(glfwMonitors.get(i));
		}
		return monitors;
	}

	static Lwjgl3Monitor toLwjgl3Monitor(long glfwMonitor) {
		IntBuffer tmp = BufferUtils.createIntBuffer(1);
		IntBuffer tmp2 = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetMonitorPos(glfwMonitor, tmp, tmp2);
		int virtualX = tmp.get(0);
		int virtualY = tmp2.get(0);
		String name = GLFW.glfwGetMonitorName(glfwMonitor);
		return new Lwjgl3Monitor(glfwMonitor, virtualX, virtualY, name);
	}

	/**
	 * Returns where preferences are stored by default.
	 * Typically AppData/Roaming on Windows, Library/Preferences on macOS and .config on Linux.
	 * @return The default preferences directory.
	 */
	public String getDefaultPreferencesDirectory() {

		if (UIUtils.isWindows) {
			String appdata = System.getenv("APPDATA");
			String windir = System.getenv("WINDIR");
			return appdata != null ? appdata // 2000/XP/Vista/7/8/10/11
				: windir != null ? windir + "/Application Data" // 95/98/Me
				: ".prefs"; // Default to legacy directory if it's broken

		} else if (UIUtils.isMac) {
			return "Library/Preferences";

		} else if (UIUtils.isLinux) {
			String configHome = System.getenv("XDG_CONFIG_HOME");
			if (configHome != null) {
				Pattern p = Pattern.compile("(?<!\\\\)\\$(\\w+)");
				Matcher m = p.matcher(configHome);
				while(m.find()) {
					m.reset(configHome = configHome.replaceFirst("\\Q" + m.group() + "\\E",
						Matcher.quoteReplacement(String.valueOf(System.getenv(m.group(1))))));
				}
			}
			return configHome != null ? configHome : ".config";

		} else return ".prefs";

	}

	/**
	 * @return The default FileType for the operating system - External or Absolute.
	 */
	public Files.FileType getDefaultPreferencesFileType() {

		if (UIUtils.isWindows) {
			return (System.getenv("APPDATA") != null || System.getenv("WINDIR") != null)
				? Files.FileType.Absolute : FileType.External;

		} else if (UIUtils.isMac) {
			return Files.FileType.External;

		} else if (UIUtils.isLinux) {
			return System.getenv("XDG_CONFIG_HOME") != null ? Files.FileType.Absolute : Files.FileType.External;

		} else return Files.FileType.External;

	}

}