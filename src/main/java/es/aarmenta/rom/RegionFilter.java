package es.aarmenta.rom;

import java.io.File;
import java.util.List;

public class RegionFilter implements java.io.FilenameFilter {

	private List<String> regions;

	public RegionFilter(List<String> regionList) {
		super();
		this.regions = regionList;
	}

	@Override
	public boolean accept(File dir, String name) {
		boolean regionMatches = false;

		File fileToCheck = new File(dir.getAbsolutePath() + "/" + name);
		if (fileToCheck.isFile()) {

			int i1 = name.indexOf('(');
			int i2 = name.indexOf(')');

			if (i1 >= 0 && i2 >= 0 && i1 < i2) {
				String fileRegion = name.substring(i1, i2 + 1);
				for (String aRegion : regions) {

					regionMatches = fileRegion.equals(aRegion);
					if (regionMatches)
						break;
				}
			}
		}

		return regionMatches;
	}

}
