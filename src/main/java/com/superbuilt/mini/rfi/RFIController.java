package com.superbuilt.mini.rfi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/rfis")
public class RFIController {

    private final RFIService rfiService;

    public RFIController(RFIService rfiService) {
        this.rfiService = rfiService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RFI createRFI(
            @PathVariable Long projectId,
            @RequestBody RFI rfi
    ) {
        return rfiService.createRFI(projectId, rfi);
    }

    @GetMapping
    public List<RFI> getRFIsByProject(
            @PathVariable Long projectId
    ) {
        return rfiService.getRFIsByProject(projectId);
    }

    @GetMapping("/open")
    public List<RFI> getOpenRFIs(
            @PathVariable Long projectId
    ) {
        return rfiService.getOpenRFIs(projectId);
    }

    @GetMapping("/{rfiId}")
    public RFI getRFIById(
            @PathVariable Long rfiId
    ) {
        return rfiService.getRFIById(rfiId);
    }
}