package com.apostal.nonogramsolver;

import com.apostal.nonogramsolver.ui.controller.MainFrameController;
import com.apostal.nonogramsolver.util.LookAndFeelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static com.apostal.nonogramsolver.util.OpenCVUtils.loadOpenCV_Lib;

@Slf4j
@SpringBootApplication
public class SwingApplication {
    public static void main(String[] args) throws Exception {
		LookAndFeelUtils.setWindowsLookAndFeel();
		ConfigurableApplicationContext context = new SpringApplicationBuilder(SwingApplication.class).headless(false).run(args);
		MainFrameController mainFrameController = context.getBean(MainFrameController.class);
		mainFrameController.prepareAndOpenFrame();
		loadOpenCV_Lib();
	}

}

