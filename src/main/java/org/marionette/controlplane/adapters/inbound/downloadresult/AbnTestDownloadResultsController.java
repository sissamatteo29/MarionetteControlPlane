package org.marionette.controlplane.adapters.inbound.downloadresult;

import org.marionette.controlplane.adapters.inbound.downloadresult.dto.AbnTestResultsDTO;
import org.marionette.controlplane.usecases.inbound.downloadresult.AbnTestResultsDownloadUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/downloadresult")
public class AbnTestDownloadResultsController {

    private final AbnTestResultsDownloadUseCase testResultsDownloadUseCase;

    public AbnTestDownloadResultsController(AbnTestResultsDownloadUseCase testResultsDownloadUseCase) {
        this.testResultsDownloadUseCase = testResultsDownloadUseCase;
    }

    @GetMapping("/")
    public ResponseEntity<AbnTestResultsDTO> getAbnTestingResults() {

        AbnTestResultsDTO response = testResultsDownloadUseCase.execute().dto();

        return ResponseEntity.ok(response);

    }

}
