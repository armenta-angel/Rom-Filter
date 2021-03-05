package es.aarmenta.rom;

import java.io.File;

public class IsFileFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File pathname) {		
		return pathname.isFile();
	}

}
