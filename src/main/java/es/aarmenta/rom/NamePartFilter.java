package es.aarmenta.rom;

import java.io.File;
import java.util.List;

public class NamePartFilter implements java.io.FilenameFilter {

	private List<String> nameParts;

	public NamePartFilter(List<String> namePartsList) {
		super();
		this.nameParts = namePartsList;
	}

	@Override
	public boolean accept(File dir, String name) {
		boolean partOfTheName = false;

		for (String aNamePart : nameParts) {
			partOfTheName = name.indexOf(aNamePart) >= 0;
			if (partOfTheName)
				break;
		}

		return partOfTheName;
	}

}
