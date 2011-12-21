package com.habzy.video;

import java.io.FileDescriptor;

public class JNI {

	/**
	 * Adjust the file descriptor to seek-able?
	 * @param fd
	 * @return
	 */
	public native boolean adjustFD(FileDescriptor fd);
	
}
