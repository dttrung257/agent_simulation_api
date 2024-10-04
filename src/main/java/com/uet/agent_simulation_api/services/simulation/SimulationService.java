package com.uet.agent_simulation_api.services.simulation;

import com.uet.agent_simulation_api.commands.builders.IGamaCommandBuilder;
import com.uet.agent_simulation_api.commands.executors.IGamaCommandExecutor;
import com.uet.agent_simulation_api.constant.ExperimentResultStatusConst;
import com.uet.agent_simulation_api.constant.SimulationConst;
import com.uet.agent_simulation_api.exceptions.errors.ExperimentErrors;
import com.uet.agent_simulation_api.exceptions.errors.ModelErrors;
import com.uet.agent_simulation_api.exceptions.errors.ProjectErrors;
import com.uet.agent_simulation_api.exceptions.experiment.ExperimentNotFoundException;
import com.uet.agent_simulation_api.exceptions.model.ModelNotFoundException;
import com.uet.agent_simulation_api.exceptions.simulation.CannotClearOldSimulationOutputException;
import com.uet.agent_simulation_api.exceptions.project.ProjectNotFoundException;
import com.uet.agent_simulation_api.models.*;
import com.uet.agent_simulation_api.repositories.*;
import com.uet.agent_simulation_api.requests.simulation.CreateSimulationRequest;
import com.uet.agent_simulation_api.services.auth.IAuthService;
import com.uet.agent_simulation_api.services.image.IImageService;
import com.uet.agent_simulation_api.services.s3.IS3Service;
import com.uet.agent_simulation_api.utils.ConvertUtil;
import com.uet.agent_simulation_api.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This service is used to handle simulation logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SimulationService implements ISimulationService {
    private final TimeUtil timeUtil;
    private final IS3Service s3Service;
    private final ConvertUtil convertUtil;
    private final IAuthService authService;
    private final IImageService imageService;
    private final ModelRepository modelRepository;
    private final ProjectRepository projectRepository;
    private final ExecutorService virtualThreadExecutor;
    private final IGamaCommandBuilder gamaCommandBuilder;
    private final IGamaCommandExecutor gamaCommandExecutor;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultRepository experimentResultRepository;
    private final ExperimentResultImageRepository experimentResultImageRepository;
    private final ExperimentResultStatusRepository experimentResultStatusRepository;
    private final ExperimentResultCategoryRepository experimentResultCategoryRepository;

    @Override
    public void run() {
        final var start = timeUtil.getCurrentTime();
        log.info("Simulation started at {}", timeUtil.getCurrentTimeString());

        final var processBuilder = new ProcessBuilder();

        final var command = gamaCommandBuilder.buildLegacy(
                Map.of(
                        "-hpc", "8",
                        "-m", "8"
                ),
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/sml-01.xml",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/test_output/"
        );
        /*
        final String batchCommand = gamaCommandBuilder.buildBatch(
                Map.of(
                        "-hpc", "2",
                        "-m", "8"
                ),
                "Normal",
                "/Users/trungdt/Workspaces/uet/gama/pig-farm/models/simulator-01.gaml"
        );
        */

        try {
            log.info("Clear old output directory");
            processBuilder.command("bash", "-c", "rm -rf /Users/trungdt/Workspaces/uet/gama/pig-farm/includes/output/normal/**").start();

            log.info("Running command: {}", command);
            final var process = processBuilder.command("bash", "-c", command).start();

            final var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Simulation ended at {}, took {} seconds",
                timeUtil.getCurrentTimeString(),
                (timeUtil.getCurrentTime() - start) * 1.0 / 1000
        );
    }

    @Override
    @Transactional(rollbackFor = { Exception.class, Throwable.class })
    public void run(CreateSimulationRequest request) {
        // Run simulation
        runSimulation(request);
    }

    /**
     * This method is used to run simulation.
     *
     * @param request CreateSimulationRequest
     */
    private void runSimulation(CreateSimulationRequest request) {
        final var userId = authService.getCurrentUserId();
        request = calculateExperimentResultId(request);
        final var projectId = request.getProjectId();
        final var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(ProjectErrors.E_PJ_0001.defaultMessage()));

        final var projectLocation = project.getLocation();

        request.getExperiments().forEach(experimentReq -> {
            // Get model
            final var model = modelRepository.findByModeIdAndProjectId(experimentReq.getModelId(), projectId)
                    .orElseThrow(() -> new ModelNotFoundException(ModelErrors.E_MODEL_0001.defaultMessage()));

            // Get experiment
            final var experiment = experimentRepository
                    .findByExperimentIdAndModelId(experimentReq.getId(), experimentReq.getModelId())
                    .orElseThrow(() -> new ExperimentNotFoundException(ExperimentErrors.E_EXP_0001.defaultMessage()));

            experimentReq.setGamlFile(model.getName() + ".gaml");
            experimentReq.setExperiment(experiment);
        });

        request.getExperiments().forEach(experimentReq -> {
            // Get experiment data from request.
            final var modelId = experimentReq.getModelId();
            final var experimentId = experimentReq.getId();
            final var gamlFile = experimentReq.getGamlFile();
            final var finalStep = experimentReq.getFinalStep() != null ? experimentReq.getFinalStep() : SimulationConst.DEFAULT_FINAL_STEP;
            final var experimentResultId = experimentReq.getExperimentResultId();
            final var gamlFileName = gamlFile.replace(".gaml", "");
            final var experimentName = experimentReq.getExperiment().getName();
            final var pathToXmlFile = "storage/xmls/user-" + userId + "_" + "project-" + projectId + "_" + "model-" +
                    modelId + "_" + "experiment-" + experimentId + "_" + experimentResultId + "_" + gamlFileName +
                    "-" + experimentName + ".xml";
            final var localOutputDir = "storage/outputs/user-" + userId + "_" + "project-" + projectId + "_" +
                    "model-" + modelId + "_" + "experiment-" + experimentId + "_" + experimentResultId + "_" +
                    gamlFileName + "-" + experimentName;

            // Clear old output directory
            clearLocalResource(localOutputDir);

            // Clear old xml file
            clearLocalResource(pathToXmlFile);

            // Create experiment plan XML file.
            final var createXmlCommand = gamaCommandBuilder.createXmlFile(
                    experimentName,  projectLocation + "/models/" + gamlFile, pathToXmlFile);

            // Build command to run experiment.
            final var runLegacyCommand = gamaCommandBuilder.buildLegacy(null, pathToXmlFile, localOutputDir);

            // Execute legacy command.
            executeLegacy(createXmlCommand, runLegacyCommand, pathToXmlFile, projectId, modelId,
                    experimentReq.getExperiment(), experimentResultId, finalStep, localOutputDir);
        });
    }

    /**
     * This method is used to calculate experiment result id.
     *
     * @param request CreateSimulationRequest
     * @return CreateSimulationRequest
     */
    private CreateSimulationRequest calculateExperimentResultId(CreateSimulationRequest request) {
        final var userId = authService.getCurrentUserId();
        final Map<String, AtomicInteger> idCounters = new HashMap<>();

        request.getExperiments().forEach(experiment -> {
            final var modelId = experiment.getModelId();
            final var experimentId = experiment.getId();
            final String key = userId + "_" + modelId + "_" + experimentId;

            final AtomicInteger counter = idCounters.computeIfAbsent(key, value -> new AtomicInteger(1));
            experiment.setExperimentResultId(counter.getAndIncrement());
        });

        return request;
    }

    /**
     * This method is used to run job execute legacy command.
     *
     * @param createXmlCommand String
     * @param runLegacyCommand String
     * @param pathToXmlFile String
     * @param projectId BigInteger
     * @param modelId BigInteger
     * @param experiment Experiment
     * @param finalStep long
     * @param localOutputDir String
     */
    private void executeLegacy(String createXmlCommand, String runLegacyCommand, String pathToXmlFile, BigInteger projectId,
       BigInteger modelId, Experiment experiment, int experimentResultId, long finalStep, String localOutputDir) {
        try {
            final var experimentId = experiment.getId();
            final var experimentName = experiment.getName();
            final ExperimentResultStatus experimentResultStatus = createExperimentResultStatus(experimentId);

            virtualThreadExecutor.submit(() -> {
                // Execute commands
                final var future = gamaCommandExecutor.executeLegacy(createXmlCommand, runLegacyCommand, pathToXmlFile,
                        experimentId, experimentName, finalStep);

                future.whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Error while running simulation", error);

                        clearLocalResource(localOutputDir);
                        clearLocalResource(pathToXmlFile);
                        updateExperimentResultStatus(experimentResultStatus, ExperimentResultStatusConst.FAILED);
                        return;
                    }

                    log.info("Simulation completed for experiment: {}", experimentId);

                    // If execution is successful, upload output directory to S3
//                    clearOldS3Result(projectId, modelId, experimentId, experimentResultId);
//                    uploadResult(projectId, modelId, experimentId, experimentResultId, localOutputDir);
//                    clearLocalResource(localOutputDir);
                    clearLocalResource(pathToXmlFile);

                    // Save result to database.
//                    final var experimentResultKey = getOutputObjectKey(projectId, modelId, experimentId, experimentResultId);
//                    final var experimentResultImageKeys = s3Service.listFileNamesInDirectory(experimentResultKey + "snapshot/");
//                    if (experimentResultImageKeys.isEmpty()) {
//                        log.error("No image found in S3 for experiment result: {}", experimentResultKey);
//                        updateExperimentResultStatus(experimentResultStatus, ExperimentResultStatusConst.FAILED);
//                        return;
//                    }

//                    saveResultS3(experimentResultImageKeys, experiment, experimentResultKey, finalStep, experimentResultStatus);

                    // Sleep for 1 second to make sure all images are loaded.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        log.error("Error while sleeping", e);
                    }
                    final var imageList = imageService.listImagesInDirectory(localOutputDir + "/snapshot");
                    saveResult(experiment, experimentResultStatus, imageList, localOutputDir, finalStep);
                });

                future.join();
            });
        } catch (Exception e) {
            log.error("Error while executing legacy command: ", e);
        }
    }

    /**
     * This method is used to clear local resource.
     *
     * @param dir String
     */
    private void clearLocalResource(String dir) {
        // Clear old output directory
        final var processBuilder = new ProcessBuilder();

        log.info("Start clear old output directory: {}", dir);

        try {
            final var process = processBuilder.command("bash", "-c", "rm -rf " + dir + "*").start();

            log.info("Successfully clear old output directory: {}", dir);
        } catch (Exception e) {
            throw new CannotClearOldSimulationOutputException(e.getMessage());
        }
    }

    /**
     * This method is used to save result after simulation.
     *
     * @param experiment Experiment
     * @param experimentResultStatus ExperimentResultStatus
     * @param imageList List<List<String>>
     * @param experimentResultLocation String
     * @param finalStep long
     */
    private void saveResult(Experiment experiment, ExperimentResultStatus experimentResultStatus, List<List<String>> imageList,
        String experimentResultLocation, long finalStep) {
        // Delete old experiment result.
        experimentResultRepository.deleteByExperimentId(experiment.getId());

        // Save new experiment result.
        final var experimentResult = experimentResultRepository.save(ExperimentResult.builder()
            .experiment(experiment)
            .location(experimentResultLocation)
            .finalStep((int) finalStep)
            .build());

        long start = timeUtil.getCurrentTimeNano();
        // Save new experiment result images.
        final List<ExperimentResultImage> experimentResultImageList = new ArrayList<>();
        final var imageCategoryList = imageList.stream().map(image -> image.get(2)).distinct().toList();
        final var experimentResultCategoryList = imageCategoryList.stream().map(imageCategory -> ExperimentResultCategory.builder()
            .name(imageCategory)
            .experimentResult(experimentResult)
            .build()).toList();

        final var experimentResultCategories = experimentResultCategoryRepository.saveAll(experimentResultCategoryList);

        imageList.forEach(image -> {
            final var imageName = image.getFirst();
            final var imageExtension = image.get(1);
            final var imageCategory = image.get(2);
            final var imageStep = !image.get(3).isEmpty() ? convertUtil.convertStringToInteger(image.get(3)) : null;
            final var experimentResultCategory = experimentResultCategories.stream()
                .filter(category -> category.getName().equals(imageCategory))
                .findFirst()
                .orElse(null);

            experimentResultImageList.add(ExperimentResultImage.builder()
                .name(imageName)
                .location(experimentResultLocation + "/snapshot/" + imageName)
                .extension(imageExtension)
                .experimentResult(experimentResult)
                .experimentResultCategory(experimentResultCategory)
                .step(imageStep)
                .build());
        });
        experimentResultImageRepository.saveAll(experimentResultImageList);

        long end = timeUtil.getCurrentTimeNano();
        log.info("Save experiment result images took: {} ns", end - start);

        // Update experiment result status.
        updateExperimentResultStatus(experimentResultStatus, ExperimentResultStatusConst.DONE);
    }

    /**
     * This method is used to create experiment result status.
     *
     * @param experimentId BigInteger
     */
    private ExperimentResultStatus createExperimentResultStatus(BigInteger experimentId) {
        final var experiment = experimentRepository.findById(experimentId).orElse(null);
        if (experiment == null) {
            return null;
        }

        experimentResultStatusRepository.deleteByExperimentId(experimentId);

        return experimentResultStatusRepository.save(ExperimentResultStatus.builder()
                .experiment(experiment)
                .status(ExperimentResultStatusConst.IN_PROGRESS)
                .build());
    }

    /**
     * This method is used to update experiment result status.
     *
     * @param experimentResultStatus ExperimentResultStatus
     * @param status int
     */
    private void updateExperimentResultStatus(ExperimentResultStatus experimentResultStatus, int status) {
        if (experimentResultStatus == null) {
            return;
        }

        experimentResultStatus.setStatus(status);
        experimentResultStatusRepository.save(experimentResultStatus);
    }

    /**
     * This method is used to save result after simulation.
     *
     * @param experimentResultImageKeys List<String>
     * @param experiment Experiment
     * @param experimentResultKey String
     * @param finalStep long
     * @param experimentResultStatus ExperimentResultStatus
     */
    private void saveResultS3(List<String> experimentResultImageKeys, Experiment experiment, String experimentResultKey,
            long finalStep, ExperimentResultStatus experimentResultStatus) {
        // Delete old experiment result.
        experimentResultRepository.deleteByExperimentId(experiment.getId());

        // Save new experiment result.
        final var experimentResult = experimentResultRepository.save(
                ExperimentResult.builder()
                        .experiment(experiment).location(s3Service.getS3PrefixUrl() + experimentResultKey)
                        .finalStep((int) finalStep).build());

        // Delete old images.
        experimentResultImageRepository.deleteByExperimentResultId(experimentResult.getId());

        long start = timeUtil.getCurrentTimeNano();
        // Save new experiment result images.
        final List<ExperimentResultImage> experimentResultImages = new ArrayList<>();
        experimentResultImageKeys.forEach(experimentResultImageKey -> {
            experimentResultImages.add(
                    ExperimentResultImage.builder().location(s3Service.getS3PrefixUrl() + experimentResultImageKey)
                            .experimentResult(experimentResult).build());
        });
        experimentResultImageRepository.saveAll(experimentResultImages);
        long end = timeUtil.getCurrentTimeNano();
        log.info("Save experiment result images took: {} ns", end - start);

        // Update experiment result status.
        updateExperimentResultStatus(experimentResultStatus, ExperimentResultStatusConst.DONE);
    }

    /**
     * This method is used to clear old S3 result.
     *
     * @param projectId BigInteger
     * @param modelId BigInteger
     * @param experimentId BigInteger
     * @param experimentResultId int
     */
    private void clearOldS3Result(BigInteger projectId, BigInteger modelId, BigInteger experimentId, int experimentResultId) {
        final var outputObjectKey = getOutputObjectKey(projectId, modelId, experimentId, experimentResultId);

        s3Service.clear(outputObjectKey);
    }

    /**
     * This method is used to upload result to S3.
     *
     * @param projectId BigInteger
     * @param modelId BigInteger
     * @param experimentId BigInteger
     * @param experimentResultId int
     * @param localOutputDir String
     */
    private void uploadResult(BigInteger projectId, BigInteger modelId, BigInteger experimentId, int experimentResultId, String localOutputDir) {
        log.info("Start get S3 object key");
        final var outputObjectKey = getOutputObjectKey(projectId, modelId, experimentId, experimentResultId);
        log.info("End get S3 object key: {}", outputObjectKey);

        log.info("Start upload directory to S3");
        s3Service.uploadDirectory(localOutputDir, outputObjectKey);
        log.info("End upload directory to S3");
    }

    /**
     * This method is used to get output object key.
     *
     * @param projectId BigInteger
     * @param modelId BigInteger
     * @param experimentId BigInteger
     * @param experimentResultId int
     * @return String
     */
    private String getOutputObjectKey(BigInteger projectId, BigInteger modelId, BigInteger experimentId, int experimentResultId) {
        final var userId = authService.getCurrentUserId();

        return String.format("simulation_results/user_%s/project_%s/model_%s/experiment_%s/result_%s/",
                projectId, userId, modelId, experimentId, experimentResultId);
    }
}
