package com.michaelfmnk.aldrindocs.config;

import com.michaelfmnk.aldrindocs.properties.StorageProperties;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;

@Component
@CommonsLog
@AllArgsConstructor
public class StorageInitialization implements ApplicationListener<ApplicationPreparedEvent> {
    private final StorageProperties storageProperties;

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent applicationPreparedEvent) {
        try {
            Path tmpLocation = Paths.get(storageProperties.getTemporaryLocation());
            Path permanentLocation = Paths.get(storageProperties.getTemporaryLocation());

            if (!exists(tmpLocation)) {
                createDirectories(tmpLocation);
                log.info("creating temporary directory");
            }
            if (!exists(tmpLocation)) {
                createDirectories(permanentLocation);
                log.info("creating permanent directory");
            }
        } catch (IOException e) {
            log.error("couldn't create directory", e);
        }
    }
}
