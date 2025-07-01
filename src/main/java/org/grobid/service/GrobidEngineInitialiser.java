package org.grobid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;
import org.grobid.core.lexicon.DatastetLexicon;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.main.LibraryLoader;
import org.grobid.core.utilities.DatastetConfiguration;
import org.grobid.core.utilities.GrobidConfig;
import org.grobid.core.utilities.GrobidConfig.ModelParameters;
import org.grobid.core.utilities.GrobidProperties;
import org.grobid.service.configuration.DatastetServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.lang.reflect.Field;

@Singleton
public class GrobidEngineInitialiser {
    private static final Logger LOGGER = LoggerFactory.getLogger(org.grobid.service.GrobidEngineInitialiser.class);

    @Inject
    public GrobidEngineInitialiser(DatastetServiceConfiguration configuration) {
        LOGGER.info("Initialising Grobid");
        GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(ImmutableList.of(configuration.getGrobidHome()));
        GrobidProperties.getInstance(grobidHomeFinder);
        DatastetLexicon.getInstance();

        DatastetConfiguration datastetConfiguration = null;
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            datastetConfiguration = mapper.readValue(new File("resources/config/config.yml").getAbsoluteFile(), DatastetConfiguration.class);
        } catch (Exception e) {
            LOGGER.error("The config file does not appear valid, see resources/config/config.yml", e);
            datastetConfiguration = null;
        }

        configuration.setDatastetConfiguration(datastetConfiguration);

        if (datastetConfiguration != null && datastetConfiguration.getModels() != null) {
            for (ModelParameters model : datastetConfiguration.getModels())
                GrobidProperties.getInstance().addModel(model);
        }

        Class<?> clazz = null; // if you know class name dynamically i.e. at runtime
        try {
            clazz = Class.forName("org.grobid.core.utilities.GrobidProperties");
            Field field = clazz.getDeclaredField("grobidConfig");
            field.setAccessible(true);
            GrobidConfig grobidConfig = (GrobidConfig) field.get("grobidConfig");
            grobidConfig.grobid.concurrency = configuration.getMaxParallelRequests();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("Invalid operation when hacking the GrobidProperties", e);
        }

        LibraryLoader.load();
    }
}
