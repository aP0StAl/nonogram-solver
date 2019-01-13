package com.apostal.nonogramsolver.config;

import com.apostal.nonogramsolver.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
public class AppConf {
    @Bean
    public MultiLayerNetwork loadNetwork() {
        MultiLayerNetwork network = null;
        try {
            File net = new File(Const.Paths.MODEL, Const.FileName.MODEL);
            network = ModelSerializer.restoreMultiLayerNetwork(net);
        } catch (IOException e) {
            log.error(Const.Pattern.LOG, Const.Messages.LOADING_NN_MODEL_ERROR, e.getMessage());
        }
        return network;
    }
}
