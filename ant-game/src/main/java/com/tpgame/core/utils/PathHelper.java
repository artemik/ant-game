package com.tpgame.core.utils;

import javafx.application.HostServices;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;

/**
 * @author Artemik on 02.03.2015
 * @version $Id: $
 */
public class PathHelper {
    private static HostServices hostServices;

    public static String getAppRootFolder() {
        try {
            CodeSource codeSource = PathHelper.class.getProtectionDomain().getCodeSource();

            File jarFile;

            if (codeSource.getLocation() != null) {
                jarFile = new File(codeSource.getLocation().toURI());
            } else {
                String path = PathHelper.class.getResource(PathHelper.class.getSimpleName() + ".class").getPath();
                String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
                jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                jarFile = new File(jarFilePath);
            }

            return jarFile.getParentFile().getAbsolutePath();
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            throw new RuntimeException("Could not resolver app root folder", e);
        }
    }

    public static HostServices getHostServices() {
        return hostServices;
    }

    public static void setHostServices(HostServices hostServices) {
        PathHelper.hostServices = hostServices;
    }
}
