package io.github.spyfcc.core.support;

import java.io.InputStream;
import java.util.Properties;

public class SpyBanner {
    public static String getVersion() {
        try (InputStream is = SpyBanner.class
                .getClassLoader()
                .getResourceAsStream("META-INF/build-info.properties")) {

            if (is == null) {
                return "unknown";
            }

            Properties props = new Properties();
            props.load(is);

            return props.getProperty("build.version", "unknown");

        } catch (Exception e) {
            return "unknown";
        }
    }
	
	private static final String BANNER ="\n"+
">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+"\n"+
"			 __    __     ________________________.___.   __    __              "+ "\n"+ 
"			 ╲ ╲   ╲ ╲   ╱   _____╱╲______   ╲__  │   │  ╱ ╱   ╱ ╱              "+ "\n"+ 
"			  ╲ ╲   ╲ ╲  ╲_____  ╲  │     ___╱╱   │   │ ╱ ╱   ╱ ╱  				"+ "\n"+
"			  ╱ ╱   ╱ ╱  ╱        ╲ │    │    ╲____   │ ╲ ╲   ╲ ╲  				"+ "\n"+
"			 ╱_╱   ╱_╱  ╱_______  ╱ │____│    ╱ ______│  ╲_╲   ╲_╲ 				"+ "\n"+
"			                    ╲╱            ╲╱                   				"+ "\n"+
">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>SPY AGENT ONLINE<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<";
	
	public static void print() {
		System.out.println(
			    "\u001B[32m" + BANNER + 
			    "\nV: " + "\u001B[35m" + getVersion() + 
			    "\u001B[0m"
			);
	}
}
