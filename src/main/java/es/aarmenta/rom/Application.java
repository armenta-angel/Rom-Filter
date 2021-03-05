package es.aarmenta.rom;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationConfig appConfig;

	// --------------------------------------------------------- Public Methods

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {

		if (strings.length > 0 && strings[0].equalsIgnoreCase("regions")) {
			if (checkDirectory(appConfig.sourceDir, "Source dir")) {
				detectRegions();
			}
			return;
		}

		if (strings.length > 0 && strings[0].equalsIgnoreCase("metadata")) {
			if (checkDirectory(appConfig.sourceDir, "Source dir")) {
				detectMetadata();
			}
			return;
		}

		if (strings.length > 0 && strings[0].equalsIgnoreCase("filterByRegions")) {
			if (checkDirectory(appConfig.sourceDir, "Source dir")
					&& checkDirectory(appConfig.destDir, "Destination dir") && checkFilterByRegions()
					&& listFileNames().size() > 0) {
				if (appConfig.filterByRegions.length() > 0) {
					List<String> filterByRegionsList = Arrays.asList(appConfig.filterByRegions.split(";"));
					filterByRegion(filterByRegionsList);
				}
			}
			return;
		}

		if (strings.length > 0 && strings[0].equalsIgnoreCase("filterRegionsByOrder")) {
			if (checkDirectory(appConfig.sourceDir, "Source dir")
					&& checkDirectory(appConfig.destDir, "Destination dir") && checkFilterRegionsByOrder()
					&& listFileNames().size() > 0) {
				if (appConfig.filterRegionsByOrder.length() > 0) {
					List<String> filterRegionsByOrderList = Arrays.asList(appConfig.filterRegionsByOrder.split(";"));
					filterRegionsByOrder(filterRegionsByOrderList);
				}
			}
			return;
		}

		if (strings.length > 0 && strings[0].equalsIgnoreCase("filterByNameParts")) {
			if (checkDirectory(appConfig.sourceDir, "Source dir")
					&& checkDirectory(appConfig.destDir, "Destination dir") && checkFilterByNameParts()
					&& listFileNames().size() > 0) {
				if (appConfig.filterByNameParts.length() > 0) {
					List<String> filterByNamePartsList = Arrays.asList(appConfig.filterByNameParts.split(";"));
					filterByNameParts(filterByNamePartsList);
				}
			}
			return;
		}
	}
	// -------------------------------------------------------- Private Methods

	private List<String> detectRegions() {
		List<String> regions = new ArrayList<String>();
		List<File> filesWithoutRegion = new ArrayList<File>();

		File sourceDirFile = new File(appConfig.sourceDir);
		File list[] = sourceDirFile.listFiles(new IsFileFilter());
		if (list == null || list.length == 0) {
			log.warn("There are no files in the source directory");
		} else {
			for (File file : list) {
				log.debug(file.getName());
				int i1 = file.getName().indexOf('(');
				int i2 = file.getName().indexOf(')');

				if (i1 >= 0 && i2 >= 0 && i1 < i2) {
					String fileRegion = file.getName().substring(i1, i2 + 1);
					if (!regions.contains(fileRegion)) {
						regions.add(fileRegion);
					}
				} else {
					filesWithoutRegion.add(file);
				}
			}

			if (regions.size() > 0) {
				log.info("Detected " + regions.size() + " (possible) regions: " + regions);
			} else {
				log.info("No regions have been detected");
			}

			if (filesWithoutRegion.size() > 0) {
				log.info("Detected " + filesWithoutRegion.size() + " files without region: " + filesWithoutRegion);
			}
		}

		return regions;
	}

	private List<String> detectMetadata() {
		List<String> metadata = new ArrayList<String>();

		File sourceDirFile = new File(appConfig.sourceDir);
		File list[] = sourceDirFile.listFiles(new IsFileFilter());
		if (list == null || list.length == 0) {
			log.warn("There are no files in the source directory");
		} else {
			for (File file : list) {
				log.debug(file.getName());
				int i1 = file.getName().indexOf('(');
				int i2 = file.getName().indexOf(')');

				if (i1 >= 0 && i2 >= 0 && i1 < i2) {
					String region = file.getName().substring(i1, i2 + 1);
					log.debug("\t Region is " + region);

					String strMetadata = file.getName().substring(i2 + 1);
					while (strMetadata.indexOf('(') >= 0) {
						i1 = strMetadata.indexOf('(');
						i2 = strMetadata.indexOf(')');

						if (i1 >= 0 && i2 >= 0 && i1 < i2) {
							String theMetadata = strMetadata.substring(i1, i2 + 1);
							if (!metadata.contains(theMetadata)) {
								metadata.add(theMetadata);
								log.debug("\t" + metadata.get(metadata.size() - 1));
							}
						}

						strMetadata = strMetadata.substring(i2 + 1);
					}
				}
			}

			if (metadata.size() > 0) {
				log.info("Detected " + metadata.size() + " (possible) metadata: " + metadata);
			} else {
				log.info("No metadata have been detected");
			}
		}

		return metadata;
	}

	private boolean checkDirectory(String directory, String whichDir) {
		boolean checkIsOk = true;

		if (directory.length() > 0) {
			log.info(whichDir + ": " + directory);
			File dirFile = new File(directory);
			if (!dirFile.exists()) {
				log.warn(whichDir + " does not exists");
				checkIsOk = false;
			} else if (!dirFile.isDirectory()) {
				log.warn(whichDir + " is not a directory");
				checkIsOk = false;
			}
		} else {
			log.warn(whichDir + " has not been configured");
			checkIsOk = false;
		}
		return checkIsOk;
	}

	private boolean checkFilterByRegions() {
		boolean checkIsOk = true;

		if (appConfig.filterByRegions.length() > 0)
			log.info("Filter by regions is: " + appConfig.filterByRegions);
		else
			checkIsOk = false;

		return checkIsOk;
	}

	private boolean checkFilterRegionsByOrder() {
		boolean checkIsOk = true;

		if (appConfig.filterRegionsByOrder.length() > 0)
			log.info("Filter regions by order is: " + appConfig.filterRegionsByOrder);
		else
			checkIsOk = false;

		return checkIsOk;
	}

	private boolean checkFilterByNameParts() {
		boolean checkIsOk = true;

		if (appConfig.filterByNameParts.length() > 0)
			log.info("Filter by name parts is: " + appConfig.filterByNameParts);
		else
			checkIsOk = false;

		return checkIsOk;
	}

	private List<String> listFileNames() {
		List<String> theList = new ArrayList<String>();

		File sourceDirFile = new File(appConfig.sourceDir);
		File list[] = sourceDirFile.listFiles(new IsFileFilter());
		if (list == null || list.length == 0) {
			log.warn("There are no files in the source directory");
		} else {
			for (File file : list) {
				log.debug(file.getName());
				theList.add(file.getName());
			}
		}

		return theList;
	}

	private void filterByRegion(List<String> regions) {
		File sourceDirFile = new File(appConfig.sourceDir);
		String list[] = sourceDirFile.list(new RegionFilter(regions));
		if (list == null || list.length == 0) {
			log.warn("There are no files in the source directory matching the regions filter");
		} else {
			log.info("There are " + list.length + " files to filter by region. These files will be moved to "
					+ appConfig.destDir);
			moveFilesToFilteredDir(list);
		}
	}

	private void moveFilesToFilteredDir(String[] list) {
		File sourceFile = null;
		File destFile = null;

		for (int i = 0; i < list.length; i++) {
			sourceFile = new File(appConfig.sourceDir + "/" + list[i]);
			destFile = new File(appConfig.destDir + "/" + list[i]);

			log.debug("Moving " + list[i] + " to " + appConfig.destDir);

			sourceFile.renameTo(destFile);
		}
	}

	private void filterRegionsByOrder(List<String> filterByOrderList) {
		List<String> filesInSourceDir = listFileNames();
		List<String> groupToFilter = null;
		List<String> filesToFilter = new ArrayList<String>();

		Collections.sort(filesInSourceDir);

		int j = 0;
		for (int i = 0; i < filesInSourceDir.size(); i++) {
			if (i < j) {
				continue;
			}
			groupToFilter = new ArrayList<String>();
			String actualFilename = filesInSourceDir.get(i);
			j = i + 1;
			if (j < filesInSourceDir.size()) {
				String nextFilename = filesInSourceDir.get(j);
				boolean same = false;
				if (same = sameRom(actualFilename, nextFilename)) {
					groupToFilter.add(actualFilename);
					groupToFilter.add(nextFilename);
					j++;
				}

				while (same && j < filesInSourceDir.size()) {
					nextFilename = filesInSourceDir.get(j);
					if (same = sameRom(actualFilename, nextFilename)) {
						groupToFilter.add(nextFilename);
						j++;
					}
				}
			}

			if (groupToFilter.size() > 0) {
				log.info("Group found: " + groupToFilter);
				List<String> itemsToFilter = filterGroup(groupToFilter, filterByOrderList);
				if (itemsToFilter != null) {
					filesToFilter.addAll(itemsToFilter);
				}
			}
		}

		if (filesToFilter.size() > 0) {
			moveFilesToFilteredDir(filesToFilter.toArray(new String[filesToFilter.size()]));
		}
	}

	private List<String> filterGroup(List<String> groupToFilter, List<String> filterByOrderList) {
		int elementToKeep = -1;
		for (String orderedFilter : filterByOrderList) {
			for (int i = 0; elementToKeep < 0 && i < groupToFilter.size(); i++) {
				if (groupToFilter.get(i).indexOf(orderedFilter) >= 0) {
					elementToKeep = i;
					break;
				}
			}
			if (elementToKeep >= 0) {
				break;
			}
		}

		if (elementToKeep >= 0) {
			log.info("\tGroup filtered. File to keep is " + groupToFilter.get(elementToKeep));
			groupToFilter.remove(elementToKeep);
			return groupToFilter;
		} else {
			log.warn("\tCan not filter the group with the given filter by order. Group: " + groupToFilter);
			return null;
		}
	}

	private boolean sameRom(String fileName1, String fileName2) {
		int i1;
		int i2;

		if ((i1 = fileName1.indexOf('(')) > 0 && (i2 = fileName2.indexOf('(')) > 0) {
			String rom1 = fileName1.substring(0, i1);
			String rom2 = fileName2.substring(0, i2);

			return (rom1.compareTo(rom2) == 0);
		} else {
			return false;
		}
	}

	private void filterByNameParts(List<String> nameParts) {
		File sourceDirFile = new File(appConfig.sourceDir);
		String list[] = sourceDirFile.list(new NamePartFilter(nameParts));
		if (list == null || list.length == 0) {
			log.warn("There are no files in the source directory matching the namepart filter");
		} else {
			log.info("There are " + list.length + " files to filter by namepart. These files will be moved to "
					+ appConfig.destDir);
			moveFilesToFilteredDir(list);
		}
	}
}
