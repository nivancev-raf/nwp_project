package rs.raf.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.raf.backend.dto.ErrorMessageDTO;
import rs.raf.backend.model.ErrorMessage;
import rs.raf.backend.services.ErrorMessageService;

@RestController
@RequestMapping("/api/errors")
public class ErrorMessageController {

    private final ErrorMessageService errorMessageService;

    public ErrorMessageController(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }


    @GetMapping
    public Page<ErrorMessageDTO> getErrors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return errorMessageService.getErrors(PageRequest.of(page, size));
    }
}
