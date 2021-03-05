package es.aarmenta.rom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	
		
	@Value("${source.roms.directory}")
	protected String sourceDir;
	
	@Value("${dest.filtered.roms.directory}")
	protected String destDir;
	
	@Value("${filter.regions}")
	protected String filterByRegions;
	
	@Value("${filter.regions.byorder}")
	protected String filterRegionsByOrder;
	
	@Value("${filter.nameparts}")
	protected String filterByNameParts;
}
