package com.uet.agent_simulation_api.controllers;

import com.uet.agent_simulation_api.responses.ResponseHandler;
import com.uet.agent_simulation_api.responses.SuccessResponse;
import com.uet.agent_simulation_api.services.node.INodeService;
import com.uet.agent_simulation_api.utils.FileUtil;
import com.uet.agent_simulation_api.utils.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final FileUtil fileUtil;
    private final ThreadUtil threadUtil;
    private final INodeService nodeService;
    private final ResponseHandler ResponseHandler;

    @Value("${server.port}")
    private int port;

    @Value("${spring.profiles.active}")
    private String profile;

    @GetMapping("/health")
    public ResponseEntity<SuccessResponse> health() {
        return ResponseHandler.respondSuccess("Ok");
    }

    @GetMapping("/current_node")
    public ResponseEntity<SuccessResponse> getCurrentNode() {
        return ResponseHandler.respondSuccess(nodeService.getCurrentNode());
    }

//    @GetMapping("/webclient_test")
//    public ResponseEntity<SuccessResponse> webClientTest() {
//        final var httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(3));
//        final var webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
//                .baseUrl("http://localhost:9998").build();
//
//        try {
//            String res = webClient.get()
//                    .uri("/api/v1/health")
//                    .retrieve()
//                    .bodyToMono(String.class)
//                    .block();
//
//            return ResponseHandler.respondSuccess(res);
//        } catch (Exception e) {
//            log.error("Error: {}", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    @GetMapping("zip_folder")
//    public ResponseEntity<SuccessResponse> zipFolder() {
//        fileUtil.zipFolderAsync("storage/outputs/node-1_user-1_project-1_model-19_experiment-6_result-1_simulator-06-Transmit");
//        return ResponseHandler.respondSuccess("Ok");
//    }

    @DeleteMapping("/threads/{id}")
    public ResponseEntity<SuccessResponse> killThread(@PathVariable long id) {
        threadUtil.showAll();
        threadUtil.killThread(id);

        return ResponseHandler.respondSuccess("OK");
    }

    @GetMapping("/threads")
    public ResponseEntity<SuccessResponse> showAllThreads() {
        threadUtil.showAll();

        return ResponseHandler.respondSuccess("OK");
    }

    @DeleteMapping("/processes/{pid}")
    public ResponseEntity<SuccessResponse> killProcess(@PathVariable long pid) {
        threadUtil.killProcessById(pid);

        return ResponseHandler.respondSuccess("OK");
    }
}
